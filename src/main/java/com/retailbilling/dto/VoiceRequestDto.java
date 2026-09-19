package com.retailbilling.dto;

public class VoiceRequestDto {
    private String text;
    private String language;

    public VoiceRequestDto() {}

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}