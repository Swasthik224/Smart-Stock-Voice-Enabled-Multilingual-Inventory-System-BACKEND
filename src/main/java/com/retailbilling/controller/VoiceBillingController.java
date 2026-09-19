package com.retailbilling.controller;

import com.retailbilling.dto.VoiceRequestDto;
import com.retailbilling.service.VoiceBillingService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/voice")
@CrossOrigin(origins = "http://localhost:5173")
public class VoiceBillingController {

    private final VoiceBillingService voiceBillingService;

    public VoiceBillingController(VoiceBillingService voiceBillingService) {
        this.voiceBillingService = voiceBillingService;
    }

    @PostMapping("/process")
    public Map<String, Object> processVoiceCommand(@RequestBody VoiceRequestDto request) {
        // --- ADD PRINT LOGS HERE ---
        System.out.println("=== CONTROLLER DEBUG ===");
        System.out.println("Raw Text: [" + request.getText() + "]");
        System.out.println("Language: [" + request.getLanguage() + "]");
        System.out.println("========================");

        return voiceBillingService.parseVoiceToBill(request.getText(), request.getLanguage());
    }
}