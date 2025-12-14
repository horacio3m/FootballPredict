package dev.java10x.FootballPredict.mcp.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatFunction;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

@Service
public class OpenAIService {

    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    public OpenAIService(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
        this.objectMapper = new ObjectMapper();
    }

    public ChatCompletionResult generateWithFunctions(String userQuery, List<ChatFunction> functions) {
        List<ChatMessage> messages = new ArrayList<>();
        
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), 
            "You are a helpful assistant that answers questions about football matches. " +
            "Use the available functions to get information when needed.");
        messages.add(systemMessage);
        
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userQuery);
        messages.add(userMessage);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4")
                .messages(messages)
                .functions(functions)
                .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                .temperature(0.7)
                .maxTokens(1000)
                .build();

        return openAiService.createChatCompletion(request);
    }

    public String generateFinalResponse(List<ChatMessage> messages, String functionResult, String functionName) {
        try {
            ChatFunctionCall functionCall = new ChatFunctionCall();
            functionCall.setName(functionName);
            functionCall.setArguments(objectMapper.readTree("{}"));
            
            ChatMessage assistantMessage = new ChatMessage();
            assistantMessage.setRole(ChatMessageRole.ASSISTANT.value());
            assistantMessage.setFunctionCall(functionCall);
            messages.add(assistantMessage);
            
            ChatMessage functionMessage = new ChatMessage();
            functionMessage.setRole(ChatMessageRole.FUNCTION.value());
            functionMessage.setName(functionName);
            functionMessage.setContent(functionResult);
            messages.add(functionMessage);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-4")
                    .messages(messages)
                    .temperature(0.7)
                    .maxTokens(500)
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(request);
            ChatMessage responseMessage = result.getChoices().get(0).getMessage();
            String content = responseMessage.getContent();
            return content != null ? content : "No response content available";
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return "Error parsing JSON: " + e.getMessage();
        } catch (Exception e) {
            return "Error generating response: " + e.getMessage() + " - " + e.getClass().getSimpleName();
        }
    }
}

