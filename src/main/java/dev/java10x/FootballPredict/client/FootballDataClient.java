package dev.java10x.FootballPredict.client;

import dev.java10x.FootballPredict.dto.MatchFilters;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

@Component
public class FootballDataClient {

    private final WebClient webClient;

    public FootballDataClient(WebClient footballDataWebClient) {
        this.webClient = footballDataWebClient;
    }

    public Mono<Map<String, Object>> getMatches() {
        return getMatches(null);
    }

    public Mono<Map<String, Object>> getMatches(MatchFilters filters) {
        Function<UriBuilder, URI> uriFunction = builder -> {
            UriBuilder uriBuilder = builder.path("/matches");
            if (filters != null) {
                if (filters.hasDateFrom()) {
                    uriBuilder = uriBuilder.queryParam("dateFrom", filters.getDateFrom());
                }
                if (filters.hasDateTo()) {
                    uriBuilder = uriBuilder.queryParam("dateTo", filters.getDateTo());
                }
            }
            return uriBuilder.build();
        };

        return webClient.get()
                .uri(uriFunction)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> response.bodyToMono(String.class)
                        .flatMap(body -> {
                            System.err.println("API Error Response: " + body);
                            return Mono.error(new RuntimeException("API returned error: " + body));
                        }))
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnError(error -> {
                    System.err.println("Error calling Football Data API: " + error.getClass().getSimpleName() + " - " + error.getMessage());
                    if (error.getCause() != null) {
                        System.err.println("Caused by: " + error.getCause().getMessage());
                    }
                });
    }
}

