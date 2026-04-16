package com.example.brokermessagebe.repository;

import com.example.brokermessagebe.model.RetryJobRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetryJobRecordRepository extends JpaRepository<RetryJobRecord, Long> {
}
