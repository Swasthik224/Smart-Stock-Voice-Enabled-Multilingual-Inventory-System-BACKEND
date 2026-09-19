package com.retailbilling.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class NvidiaAiService {

    @Value("${nvidia.api.key}")
    private String apiKey;

    @Value("${nvidia.api.url:https://integrate.api.nvidia.com/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public NvidiaAiService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(4000); // 4 sec timeout
        factory.setReadTimeout(4000);    // 4 sec timeout
        this.restTemplate = new RestTemplate(factory);
    }

    public String askNvidiaAiWithSystemPrompt(String systemPrompt, String userPrompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        // Switched to 8B model for sub-second responses and no 503 limits
        requestBody.put("model", "meta/llama-3.1-8b-instruct"); 
        requestBody.put("temperature", 0.1);
        requestBody.put("max_tokens", 150);

        List<Map<String, String>> messages = List.of(
            Map.of("role", "system", "content", systemPrompt),
            Map.of("role", "user", "content", userPrompt)
        );
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("choices")) {
                List<?> choices = (List<?>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            System.err.println("NVIDIA Request Failed: " + e.getMessage());
        }
        
        // Return valid empty JSON structure on failure instead of plain error text
        return "{}";
    }
}