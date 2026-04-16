package com.example.brokermessagebe.repository;

import com.example.brokermessagebe.model.ProductRetryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRetryRepository extends JpaRepository<ProductRetryJob, Long> {
    List<ProductRetryJob> findByStatus(String status);
}
