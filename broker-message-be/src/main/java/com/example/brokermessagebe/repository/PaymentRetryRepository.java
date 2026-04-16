package com.example.brokermessagebe.repository;

import com.example.brokermessagebe.model.PaymentRetryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRetryRepository extends JpaRepository<PaymentRetryJob, Long> {
    List<PaymentRetryJob> findByStatus(String status);
}
