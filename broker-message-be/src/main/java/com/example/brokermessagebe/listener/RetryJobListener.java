package com.example.brokermessagebe.listener;

import com.example.brokermessagebe.model.*;
import com.example.brokermessagebe.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RetryJobListener {

    private static final Logger log = LoggerFactory.getLogger(RetryJobListener.class);
    private final PaymentRetryRepository paymentRetryRepository;
    private final OrderRetryRepository orderRetryRepository;
    private final ProductRetryRepository productRetryRepository;
    private final RetryJobRecordRepository retryJobRecordRepository;
    private final ObjectMapper objectMapper;

    public RetryJobListener(PaymentRetryRepository paymentRetryRepository,
                            OrderRetryRepository orderRetryRepository,
                            ProductRetryRepository productRetryRepository,
                            RetryJobRecordRepository retryJobRecordRepository,
                            ObjectMapper objectMapper) {
        this.paymentRetryRepository = paymentRetryRepository;
        this.orderRetryRepository = orderRetryRepository;
        this.productRetryRepository = productRetryRepository;
        this.retryJobRecordRepository = retryJobRecordRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "payments_retry_jobs", groupId = "retry-group")
    public void listenPayments(String message) {
        log.info("Received payment retry message: {}", message);
        saveJob(message, new PaymentRetryJob(), "PAYMENT");
    }

    @KafkaListener(topics = "order_retry_jobs", groupId = "retry-group")
    public void listenOrders(String message) {
        log.info("Received order retry message: {}", message);
        saveJob(message, new OrderRetryJob(), "ORDER");
    }

    @KafkaListener(topics = "product_retry_jobs", groupId = "retry-group")
    public void listenProducts(String message) {
        log.info("Received product retry message: {}", message);
        saveJob(message, new ProductRetryJob(), "PRODUCT");
    }

    private void saveJob(String data, BaseRetryJob job, String type) {
        try {
            job.setData(data);
            job.setStatus("PENDING");
            
            if (job instanceof PaymentRetryJob) paymentRetryRepository.save((PaymentRetryJob) job);
            else if (job instanceof OrderRetryJob) orderRetryRepository.save((OrderRetryJob) job);
            else if (job instanceof ProductRetryJob) productRetryRepository.save((ProductRetryJob) job);

            // Also save to global record
            RetryJobRecord record = new RetryJobRecord();
            record.setData(data);
            record.setJobType(type);
            record.setStatus("PENDING");
            retryJobRecordRepository.save(record);
            
        } catch (Exception e) {
            log.error("Error saving retry job: {}", e.getMessage());
        }
    }
}
