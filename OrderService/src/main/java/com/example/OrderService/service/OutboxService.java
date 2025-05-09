package com.example.OrderService.service;

import com.example.OrderService.entity.OrderOutbox;
import com.example.OrderService.event.DomainEvent;
import com.example.OrderService.event.EnrichedDomainEvent;
import com.example.OrderService.repository.OrderOutboxRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OrderOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @EventListener
    public void handlePaymentRequestDomainEvent(EnrichedDomainEvent<? extends DomainEvent> enrichedDomainEvent) {
        outboxRepository.save(
                OrderOutbox.builder()
                        .aggregateType(enrichedDomainEvent.getAggregateType())
                        .aggregateId(enrichedDomainEvent.getAggregateId())
                        .eventType("payment_request")
                        .payload(objectMapper.convertValue(enrichedDomainEvent.getDomainEvent(), JsonNode.class))
                        .build()
        );
    }
}
