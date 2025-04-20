package com.example.DeliveryService.dto;

public record RegisterAddressDto(
        Long userId,
        String address,
        String alias
) {
}
