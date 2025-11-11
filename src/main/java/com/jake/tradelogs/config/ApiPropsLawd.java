package com.jake.tradelogs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.jake.landtrade.config.lawd")
public record ApiPropsLawd(
    String baseUrl,
    String servicePath,
    String serviceKey,
    Integer defaultPageSize,

    // 타임아웃/재시도
    Integer connectTimeoutMills,        // ex. 3000
    Integer responseTimeoutMills,       // ex. 5000
    Integer retryMaxAttemps,            // ex. 3
    Long retryBackoffMills              // ex. 500
) {}
