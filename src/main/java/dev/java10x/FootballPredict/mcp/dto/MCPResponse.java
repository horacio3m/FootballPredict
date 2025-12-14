package dev.java10x.FootballPredict.mcp.dto;

import java.util.List;
import java.util.Map;

public class MCPResponse {
    private String response;
    private List<Map<String, Object>> toolResults;

    public MCPResponse() {
    }

    public MCPResponse(String response, List<Map<String, Object>> toolResults) {
        this.response = response;
        this.toolResults = toolResults;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<Map<String, Object>> getToolResults() {
        return toolResults;
    }

    public void setToolResults(List<Map<String, Object>> toolResults) {
        this.toolResults = toolResults;
    }
}

