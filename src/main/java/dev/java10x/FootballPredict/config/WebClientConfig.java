package dev.java10x.FootballPredict.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${football.data.api.base-url}")
    private String baseUrl;

    @Value("${football.data.api.token}")
    private String apiToken;

    @Bean
    public WebClient footballDataWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Auth-Token", apiToken)
                .build();
    }
}

