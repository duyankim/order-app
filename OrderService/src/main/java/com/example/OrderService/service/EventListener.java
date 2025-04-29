package com.example.OrderService.service;

import com.example.OrderService.dto.DecreaseStockCountDto;
import com.example.OrderService.entity.ProductOrder;
import com.example.OrderService.enums.OrderStatus;
import com.example.OrderService.feign.CatalogClient;
import com.example.OrderService.repository.OrderRepository;
import ecommerce.protobuf.EdaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CatalogClient catalogClient;

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @KafkaListener(topics = "payment_result")
    public void consumePaymentResult(byte[] message) throws Exception {
        var object = EdaMessage.PaymentResultV1.parseFrom(message);

        logger.info("[payment_request] consumed: {}", object);

        // 결제 정보 업데이트
        ProductOrder order = orderRepository.findById(object.getOrderId())
                .orElseThrow();

        order.builder()
                .paymentId(object.getPaymentId())
                .orderStatus(OrderStatus.DELIVERY_REQUESTED)
                .build();

        orderRepository.save(order);

        Map<String, Object> product = catalogClient.getProduct(object.getOrderId());

        var deliveryRequestMessage = EdaMessage.DeliveryRequestV1.newBuilder()
                .setOrderId(order.getId())
                .setProductName(product.get("name").toString())
                .setProductCount(order.getCount())
                .setAddress(order.getDeliveryAddress())
                .build();

        kafkaTemplate.send("delivery_request", deliveryRequestMessage.toByteArray());
    }

    @KafkaListener(topics = "delivery_status_update")
    public void consumeDeliveryStatusUpdate(byte[] message) throws Exception {
        var object = EdaMessage.DeliveryStatusUpdateV1.parseFrom(message);

        logger.info("[delivery_status_update] consumed: {}", object);

        if ("REQUESTED".equals(object.getDeliveryStatus())) {
            // 상품 재고 감소
            ProductOrder order = orderRepository.findById(object.getOrderId())
                    .orElseThrow();

            DecreaseStockCountDto decreaseStockCountDto = new DecreaseStockCountDto(order.getCount());
            catalogClient.decreaseStockCount(order.getProductId(), decreaseStockCountDto);
        }
    }
}
