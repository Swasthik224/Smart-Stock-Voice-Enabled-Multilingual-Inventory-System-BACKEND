package com.retailbilling.controller;

import com.retailbilling.entity.Order;
import com.retailbilling.entity.OrderItem;
import com.retailbilling.entity.Product;
import com.retailbilling.repository.OrderRepository;
import com.retailbilling.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
@CrossOrigin(origins = "http://localhost:5173")
public class BillingController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public BillingController(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/history")
    public ResponseEntity<?> getBillingHistory(
            @RequestParam String start, 
            @RequestParam String end) {
        try {
            DateTimeFormatter flexibleFormatter = new DateTimeFormatterBuilder()
                    .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .optionalStart()
                    .appendOffsetId()
                    .toFormatter();

            LocalDateTime startTime = LocalDateTime.parse(start, flexibleFormatter);
            LocalDateTime endTime = LocalDateTime.parse(end, flexibleFormatter);

            List<Order> orders = orderRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startTime, endTime);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace(); 
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error parsing timeline query: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Order orderPayload) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 1. Build a fresh Order instance to isolate Hibernate tracking states
            Order dbOrder = new Order();
            dbOrder.setCustomerName(orderPayload.getCustomerName());
            dbOrder.setCustomerPhone(orderPayload.getCustomerPhone());
            dbOrder.setTotalAmount(orderPayload.getTotalAmount());
            dbOrder.setCreatedAt(LocalDateTime.now());

            List<OrderItem> managedItems = new ArrayList<>();

            if (orderPayload.getItems() != null) {
                for (OrderItem incomingItem : orderPayload.getItems()) {
                    if (incomingItem.getProduct() != null && incomingItem.getProduct().getId() != null) {
                        
                        // Fetch real entity from context
                        Product managedProduct = productRepository.findById(incomingItem.getProduct().getId())
                                .orElseThrow(() -> new RuntimeException("Product ID " + incomingItem.getProduct().getId() + " not found"));
                        
                        // Check stock restrictions
                        if (managedProduct.getStockQuantity() < incomingItem.getQuantity()) {
                            response.put("success", false);
                            response.put("message", "Insufficient stock availability for: " + managedProduct.getName());
                            return ResponseEntity.badRequest().body(response);
                        }
                        
                        // Deduct inventory stock levels safely
                        managedProduct.setStockQuantity(managedProduct.getStockQuantity() - incomingItem.getQuantity());
                        productRepository.save(managedProduct);
                        
                        // Build clean OrderItem mapping
                        OrderItem dbItem = new OrderItem();
                        dbItem.setOrder(dbOrder);
                        dbItem.setProduct(managedProduct);
                        dbItem.setQuantity(incomingItem.getQuantity());
                        dbItem.setPriceAtPurchase(managedProduct.getPrice());
                        
                        managedItems.add(dbItem);
                    }
                }
            }

            dbOrder.setItems(managedItems);

            // 2. Persist order graph
            Order savedOrder = orderRepository.save(dbOrder);

            // 3. Return clean confirmation JSON map matching React dashboard expectancies
            response.put("success", true);
            response.put("message", "Purchase finalized successfully");
            response.put("orderId", savedOrder.getId());
            response.put("totalAmount", savedOrder.getTotalAmount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Internal error saving purchase transaction: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}