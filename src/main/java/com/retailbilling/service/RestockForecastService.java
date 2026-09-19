package com.retailbilling.service;

import com.retailbilling.dto.RestockForecastDto;
import com.retailbilling.entity.Product;
import com.retailbilling.repository.OrderRepository;
import com.retailbilling.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RestockForecastService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    public RestockForecastService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.restTemplate = new RestTemplate();
    }

    // DTO helper to read JSON response from FastAPI Python server
    private static class PythonPrediction {
        private Long productId;
        private Double predictedDailyVelocity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Double getPredictedDailyVelocity() { return predictedDailyVelocity; }
        public void setPredictedDailyVelocity(Double predictedDailyVelocity) { this.predictedDailyVelocity = predictedDailyVelocity; }
    }

    public List<RestockForecastDto> generateDemandForecast() {
        List<Product> products = productRepository.findAll();
        List<RestockForecastDto> forecasts = new ArrayList<>();
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // 1. Fetch ML predictions from FastAPI (http://localhost:8000/predict)
        Map<Long, Double> mlVelocityMap = new HashMap<>();
        try {
            PythonPrediction[] predictions = restTemplate.getForObject("http://localhost:8000/predict", PythonPrediction[].class);
            if (predictions != null) {
                for (PythonPrediction pred : predictions) {
                    mlVelocityMap.put(pred.getProductId(), pred.getPredictedDailyVelocity());
                }
            }
        } catch (Exception e) {
            System.err.println("Python ML Server offline or unreachable, falling back to OrderRepository history: " + e.getMessage());
        }

        // 2. Loop through products and build forecast DTOs
        for (Product p : products) {
            double dailyVelocity;

            // Use Python ML velocity if present; otherwise, fall back to SQL OrderRepository
            if (mlVelocityMap.containsKey(p.getId())) {
                dailyVelocity = mlVelocityMap.get(p.getId());
            } else {
                Long totalSold = orderRepository.getQuantitySoldSince(p.getId(), thirtyDaysAgo);
                double unitsSold = (totalSold != null) ? totalSold : 0;
                dailyVelocity = unitsSold / 30.0;
            }

            int currentStock = p.getStockQuantity() != null ? p.getStockQuantity() : 0;
            int daysRemaining = dailyVelocity > 0 ? (int) (currentStock / dailyVelocity) : 999;

            int targetStock = (int) Math.ceil(dailyVelocity * 14);
            int recommendedOrder = Math.max(0, targetStock - currentStock);

            forecasts.add(new RestockForecastDto(
                p.getId(),
                p.getName(),
                currentStock,
                Math.round(dailyVelocity * 100.0) / 100.0,
                daysRemaining,
                recommendedOrder
            ));
        }

        return forecasts;
    }
}