package com.retailbilling.service;

import com.retailbilling.entity.User;
import com.retailbilling.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Generate & Send OTP
    public Map<String, Object> sendOtp(String identifier) {
        User user = userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElse(null);

        if (user == null || user.getEmail() == null) {
            return Map.of("success", false, "message", "User with given Email/Username not found.");
        }

        // Generate 6-digit numeric OTP
        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);
        
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5)); // Valid for 5 minutes
        userRepository.save(user);

        // Send Email
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Retail Billing System - Password Reset OTP");
            mailMessage.setText("Your OTP for resetting your password is: " + otp + "\nThis OTP is valid for 5 minutes.");
            mailSender.send(mailMessage);

            return Map.of("success", true, "message", "OTP sent successfully to " + maskEmail(user.getEmail()));
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return Map.of("success", false, "message", "Failed to send OTP email.");
        }
    }

    // 2. Verify OTP & Reset Password
    public Map<String, Object> resetPassword(String identifier, String otp, String newPassword) {
        User user = userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElse(null);

        if (user == null) {
            return Map.of("success", false, "message", "User not found.");
        }

        // Check if OTP matches and is not expired
        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            return Map.of("success", false, "message", "Invalid OTP.");
        }

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return Map.of("success", false, "message", "OTP has expired. Please request a new one.");
        }

        // Update password & clear OTP fields
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return Map.of("success", true, "message", "Password reset successful!");
    }

    private String maskEmail(String email) {
        return email.replaceAll("(^[^@]{2})[^@]+", "$1****");
    }
}