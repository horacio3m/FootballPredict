package dev.java10x.FootballPredict.mcp.service;

import dev.java10x.FootballPredict.mcp.dto.MCPTool;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MCPToolRegistry {

    public List<MCPTool> getAvailableTools() {
        List<MCPTool> tools = new ArrayList<>();
        
        Map<String, Object> getLastMatchSchema = new HashMap<>();
        Map<String, Object> getLastMatchProperties = new HashMap<>();
        Map<String, Object> teamProperty = new HashMap<>();
        teamProperty.put("type", "string");
        teamProperty.put("description", "Name of the team");
        getLastMatchProperties.put("team", teamProperty);
        getLastMatchSchema.put("type", "object");
        getLastMatchSchema.put("properties", getLastMatchProperties);
        getLastMatchSchema.put("required", Arrays.asList("team"));
        
        tools.add(new MCPTool(
            "get_last_match",
            "Retorna o último jogo de um time, incluindo data e resultado",
            getLastMatchSchema
        ));
        
        Map<String, Object> getMatchesSchema = new HashMap<>();
        Map<String, Object> getMatchesProperties = new HashMap<>();
        Map<String, Object> teamProp = new HashMap<>();
        teamProp.put("type", "string");
        teamProp.put("description", "Name of the team (optional)");
        getMatchesProperties.put("team", teamProp);
        
        Map<String, Object> dateFromProp = new HashMap<>();
        dateFromProp.put("type", "string");
        dateFromProp.put("description", "Start date in YYYY-MM-DD format (optional)");
        getMatchesProperties.put("dateFrom", dateFromProp);
        
        Map<String, Object> dateToProp = new HashMap<>();
        dateToProp.put("type", "string");
        dateToProp.put("description", "End date in YYYY-MM-DD format (optional)");
        getMatchesProperties.put("dateTo", dateToProp);
        
        getMatchesSchema.put("type", "object");
        getMatchesSchema.put("properties", getMatchesProperties);
        getMatchesSchema.put("required", Collections.emptyList());
        
        tools.add(new MCPTool(
            "get_matches",
            "Retorna partidas filtradas por time e/ou intervalo de datas",
            getMatchesSchema
        ));
        
        return tools;
    }

    public String getToolsAsJson() {
        List<MCPTool> tools = getAvailableTools();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < tools.size(); i++) {
            MCPTool tool = tools.get(i);
            json.append("{");
            json.append("\"name\":\"").append(tool.getName()).append("\",");
            json.append("\"description\":\"").append(tool.getDescription()).append("\",");
            json.append("\"input_schema\":").append(mapToJson(tool.getInputSchema()));
            json.append("}");
            if (i < tools.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    @SuppressWarnings("unchecked")
    private String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) json.append(",");
            first = false;
            json.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else if (value instanceof List) {
                json.append(listToJson((List<?>) value));
            } else if (value instanceof Map) {
                json.append(mapToJson((Map<String, Object>) value));
            } else {
                json.append(value);
            }
        }
        json.append("}");
        return json.toString();
    }

    @SuppressWarnings("unchecked")
    private String listToJson(List<?> list) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            if (item instanceof String) {
                json.append("\"").append(item).append("\"");
            } else if (item instanceof Map) {
                json.append(mapToJson((Map<String, Object>) item));
            } else {
                json.append(item);
            }
            if (i < list.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
}

