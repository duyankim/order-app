package com.example.OrderService.dto;

public record FinishOrderDto(
        Long orderId,
        Long paymentMethodId,
        Long addressId
) { }
