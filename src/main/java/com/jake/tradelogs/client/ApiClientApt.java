package com.jake.tradelogs.client;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jake.tradelogs.config.ApiPropsLawd;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

@Slf4j
@Component
public class ApiClientApt {
    private final ApiPropsLawd props;
    private final WebClient webClient;
    private final XmlMapper xmlMapper = new XmlMapper();

    // Open API 접속을 위한 WebClient 객체를 생성한다.
    public ApiClientApt(ApiPropsLawd props) {
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
                //.defaultHeaders(h -> h.setAccept(List.of(MediaType.APPLICATION_XML)))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .filter(logRequest())
                .filter(logResponse())
                .filter((request, next) -> {
                    log.info(">> {} {}", request.method(), request.url());
                    request.headers().forEach((k, v) -> log.debug(">> H {}: {}", k, v));
                    return next.exchange(request);
                })
                .build();
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
