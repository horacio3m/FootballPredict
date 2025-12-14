package dev.java10x.FootballPredict.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class FootballDataClient {

    private final WebClient webClient;

    public FootballDataClient(WebClient footballDataWebClient) {
        this.webClient = footballDataWebClient;
    }

    public Mono<Map<String, Object>> getMatches() {
        return webClient.get()
                .uri("/matches")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}

