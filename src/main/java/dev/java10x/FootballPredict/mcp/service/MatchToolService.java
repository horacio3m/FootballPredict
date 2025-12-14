package dev.java10x.FootballPredict.mcp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import dev.java10x.FootballPredict.dto.MatchFilters;
import dev.java10x.FootballPredict.service.MatchService;
import reactor.core.publisher.Mono;

@Service
public class MatchToolService {

    private final MatchService matchService;

    public MatchToolService(MatchService matchService) {
        this.matchService = matchService;
    }

    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> getLastMatch(String team) {
        MatchFilters filters = new MatchFilters(team, null, null);
        
        return matchService.getMatches(filters)
                .map(response -> {
                    List<Map<String, Object>> matches = (List<Map<String, Object>>) response.get("matches");
                    
                    if (matches == null || matches.isEmpty()) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("error", "No matches found for team: " + team);
                        return result;
                    }
                    
                    matches.sort((m1, m2) -> {
                        String date1 = (String) m1.get("utcDate");
                        String date2 = (String) m2.get("utcDate");
                        if (date1 == null || date2 == null) return 0;
                        return date2.compareTo(date1);
                    });
                    
                    Map<String, Object> lastMatch = matches.get(0);
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("date", lastMatch.get("utcDate"));
                    result.put("status", lastMatch.get("status"));
                    
                    Map<String, Object> homeTeam = (Map<String, Object>) lastMatch.get("homeTeam");
                    Map<String, Object> awayTeam = (Map<String, Object>) lastMatch.get("awayTeam");
                    
                    if (homeTeam != null) {
                        result.put("homeTeam", homeTeam.get("name"));
                    }
                    if (awayTeam != null) {
                        result.put("awayTeam", awayTeam.get("name"));
                    }
                    
                    Map<String, Object> score = (Map<String, Object>) lastMatch.get("score");
                    if (score != null) {
                        Map<String, Object> fullTime = (Map<String, Object>) score.get("fullTime");
                        if (fullTime != null) {
                            result.put("homeScore", fullTime.get("home"));
                            result.put("awayScore", fullTime.get("away"));
                        }
                    }
                    
                    return result;
                });
    }

    public Mono<Map<String, Object>> getMatches(String team, String dateFrom, String dateTo) {
        MatchFilters filters = new MatchFilters(team, dateFrom, dateTo);
        return matchService.getMatches(filters);
    }
}

