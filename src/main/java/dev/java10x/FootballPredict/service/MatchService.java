package dev.java10x.FootballPredict.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import dev.java10x.FootballPredict.client.FootballDataClient;
import dev.java10x.FootballPredict.dto.MatchFilters;
import reactor.core.publisher.Mono;

@Service
public class MatchService {

    private final FootballDataClient footballDataClient;

    public MatchService(FootballDataClient footballDataClient) {
        this.footballDataClient = footballDataClient;
    }

    public Mono<Map<String, Object>> getMatches() {
        return getMatches(null);
    }

    public Mono<Map<String, Object>> getMatches(MatchFilters filters) {
        return footballDataClient.getMatches(filters)
                .map(response -> {
                    if (filters != null && filters.hasTeam()) {
                        return filterByTeam(response, filters.getTeam());
                    }
                    return response;
                });
    }

    private Map<String, Object> filterByTeam(Map<String, Object> response, String teamName) {
        String teamNameLower = teamName.toLowerCase().trim();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> matches = (List<Map<String, Object>>) response.get("matches");
        
        if (matches == null) {
            return response;
        }

        List<Map<String, Object>> filteredMatches = new ArrayList<>();
        
        for (Map<String, Object> match : matches) {
            @SuppressWarnings("unchecked")
            Map<String, Object> homeTeam = (Map<String, Object>) match.get("homeTeam");
            @SuppressWarnings("unchecked")
            Map<String, Object> awayTeam = (Map<String, Object>) match.get("awayTeam");
            
            boolean matchesTeam = false;
            
            if (homeTeam != null) {
                String homeTeamName = (String) homeTeam.get("name");
                if (homeTeamName != null && homeTeamName.toLowerCase().contains(teamNameLower)) {
                    matchesTeam = true;
                }
            }
            
            if (!matchesTeam && awayTeam != null) {
                String awayTeamName = (String) awayTeam.get("name");
                if (awayTeamName != null && awayTeamName.toLowerCase().contains(teamNameLower)) {
                    matchesTeam = true;
                }
            }
            
            if (matchesTeam) {
                filteredMatches.add(match);
            }
        }
        
        response.put("matches", filteredMatches);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultSet = (Map<String, Object>) response.get("resultSet");
        if (resultSet != null) {
            resultSet.put("count", filteredMatches.size());
        }
        
        return response;
    }
}

