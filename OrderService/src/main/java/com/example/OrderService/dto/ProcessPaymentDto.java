package com.example.OrderService.dto;

public record ProcessPaymentDto(
    Long orderId,
    Long userId,
    Long accountKRW,
    Long paymentMethodId
) { }
