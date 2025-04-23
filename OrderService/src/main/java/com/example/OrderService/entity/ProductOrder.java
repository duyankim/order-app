package com.example.OrderService.entity;

import com.example.OrderService.enums.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long productId; // 단일 상품만 주문 가능하다고 가정

    private Long count;

    private OrderStatus orderStatus;

    private Long paymentId;

    private Long deliveryId;

    @Builder
    public ProductOrder(Long userId, Long productId, Long count, OrderStatus orderStatus, Long paymentId, Long deliveryId) {
        this.userId = userId;
        this.productId = productId;
        this.count = count;
        this.orderStatus = orderStatus;
        this.paymentId = paymentId;
        this.deliveryId = deliveryId;
    }
}
