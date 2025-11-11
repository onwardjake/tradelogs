package com.jake.tradelogs.client;

import com.jake.tradelogs.config.ApiPropsLawd;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class ApiClientLawd {
    private final ApiPropsLawd props;
    private final WebClient webClient;

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
                .defaultHeaders(h -> h.setAccept(List.of(MediaType.APPLICATION_JSON)))
                .filter(logRequest())
                .filter(logResponse())
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
}
