package com.example.OrderService.dto;

public record ProductOrderDetailDto(
        Long orderId,
        Long userId,
        Long productId,
        Long count,
        String orderStatus,
        Long paymentId,
        Long deliveryId,
        String paymentStatus,
        String deliveryStatus
) { }
