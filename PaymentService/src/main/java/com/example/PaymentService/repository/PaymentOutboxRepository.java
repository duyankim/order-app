package com.example.PaymentService.repository;

import com.example.PaymentService.entity.PaymentOutbox;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface PaymentOutboxRepository extends JpaRepository<PaymentOutbox, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Page<PaymentOutbox> findAllByOrderByIdAsc(Pageable pageable);
}
