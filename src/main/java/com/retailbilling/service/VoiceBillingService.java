package com.retailbilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VoiceBillingService {

    private final NvidiaAiService nvidiaAiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VoiceBillingService(NvidiaAiService nvidiaAiService) {
        this.nvidiaAiService = nvidiaAiService;
    }

    public Map<String, Object> parseVoiceToBill(String speechText, String language) {
        if (speechText == null || speechText.trim().isEmpty()) {
            return Map.of("parsedItems", Collections.emptyList(), "audioMessage", "No speech detected.");
        }

        String systemPrompt = """
            Extract items, quantities, and units into JSON ONLY.
            Format:
            {"items":[{"name":"Product Name","quantity":1,"unit":"pack"}],"confirmationText":"Added"}
            Return ONLY raw JSON.
            """;

        String userPrompt = "Language: " + language + " | Text: " + speechText;

        try {
            String rawResponse = nvidiaAiService.askNvidiaAiWithSystemPrompt(systemPrompt, userPrompt);
            
            // --- ADD LOG TO SEE NVIDIA RESPONSE ---
            System.out.println("-> Nvidia AI Raw Output: " + rawResponse);

            if (rawResponse != null) {
                // --- REPLACE JSON CLEANING WITH INDEX EXTRACTION HERE ---
                int startIndex = rawResponse.indexOf("{");
                int endIndex = rawResponse.lastIndexOf("}");

                if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                    String jsonContent = rawResponse.substring(startIndex, endIndex + 1);
                    Map<String, Object> parsedMap = objectMapper.readValue(jsonContent, Map.class);

                    if (parsedMap.containsKey("items")) {
                        return Map.of(
                            "parsedItems", parsedMap.get("items"),
                            "audioMessage", parsedMap.getOrDefault("confirmationText", "Added to cart.")
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("AI Parsing Exception: " + e.getMessage());
        }
        // --- INSTANT LOCAL FALLBACK ---
        // If NVIDIA API is overloaded (503), add the item directly using raw spoken text!
        Map<String, Object> fallbackItem = Map.of(
            "name", speechText,
            "quantity", 1,
            "unit", "pack"
        );

        return Map.of(
            "parsedItems", List.of(fallbackItem),
            "audioMessage", "Added " + speechText + " to cart."
        );
    }
}