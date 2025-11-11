package com.jake.tradelogs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
//@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ConfigurationPropertiesScan
public class TradelogsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradelogsApplication.class, args);
    }

}
