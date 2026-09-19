package com.retailbilling.repository;

import com.retailbilling.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Fetches all orders between start and end dates, sorted from newest to oldest
    List<Order> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);

    // Calculates total quantity sold for a specific product since a given timestamp
    @Query("SELECT SUM(item.quantity) FROM Order o JOIN o.items item WHERE item.product.id = :productId AND o.createdAt >= :since")
    Long getQuantitySoldSince(@Param("productId") Long productId, @Param("since") LocalDateTime since);
}