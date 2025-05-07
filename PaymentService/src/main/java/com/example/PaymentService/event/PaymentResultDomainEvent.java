package com.example.PaymentService.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@Builder
public class PaymentResultDomainEvent extends DomainEvent {
    private String orderId;
    private String paymentId;
    private String paymentStatus;
}
