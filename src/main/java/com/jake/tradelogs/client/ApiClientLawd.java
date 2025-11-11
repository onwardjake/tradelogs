package com.jake.tradelogs.client;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jake.tradelogs.config.ApiPropsLawd;
import com.jake.tradelogs.dto.lawd.ApiResponseLawd;
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
import reactor.netty.http.client.HttpClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ApiClientLawd {
    private final ApiPropsLawd props;
    private final WebClient webClient;
    private final XmlMapper xmlMapper = new XmlMapper();

    // Open API 접속을 위한 WebClient 객체를 생성한다.
    public ApiClientLawd(ApiPropsLawd props) {
        this.props = props;

        // http client 생성
        HttpClient httpClient = HttpClient.create()
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
                    log.info(">> {} {}", request.method(), request.url());
                    request.headers().forEach((k, v) -> log.debug(">> H {}: {}", k, v));
                    return next.exchange(request);
                })
                .build();
    }

    // Open API에 데이터를 요청해서 받는다.
    // 메소드 오버라이딩을 해서 type과 지역명을 입력하지 않은 경우에 대한 처리를 한다
    public ApiResponseLawd getLawdPage(Integer pageNo, Integer numOfRows){
        return getLawdPage(pageNo, numOfRows, "XML", "");
    }
    public ApiResponseLawd getLawdPage(Integer pageNo, Integer numOfRows, String type, String locataddNm) {

        String resRaw = webClient.get()
                .uri(uriBuilder -> {
                    var b = uriBuilder
                            .path(props.servicePath())
                            .queryParam("serviceKey", props.serviceKey())
                            .queryParam("pageNo", Optional.ofNullable(pageNo).orElse(1))
                            .queryParam("numOfRows", Optional.ofNullable(numOfRows).orElse(props.defaultPageSize()))
                            .queryParam("type", Optional.ofNullable(type).orElse("XML"));

                    // locataddNm이 빈 값이 아닌 경우 추가해준다
                    if(locataddNm != null && !locataddNm.isBlank()) {
                        b.queryParam("locatadd_nm", locataddNm);
                    }
                    
                    return b.build();
                })
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::map4xx)
                .onStatus(HttpStatusCode::is5xxServerError, this::map5xx)
                .bodyToMono(String.class)
                .block();

        try {
            // Jackson XmlMapper로 응답을 파싱한다
            return xmlMapper.readValue(resRaw, ApiResponseLawd.class);
        } catch (Exception e) {
            throw new RuntimeException("XML 파싱 실패: " + e.getMessage(), e);
        }
    }

    // 전체 법정동코드를 가지고 온다.
    public ApiResponseLawd getAllLawd() {
        int pageNo = 1;
        int numOfRows = props.defaultPageSize();
        String type = "xml";
        String locataddNm = "";

        // 첫번째 페이지 데이터를 요청해서 받아온다
        ApiResponseLawd first = getLawdPage(pageNo, numOfRows);
        log.info("first: {}", first);



        return first;
    }

    // WebClient에서 Request를 보낼 때 로그를 남긴다
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            log.info("WebClient Request: {} {}", req.method(), req.url());
            return Mono.just(req);
        });
    }

    // WebClient에서 Response를 받을 때 로그를 남긴다
    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(res -> {
            log.info("WebClient Response: status={}", res.statusCode());
            return Mono.just(res);
        });
    }

    // url을 인코딩한다.
    public static String urlEncode(String s) {
        // 이미 인코딩 되어 있다면 중복 인코딩을 하지 않는다.
        if(s.contains("%"))
            return s;

        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private <T> Mono<? extends Throwable> map4xx(ClientResponse resp) {
        return resp.bodyToMono(String.class)
                .defaultIfEmpty("")
                .map(body -> {
                    int code = resp.statusCode().value();
                    String msg = "OpenAPI 4xx 오류: " + code + " - " + summarize(body);
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
                    String msg = "OpenAPI 5xx 오류: " + code + " - " + summarize(body);
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
