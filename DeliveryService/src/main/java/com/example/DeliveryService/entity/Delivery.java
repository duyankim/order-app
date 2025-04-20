package com.example.DeliveryService.entity;

import com.example.DeliveryService.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(indexes = @Index(name = "idx_orderId", columnList = "orderId"))
@NoArgsConstructor
@Getter
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String productName;

    private Long productCount;

    private String address;

    private Long referenceCode; // 외부시스템과 연동시 사용할 수 있는 코드

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Builder
    public Delivery(Long orderId, String productName, Long productCount, String address, Long referenceCode, DeliveryStatus deliveryStatus) {
        this.orderId = orderId;
        this.productName = productName;
        this.productCount = productCount;
        this.address = address;
        this.referenceCode = referenceCode;
        this.deliveryStatus = deliveryStatus;
    }

    public void markAsCompleted() {
        this.deliveryStatus = DeliveryStatus.COMPLETED;
    }

    public void markAsInDelivery() {
        this.deliveryStatus = DeliveryStatus.IN_DELIVERY;
    }
}
