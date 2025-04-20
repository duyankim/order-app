package com.example.DeliveryService.dg;

import com.example.DeliveryService.enums.DeliveryStatus;
import com.example.DeliveryService.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeliveryStatusUpdater {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Scheduled(fixedDelay = 10000)
    public void deliveryStatusUpdate() {

        // 배송중인 상태를 완료로 업데이트
        deliveryRepository.findAllByDeliveryStatus(DeliveryStatus.IN_DELIVERY)
                .forEach(delivery -> {
                    delivery.markAsCompleted();
                    deliveryRepository.save(delivery);
                });

        // 요청된 상태를 배송중으로 업데이트
        deliveryRepository.findAllByDeliveryStatus(DeliveryStatus.REQUESTED)
                .forEach(delivery -> {
                    delivery.markAsInDelivery();
                    deliveryRepository.save(delivery);
                });
    }
}
