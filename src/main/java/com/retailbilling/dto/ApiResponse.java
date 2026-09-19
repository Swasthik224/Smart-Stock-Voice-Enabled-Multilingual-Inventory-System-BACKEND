package com.retailbilling.dto;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // Manual constructor that the Controller can actually see
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Standard Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    
    
}