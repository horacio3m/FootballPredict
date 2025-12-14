package dev.java10x.FootballPredict.mcp.dto;

import java.util.List;

public class MCPRequest {
    private String query;
    private List<MCPToolCall> toolCalls;

    public MCPRequest() {
    }

    public MCPRequest(String query, List<MCPToolCall> toolCalls) {
        this.query = query;
        this.toolCalls = toolCalls;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<MCPToolCall> getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(List<MCPToolCall> toolCalls) {
        this.toolCalls = toolCalls;
    }
}

