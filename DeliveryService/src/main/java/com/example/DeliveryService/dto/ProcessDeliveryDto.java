package com.example.DeliveryService.dto;

public record ProcessDeliveryDto(
        Long orderId,
        String productName,
        Long productCount,
        String address
) {
}
