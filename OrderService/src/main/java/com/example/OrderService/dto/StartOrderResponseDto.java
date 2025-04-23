package com.example.OrderService.dto;

import java.util.Map;

public record StartOrderResponseDto(
        Long orderId,
        Map<String, Object> paymentMethod,
        Map<String, Object> address
) { }
