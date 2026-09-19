package com.retailbilling.service;

import com.retailbilling.entity.Order;
import com.retailbilling.entity.OrderItem;
import com.retailbilling.entity.Product;
import com.retailbilling.repository.OrderRepository;
import com.retailbilling.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BillingService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public BillingService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order processBulkPurchase(String customerName, String customerPhone, List<Map<String, Object>> items) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerPhone(customerPhone);
        
        List<OrderItem> orderItems = new ArrayList<>();
        double grandTotal = 0.0;

        for (Map<String, Object> itemMap : items) {
            Long productId = Long.valueOf(itemMap.get("productId").toString());
            Integer quantityPurchased = Integer.valueOf(itemMap.get("quantity").toString());

            // 1. Fetch Product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product ID " + productId + " not found"));

            // 2. Validate Stock
            if (product.getStockQuantity() < quantityPurchased) {
                throw new RuntimeException("Insufficient stock for '" + product.getName() + "'!");
            }

            // 3. Deduct Stock
            product.setStockQuantity(product.getStockQuantity() - quantityPurchased);
            productRepository.save(product);

            // 4. Record as Order Item line
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantityPurchased);
            orderItem.setPriceAtPurchase(product.getPrice());
            
            orderItems.add(orderItem);
            grandTotal += (product.getPrice() * quantityPurchased);
        }

        order.setItems(orderItems);
        order.setTotalAmount(grandTotal);

        // 5. Save the complete order and items
        return orderRepository.save(order);
    }
}