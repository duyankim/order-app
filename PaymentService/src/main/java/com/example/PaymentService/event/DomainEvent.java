package com.example.PaymentService.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
public abstract class DomainEvent {
}
