package com.example.PaymentService.dto;

public record ProcessPaymentDto(
        Long userId,
        Long orderId,
        Long amountKRW,
        Long paymentMethodId
) {
}
