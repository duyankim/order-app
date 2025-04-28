package com.example.DeliveryService.dg;

import com.example.DeliveryService.entity.Delivery;
import com.example.DeliveryService.enums.DeliveryStatus;
import com.example.DeliveryService.repository.DeliveryRepository;
import ecommerce.protobuf.EdaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeliveryStatusUpdater {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @Scheduled(fixedDelay = 10000)
    public void deliveryStatusUpdate() {

        // 배송중인 상태를 완료로 업데이트
        deliveryRepository.findAllByDeliveryStatus(DeliveryStatus.IN_DELIVERY)
                .forEach(delivery -> {
                    delivery.markAsCompleted();
                    deliveryRepository.save(delivery);

                    publishStatusChange(delivery);
                });

        // 요청된 상태를 배송중으로 업데이트
        deliveryRepository.findAllByDeliveryStatus(DeliveryStatus.REQUESTED)
                .forEach(delivery -> {
                    delivery.markAsInDelivery();
                    deliveryRepository.save(delivery);

                    publishStatusChange(delivery);
                });
    }

    private void publishStatusChange(Delivery delivery) {
        var deliveryStatusMessage = EdaMessage.DeliveryStatusUpdateV1.newBuilder()
                .setOrderId(delivery.getOrderId())
                .setDeliveryId(delivery.getId())
                .setDeliveryStatus(delivery.getDeliveryStatus().toString())
                .build();

        kafkaTemplate.send("delivery_status_update", deliveryStatusMessage.toByteArray());
    }
}
