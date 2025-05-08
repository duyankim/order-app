package com.example.OrderService.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderFinishedEvent extends DomainEvent {
    private Long orderId;
    private Long userId;
    private Long productId;
    private Long count;
}