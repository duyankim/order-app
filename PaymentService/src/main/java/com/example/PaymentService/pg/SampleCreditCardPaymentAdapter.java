package com.example.PaymentService.pg;

import org.springframework.stereotype.Service;

@Service
public class SampleCreditCardPaymentAdapter implements CreditCardPaymentAdapter{

    @Override
    public Long processCreditCardPayment(Long amountKRW, String creditCardNumber) {

        // 실제 결제 처리 로직을 구현합니다.
        return Math.round(Math.random() * 10000000);
    }
}
