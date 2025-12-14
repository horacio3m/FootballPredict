package dev.java10x.FootballPredict.dto;

public class MatchFilters {
    private String team;
    private String dateFrom;
    private String dateTo;

    public MatchFilters() {
    }

    public MatchFilters(String team, String dateFrom, String dateTo) {
        this.team = team;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public boolean hasTeam() {
        return team != null && !team.trim().isEmpty();
    }

    public boolean hasDateFrom() {
        return dateFrom != null && !dateFrom.trim().isEmpty();
    }

    public boolean hasDateTo() {
        return dateTo != null && !dateTo.trim().isEmpty();
    }
}

