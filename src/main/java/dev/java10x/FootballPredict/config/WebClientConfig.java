package dev.java10x.FootballPredict.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Value("${football.data.api.base-url}")
    private String baseUrl;

    @Value("${football.data.api.token}")
    private String apiToken;

    @Bean
    public WebClient footballDataWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30))
                .followRedirect(true);

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Auth-Token", apiToken)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}

