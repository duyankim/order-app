package com.example.PaymentService.service;

import com.example.PaymentService.entity.Payment;
import com.google.protobuf.InvalidProtocolBufferException;
import ecommerce.protobuf.EdaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @KafkaListener(topics = "payment_request")
    public void consumeTagAdded(byte[] message) throws Exception {
        var object = EdaMessage.PaymentRequestV1.parseFrom(message);

        logger.info("[payment_request] consumed: {}", object);
        Payment payment = paymentService.processPayment(
                object.getUserId(),
                object.getOrderId(),
                object.getAmountKRW(),
                object.getPaymentMethodId());

        var paymentResultMessage = EdaMessage.PaymentResultV1.newBuilder()
                .setOrderId(payment.getOrderId())
                .setPaymentId(payment.getId())
                .setPaymentStatus(payment.getPaymentStatus().toString())
                .build();

        kafkaTemplate.send("payment_result", paymentResultMessage.toByteArray());
    }
}
