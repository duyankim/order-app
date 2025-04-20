package com.example.PaymentService.entity;

import com.example.PaymentService.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(indexes = {
        @Index(name = "idx_userId", columnList = "userId")
})
@NoArgsConstructor
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long userId;

    private Long orderId;

    private Long amountKRW;

    // 연관관계 없이, FK ID 저장 필드
    @Column(name = "payment_method_id")
    private Long paymentMethod;

    private String paymentData; // 실제로는 결제수단별로 다른 테이블 생성해서 data는 그 테이블로 넣어야 함

    private PaymentStatus paymentStatus;

    @Column(unique = true)
    private Long referenceCode;

    @Builder
    private Payment(Long userId, Long orderId, Long amountKRW, Long paymentMethod, String paymentData,
                    PaymentStatus paymentStatus, Long referenceCode) {
        this.userId = userId;
        this.orderId = orderId;
        this.amountKRW = amountKRW;
        this.paymentMethod = paymentMethod;
        this.paymentData = paymentData;
        this.paymentStatus = paymentStatus;
        this.referenceCode = referenceCode;
    }
}
