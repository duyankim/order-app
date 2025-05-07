package com.example.PaymentService.service;

import com.example.PaymentService.entity.Payment;
import com.example.PaymentService.entity.PaymentMethod;
import com.example.PaymentService.enums.PaymentMethodType;
import com.example.PaymentService.enums.PaymentStatus;
import com.example.PaymentService.event.EnrichedDomainEvent;
import com.example.PaymentService.event.PaymentResultDomainEvent;
import com.example.PaymentService.pg.CreditCardPaymentAdapter;
import com.example.PaymentService.repository.PaymentMethodRepository;
import com.example.PaymentService.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CreditCardPaymentAdapter creditCardPaymentAdapter;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private OutboxService outboxService;

    public PaymentMethod registerPaymentMethod (
            Long userId,
            PaymentMethodType paymentMethodType,
            String creditCardNumber
    ){
        PaymentMethod paymentMethod = new PaymentMethod(userId, paymentMethodType, creditCardNumber);
        return paymentMethodRepository.save(paymentMethod);
    }

    @Transactional
    public Payment processPayment (
            Long userId,
            Long orderId,
            Long amountKRW,
            Long paymentMethodId
    ) throws Exception {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow();
        if (paymentMethod.getPaymentMethodType() != PaymentMethodType.CREDIT_CARD) {
            throw new Exception("Invalid payment method type");
        }

        // 실제 결제 진행
        Long referenceCode = creditCardPaymentAdapter.processCreditCardPayment(amountKRW, paymentMethod.getCreditCardNumber());

        // 결제 완료
        Payment payment = Payment.builder()
                .userId(userId)
                .orderId(orderId)
                .amountKRW(amountKRW)
                .paymentMethod(paymentMethod.getId())
                .paymentData(paymentMethod.getCreditCardNumber())
                .paymentStatus(PaymentStatus.COMPLETED)
                .referenceCode(referenceCode)
                .build();
        payment = paymentRepository.save(payment);

        outboxService.handlePaymentResultDomainEvent(
                EnrichedDomainEvent.builder()
                        .aggregateType("payment")
                        .aggregateId(payment.getOrderId().toString())
                        .domainEvent(
                                PaymentResultDomainEvent.builder()
                                        .orderId(payment.getOrderId().toString())
                                        .paymentId(payment.getId().toString())
                                        .paymentStatus(payment.getPaymentStatus().toString())
                                        .build()
                        ).build());

        return payment;
    }

    public PaymentMethod getPaymentMethodByUser(Long userId) {
        return paymentMethodRepository.findByUserId(userId)
                .stream().findFirst()
                .orElseThrow();
    }

    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow();
    }
}
