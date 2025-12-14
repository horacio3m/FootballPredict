package dev.java10x.FootballPredict.service;

import dev.java10x.FootballPredict.client.FootballDataClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class MatchService {

    private final FootballDataClient footballDataClient;

    public MatchService(FootballDataClient footballDataClient) {
        this.footballDataClient = footballDataClient;
    }

    public Mono<Map<String, Object>> getMatches() {
        return footballDataClient.getMatches();
    }
}

