package com.example.OrderService.repository;

import com.example.OrderService.entity.OrderOutbox;
import com.example.OrderService.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderOutboxRepository extends JpaRepository<OrderOutbox, Long> {
    
}
