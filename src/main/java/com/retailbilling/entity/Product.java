package com.retailbilling.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Double price;

    // 1. UPDATE THIS LINE: Add name = "stock_quantity" to match your MySQL database schema exactly
    @Column(name = "stock_quantity", nullable = false)
    @JsonProperty("stockQuantity")
    private Integer stockQuantity; 

    // 2. UPDATE THIS LINE: Explicitly map the database column name to updated_at
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        // This is perfect! It will automatically write the current date & time 
        // whenever a product is created (saved) or modified (updated).
        updatedAt = LocalDateTime.now();
    }

    // --- MANUAL GETTERS & SETTERS TO FIX STS/LOMBOK BLINDNESS ---

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return this.stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}