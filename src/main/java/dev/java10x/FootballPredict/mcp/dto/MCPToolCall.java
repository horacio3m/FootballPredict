package dev.java10x.FootballPredict.mcp.dto;

import java.util.Map;

public class MCPToolCall {
    private String name;
    private Map<String, Object> arguments;

    public MCPToolCall() {
    }

    public MCPToolCall(String name, Map<String, Object> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }
}

