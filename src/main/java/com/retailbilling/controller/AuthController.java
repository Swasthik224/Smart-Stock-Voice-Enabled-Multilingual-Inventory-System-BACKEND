package com.retailbilling.controller;

import com.retailbilling.dto.ForgotPasswordRequest;
import com.retailbilling.dto.ResetPasswordRequest;
import com.retailbilling.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@RequestBody ForgotPasswordRequest request) {
        Map<String, Object> response = authService.sendOtp(request.getIdentifier());
        boolean success = (boolean) response.get("success");
        if (success) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody ResetPasswordRequest request) {
        Map<String, Object> response = authService.resetPassword(
                request.getIdentifier(),
                request.getOtp(),
                request.getNewPassword()
        );
        boolean success = (boolean) response.get("success");
        if (success) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}