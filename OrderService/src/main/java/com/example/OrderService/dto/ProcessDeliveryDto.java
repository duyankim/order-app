package com.example.OrderService.dto;

public record ProcessDeliveryDto(
    Long orderId,
    String productName,
    Long productCount,
    String address
) { }
