package com.example.PaymentService.service;

import com.example.PaymentService.entity.PaymentOutbox;
import com.example.PaymentService.event.DomainEvent;
import com.example.PaymentService.event.EnrichedDomainEvent;
import com.example.PaymentService.repository.PaymentOutboxRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OutboxService {

    @Autowired
    private PaymentOutboxRepository outboxRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public void handlePaymentResultDomainEvent(EnrichedDomainEvent<? extends DomainEvent> enrichedDomainEvent) {
        outboxRepository.save(
                PaymentOutbox.builder()
                        .aggregateType(enrichedDomainEvent.getAggregateType())
                        .aggregateId(enrichedDomainEvent.getAggregateId())
                        .eventType("payment-result")
                        .payload(objectMapper.convertValue(enrichedDomainEvent.getDomainEvent(), JsonNode.class))
                        .build()
        );
    }
}
