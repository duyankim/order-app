package com.example.PaymentService.scheduler;

import com.example.PaymentService.entity.PaymentOutbox;
import com.example.PaymentService.event.PaymentResultDomainEvent;
import com.example.PaymentService.repository.PaymentOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.protobuf.EdaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class OutboxMessageRelay {

    private static Logger logger = LoggerFactory.getLogger(OutboxMessageRelay.class);

    @Autowired
    private PaymentOutboxRepository paymentOutboxRepository;

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void publishOutboxMessages() {
        logger.info("Publishing outbox messages");
        List<PaymentOutbox> entities = paymentOutboxRepository.findAllByOrderByIdAsc(Pageable.ofSize(10)).toList();

        for (PaymentOutbox entity : entities) {
            try {
                // 1. JSON payload 도메인 이벤트 역직렬화
                PaymentResultDomainEvent event = objectMapper.treeToValue(
                        entity.getPayload(), PaymentResultDomainEvent.class);

                // 2. 도메인 이벤트 Protobuf 메시지 변환
                var paymentResultMessage = EdaMessage.PaymentResultV1.newBuilder()
                        .setOrderId(Long.parseLong(event.getOrderId()))
                        .setPaymentId(Long.parseLong(event.getPaymentId()))
                        .setPaymentStatus(event.getPaymentStatus())
                        .build();

                // 3. Kafka 전송
                kafkaTemplate.send("payment_result", paymentResultMessage.toByteArray());

                logger.info("Published payment_result: {}", paymentResultMessage);
            } catch (Exception e) {
                logger.error("Failed to publish outbox message: {}", entity, e);
            }
        }

        // 4. 발송 완료 후 삭제
        paymentOutboxRepository.deleteAllInBatch();
    }
}
