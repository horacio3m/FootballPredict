package dev.java10x.FootballPredict.mcp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatFunction;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;

import dev.java10x.FootballPredict.mcp.dto.GetLastMatchRequest;
import dev.java10x.FootballPredict.mcp.dto.GetMatchesRequest;
import dev.java10x.FootballPredict.mcp.dto.MCPRequest;
import dev.java10x.FootballPredict.mcp.dto.MCPResponse;
import dev.java10x.FootballPredict.mcp.dto.MCPTool;
import dev.java10x.FootballPredict.mcp.dto.MCPToolCall;
import dev.java10x.FootballPredict.mcp.service.MCPToolRegistry;
import dev.java10x.FootballPredict.mcp.service.MatchToolService;
import dev.java10x.FootballPredict.mcp.service.OpenAIService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/mcp")
public class MCPController {

    private final MCPToolRegistry toolRegistry;
    private final MatchToolService matchToolService;
    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper;

    public MCPController(MCPToolRegistry toolRegistry, MatchToolService matchToolService, OpenAIService openAIService) {
        this.toolRegistry = toolRegistry;
        this.matchToolService = matchToolService;
        this.openAIService = openAIService;
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping(value = "/tools", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MCPTool>> getTools() {
        return ResponseEntity.ok(toolRegistry.getAvailableTools());
    }

    @PostMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<MCPResponse>> query(@RequestBody MCPRequest request) {
        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
            return handleNaturalLanguageQuery(request.getQuery());
        } else if (request.getToolCalls() != null && !request.getToolCalls().isEmpty()) {
            return handleToolCalls(request.getToolCalls());
        } else {
            MCPResponse errorResponse = new MCPResponse();
            errorResponse.setResponse("Invalid request: either 'query' or 'toolCalls' must be provided");
            return Mono.just(ResponseEntity.badRequest().body(errorResponse));
        }
    }

    private Mono<ResponseEntity<MCPResponse>> handleNaturalLanguageQuery(String query) {
        try {
            List<ChatFunction> functions = createChatFunctions();
            
            ChatCompletionResult completionResult = openAIService.generateWithFunctions(query, functions);
            
            if (completionResult.getChoices() == null || completionResult.getChoices().isEmpty()) {
                MCPResponse errorResponse = new MCPResponse();
                errorResponse.setResponse("No response from OpenAI");
                return Mono.just(ResponseEntity.ok(errorResponse));
            }
            
            ChatMessage message = completionResult.getChoices().get(0).getMessage();
            ChatFunctionCall functionCall = message.getFunctionCall();
            
            if (functionCall != null) {
                String functionName = functionCall.getName();
                String functionArguments = functionCall.getArguments() != null 
                    ? functionCall.getArguments().toString() 
                    : "{}";
                
                return executeFunctionCall(functionName, functionArguments)
                        .flatMap(toolResult -> {
                            try {
                                List<ChatMessage> conversationHistory = new ArrayList<>();
                                ChatMessage systemMsg = new ChatMessage(ChatMessageRole.SYSTEM.value(), 
                                    "You are a helpful assistant that answers questions about football matches.");
                                conversationHistory.add(systemMsg);
                                ChatMessage userMsg = new ChatMessage(ChatMessageRole.USER.value(), query);
                                conversationHistory.add(userMsg);
                                
                                String finalResponse = openAIService.generateFinalResponse(
                                    conversationHistory,
                                    toolResult,
                                    functionName
                                );
                                MCPResponse response = new MCPResponse();
                                response.setResponse(finalResponse);
                                return Mono.just(ResponseEntity.ok(response));
                            } catch (Exception e) {
                                MCPResponse errorResponse = new MCPResponse();
                                errorResponse.setResponse("Error generating final response: " + e.getMessage());
                                return Mono.just(ResponseEntity.ok(errorResponse));
                            }
                        });
            } else {
                MCPResponse response = new MCPResponse();
                String content = message.getContent();
                response.setResponse(content != null ? content : "No content in response");
                return Mono.just(ResponseEntity.ok(response));
            }
        } catch (Exception e) {
            MCPResponse errorResponse = new MCPResponse();
            errorResponse.setResponse("Error processing query: " + e.getMessage() + " - " + e.getClass().getSimpleName());
            return Mono.just(ResponseEntity.ok(errorResponse));
        }
    }

    private List<ChatFunction> createChatFunctions() {
        List<ChatFunction> functions = new ArrayList<>();
        
        ChatFunction getLastMatch = ChatFunction.builder()
            .name("get_last_match")
            .description("Retorna o último jogo de um time, incluindo data e resultado")
            .executor(GetLastMatchRequest.class, (request) -> {
                try {
                    Map<String, Object> result = matchToolService.getLastMatch(request.getTeam()).block();
                    return objectMapper.writeValueAsString(result);
                } catch (Exception e) {
                    return "{\"error\": \"" + e.getMessage() + "\"}";
                }
            })
            .build();
        
        ChatFunction getMatches = ChatFunction.builder()
            .name("get_matches")
            .description("Retorna partidas filtradas por time e/ou intervalo de datas")
            .executor(GetMatchesRequest.class, (request) -> {
                try {
                    Map<String, Object> result = matchToolService.getMatches(
                        request.getTeam(), 
                        request.getDateFrom(), 
                        request.getDateTo()
                    ).block();
                    return objectMapper.writeValueAsString(result);
                } catch (Exception e) {
                    return "{\"error\": \"" + e.getMessage() + "\"}";
                }
            })
            .build();
        
        functions.add(getLastMatch);
        functions.add(getMatches);
        
        return functions;
    }

    private Mono<String> executeFunctionCall(String functionName, String functionArguments) {
        try {
            JsonNode arguments = objectMapper.readTree(functionArguments);
            
            if ("get_last_match".equals(functionName)) {
                String team = arguments.get("team").asText();
                return matchToolService.getLastMatch(team)
                        .map(result -> {
                            try {
                                return objectMapper.writeValueAsString(result);
                            } catch (Exception e) {
                                return "{\"error\": \"Failed to serialize result\"}";
                            }
                        });
            } else if ("get_matches".equals(functionName)) {
                String team = arguments.has("team") ? arguments.get("team").asText() : null;
                String dateFrom = arguments.has("dateFrom") ? arguments.get("dateFrom").asText() : null;
                String dateTo = arguments.has("dateTo") ? arguments.get("dateTo").asText() : null;
                return matchToolService.getMatches(team, dateFrom, dateTo)
                        .map(result -> {
                            try {
                                return objectMapper.writeValueAsString(result);
                            } catch (Exception e) {
                                return "{\"error\": \"Failed to serialize result\"}";
                            }
                        });
            }
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return Mono.just("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return Mono.just("{\"error\": \"" + e.getMessage() + "\"}");
        }
        
        return Mono.just("{\"error\": \"Unknown function\"}");
    }

    private Mono<ResponseEntity<MCPResponse>> handleToolCalls(List<MCPToolCall> toolCalls) {
        List<Mono<Map<String, Object>>> toolResults = new ArrayList<>();
        
        for (MCPToolCall toolCall : toolCalls) {
            String toolName = toolCall.getName();
            Map<String, Object> arguments = toolCall.getArguments();
            
            if ("get_last_match".equals(toolName)) {
                String team = (String) arguments.get("team");
                if (team != null) {
                    toolResults.add(matchToolService.getLastMatch(team));
                }
            } else if ("get_matches".equals(toolName)) {
                String team = (String) arguments.get("team");
                String dateFrom = (String) arguments.get("dateFrom");
                String dateTo = (String) arguments.get("dateTo");
                toolResults.add(matchToolService.getMatches(team, dateFrom, dateTo));
            }
        }
        
        return Mono.zip(toolResults, results -> {
            List<Map<String, Object>> toolResultsList = new ArrayList<>();
            for (Object result : results) {
                if (result instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> resultMap = (Map<String, Object>) result;
                    toolResultsList.add(resultMap);
                }
            }
            
            MCPResponse response = new MCPResponse();
            response.setResponse("Tool execution completed");
            response.setToolResults(toolResultsList);
            return ResponseEntity.ok(response);
        });
    }
}

