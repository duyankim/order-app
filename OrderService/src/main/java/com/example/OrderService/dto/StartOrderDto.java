package com.example.OrderService.dto;

public record StartOrderDto(
        Long userId,
        Long productId,
        Long count
) { }
