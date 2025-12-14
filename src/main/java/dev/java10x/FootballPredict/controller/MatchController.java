package dev.java10x.FootballPredict.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.java10x.FootballPredict.dto.MatchFilters;
import dev.java10x.FootballPredict.service.MatchService;
import reactor.core.publisher.Mono;

@RestController
public class MatchController {

    private final MatchService matchService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping(value = "/matches", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> getMatches(
            @RequestParam(required = false) String team,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        
        MatchFilters filters = new MatchFilters(team, dateFrom, dateTo);
        
        ResponseEntity<?> validationError = validateDates(filters);
        if (validationError != null) {
            return Mono.just(validationError);
        }
        
        return matchService.getMatches(filters)
                .<ResponseEntity<?>>map(response -> {
                    if (response.containsKey("error")) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                    return ResponseEntity.ok(response);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private ResponseEntity<?> validateDates(MatchFilters filters) {
        if (filters.hasDateFrom()) {
            if (!isValidDate(filters.getDateFrom())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Invalid dateFrom format. Expected YYYY-MM-DD"));
            }
        }
        
        if (filters.hasDateTo()) {
            if (!isValidDate(filters.getDateTo())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Invalid dateTo format. Expected YYYY-MM-DD"));
            }
        }
        
        if (filters.hasDateFrom() && filters.hasDateTo()) {
            LocalDate from = LocalDate.parse(filters.getDateFrom(), DATE_FORMATTER);
            LocalDate to = LocalDate.parse(filters.getDateTo(), DATE_FORMATTER);
            
            if (from.isAfter(to)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("dateFrom must be before or equal to dateTo"));
            }
            
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(from, to);
            if (daysBetween > 30) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Date range cannot exceed 30 days. Please use a smaller date range."));
            }
        }
        
        return null;
    }

    private boolean isValidDate(String dateString) {
        try {
            LocalDate.parse(dateString, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}

