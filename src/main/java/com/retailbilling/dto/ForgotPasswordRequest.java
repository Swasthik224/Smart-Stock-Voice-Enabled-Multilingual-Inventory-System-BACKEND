package com.retailbilling.dto;

public class ForgotPasswordRequest {
    private String identifier; // Email or Phone Number

    public ForgotPasswordRequest() {}

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
}