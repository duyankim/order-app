package com.example.PaymentService.service;

import com.example.PaymentService.entity.Payment;
import com.example.PaymentService.event.EnrichedDomainEvent;
import com.example.PaymentService.event.PaymentResultDomainEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import ecommerce.protobuf.EdaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentService paymentService;

    @KafkaListener(topics = "payment_request")
    public void consumePaymentResult(byte[] message) throws Exception {
        var object = EdaMessage.PaymentRequestV1.parseFrom(message);

        logger.info("[payment_request] consumed: {}", object);
        Payment payment = paymentService.processPayment(
                object.getUserId(),
                object.getOrderId(),
                object.getAmountKRW(),
                object.getPaymentMethodId());
    }
}
