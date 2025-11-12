package com.jake.tradelogs.client;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jake.tradelogs.config.ApiPropsApt;
import com.jake.tradelogs.dto.apt.ApiResApt;
import com.jake.tradelogs.dto.apt.ApiResAptBody;
import com.jake.tradelogs.dto.apt.ApiResAptBodyItem;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ApiClientApt {
    private final ApiPropsApt props;
    private final WebClient webClient;
    private final XmlMapper xmlMapper = new XmlMapper();
    private final String clientName = "[아파트 실거래가]";

    // Open API 접속을 위한 WebClient 객체를 생성한다.
    public ApiClientApt(ApiPropsApt props) {
        this.props = props;

        // http client 생성
        HttpClient httpClient = HttpClient.create()
                .protocol(HttpProtocol.HTTP11)      // HTTP/1.1로 고정
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.connectTimeoutMills())
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(props.responseTimeoutMills())))
                .responseTimeout(Duration.ofMillis(props.responseTimeoutMills()));

        // webclient 생성
        // 반응형 컨트롤러(WebFlux)를 쓰지 않더라도, 외부 Open API 등을 호출하기 위해
        // 비동기 처리와 연결/응답 타임아웃 설정이 가능한 HTTP 클라이언트를 구성
        this.webClient = WebClient.builder()
                .baseUrl(props.baseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeaders(h -> h.setAccept(List.of(MediaType.APPLICATION_XML)))
                .filter(logRequest())
                .filter(logResponse())
                .filter((request, next) -> {
                    //log.info(">> {} WebClient request: {} {}", clientName, request.method(), request.url());
                    request.headers().forEach((k, v) -> log.debug(">> H {}: {}", k, v));
                    return next.exchange(request);
                })
                .build();
    }

    public List<ApiResAptBodyItem> getAllAptTrades() {
        int pageNo = 1;
        int numOfRows = props.defaultPageSize();
        List<ApiResAptBodyItem> all = new ArrayList<>();
        String lawdCd = "41430"; // 테스트용: 경기도 의왕시 포일동
        String dealYmd = "202510"; // 테스트용

        // LAWD를 가지고 와서 (DB 조회 또는 법정동코드 조회 API 호출)
        // LAWD를 iteration 하면서
        //   기간만큼 iteraion 하면서
        //     페이지 수 만큼 iteration 하면서
        //       데이터를 받아온다


        // 첫번째 페이지 데이터를 요청해서 받아온다
        ApiResApt first = getAptTradePage(lawdCd, dealYmd, pageNo, numOfRows);
        ApiResAptBody body = first.body();
        if(body.items() != null && body.items().item() != null) {
            all.addAll(body.items().item());
        }

        return all;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // method : getAptTradePage
    // description : Open API를 호출하여 특정 페이지의 데이터를 요청한다.
    // parameter : lawdCd - 법정동코드 5자리(ex. 11110), dealYmd - 거래년월(ex. 202510)
    //             pageNo - 요청 페이지 번호, numOfRows - 페이지당 항목 개수
    // return type : 요청 결과를 ApiResApt에 넣어서 리턴한다
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ApiResApt getAptTradePage(String lawdCd, String dealYmd, Integer pageNo, Integer numOfRows) {
        String resRaw = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(props.servicePath())
                        .queryParam("LAWD_CD", lawdCd)
                        .queryParam("DEAL_YMD", dealYmd)
                        .queryParam("serviceKey", props.serviceKey())
                        .queryParam("pageNo", Optional.ofNullable(pageNo).orElse(1))
                        .queryParam("numOfRows", Optional.ofNullable(numOfRows).orElse(props.defaultPageSize()))
                        .build(true))
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::map4xx)
                .onStatus(HttpStatusCode::is5xxServerError, this::map5xx)
                .bodyToMono(String.class)
                .onErrorResume(ex -> Mono.error(new RuntimeException(clientName + " OpenAPI 호출 실패: " + ex.getMessage(), ex)))
                .block();

        // 원문 XML 로그
        log.debug(">> {} RAW XML = {}", clientName, resRaw);

        try {
            // Jackson XmlMapper로 응답을 파싱한다
            ApiResApt response = xmlMapper.readValue(resRaw, ApiResApt.class);

            // 예외 처리
            if(response == null || response.header() == null) {
                throw new IllegalStateException(clientName + " 응답 파싱 실패 : 응답 내용이 없거나 header 없음");
            }
            if(response.body() == null) {
                throw new IllegalStateException(clientName + " 응답 파싱 실패: body 없음");
            }

            return response;
        } catch (Exception e) {
            log.info(">> {} Open API 요청 처리 중 exception 발생: {}", clientName, e.getMessage());
            throw new RuntimeException(clientName + " Open API 요청 처리 중 exception 발생: " + e.getMessage(), e);
        }
    }

    // WebClient에서 Request를 보낼 때 로그를 남긴다
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            log.info(">> [아파트 실거래가] WebClient Request: {} {}", req.method(), req.url());
            return Mono.just(req);
        });
    }

    // WebClient에서 Response를 받을 때 로그를 남긴다
    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(res -> {
            log.info(">> [아파트 실거래가] WebClient Response: status={}", res.statusCode());
            return Mono.just(res);
        });
    }

    private <T> Mono<? extends Throwable> map4xx(ClientResponse resp) {
        return resp.bodyToMono(String.class)
                .defaultIfEmpty("")
                .map(body -> {
                    int code = resp.statusCode().value();
                    String msg = ">> [아파트 실거래가] OpenAPI 4xx 오류: " + code + " - " + summarize(body);
                    log.warn(msg);
                    return new WebClientResponseException(
                            msg,
                            resp.statusCode().value(),
                            HttpStatus.resolve(code) != null ? HttpStatus.resolve(code).name() : "CLIENT_ERROR",
                            resp.headers().asHttpHeaders(),
                            body.getBytes(),
                            null);
                });
    }

    private <T> Mono<? extends Throwable> map5xx(ClientResponse resp) {
        return resp.bodyToMono(String.class)
                .defaultIfEmpty("")
                .map(body -> {
                    int code = resp.statusCode().value();
                    String msg = ">> [아파트 실거래가] OpenAPI 5xx 오류: " + code + " - " + summarize(body);
                    log.error(msg);
                    return new WebClientResponseException(
                            msg,
                            resp.statusCode().value(),
                            HttpStatus.resolve(code) != null ? HttpStatus.resolve(code).name() : "SERVER_ERROR",
                            resp.headers().asHttpHeaders(),
                            body.getBytes(),
                            null);
                });
    }

    private static String summarize(String body) {
        return body.length() > 500 ? body.substring(0, 500) + "..." : body;
    }
}
