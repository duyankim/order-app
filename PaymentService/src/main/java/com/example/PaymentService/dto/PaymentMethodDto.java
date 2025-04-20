package com.example.PaymentService.dto;

import com.example.PaymentService.enums.PaymentMethodType;

public record PaymentMethodDto(
        Long userId,
        PaymentMethodType paymentMethodType,
        String creditCardNumber
) {
}
