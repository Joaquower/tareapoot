package com.example.brokermessagebe.repository;

import com.example.brokermessagebe.model.OrderRetryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRetryRepository extends JpaRepository<OrderRetryJob, Long> {
    List<OrderRetryJob> findByStatus(String status);
}
