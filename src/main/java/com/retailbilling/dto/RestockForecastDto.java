package com.retailbilling.dto;

public class RestockForecastDto {

    private Long productId;
    private String productName;
    private Integer currentStock;
    private Double predictedDailyVelocity;
    private Integer daysRemaining;
    private Integer recommendedOrderQty;

    // Default Constructor
    public RestockForecastDto() {}

    // Parameterized Constructor
    public RestockForecastDto(Long productId, String productName, Integer currentStock, 
                              Double predictedDailyVelocity, Integer daysRemaining, Integer recommendedOrderQty) {
        this.productId = productId;
        this.productName = productName;
        this.currentStock = currentStock;
        this.predictedDailyVelocity = predictedDailyVelocity;
        this.daysRemaining = daysRemaining;
        this.recommendedOrderQty = recommendedOrderQty;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Double getPredictedDailyVelocity() {
        return predictedDailyVelocity;
    }

    public void setPredictedDailyVelocity(Double predictedDailyVelocity) {
        this.predictedDailyVelocity = predictedDailyVelocity;
    }

    public Integer getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Integer daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public Integer getRecommendedOrderQty() {
        return recommendedOrderQty;
    }

    public void setRecommendedOrderQty(Integer recommendedOrderQty) {
        this.recommendedOrderQty = recommendedOrderQty;
    }
}