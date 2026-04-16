package com.example.brokermessagebe.scheduler;

import com.example.brokermessagebe.model.*;
import com.example.brokermessagebe.repository.*;
import com.example.brokermessagebe.service.RetryOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RetryScheduler {

    private static final Logger log = LoggerFactory.getLogger(RetryScheduler.class);
    private final PaymentRetryRepository paymentRetryRepository;
    private final OrderRetryRepository orderRetryRepository;
    private final ProductRetryRepository productRetryRepository;
    private final RetryOrchestrator orchestrator;

    public RetryScheduler(PaymentRetryRepository paymentRetryRepository,
                          OrderRetryRepository orderRetryRepository,
                          ProductRetryRepository productRetryRepository,
                          RetryOrchestrator orchestrator) {
        this.paymentRetryRepository = paymentRetryRepository;
        this.orderRetryRepository = orderRetryRepository;
        this.productRetryRepository = productRetryRepository;
        this.orchestrator = orchestrator;
    }

    @Scheduled(fixedRate = 10000)
    public void retryPayments() {
        List<PaymentRetryJob> jobs = paymentRetryRepository.findByStatus("PENDING");
        if (!jobs.isEmpty()) {
            log.info("Processing {} pending payment retries", jobs.size());
            jobs.forEach(job -> {
                orchestrator.executeRetry(job);
                paymentRetryRepository.save(job);
            });
        }
    }

    @Scheduled(fixedRate = 10000)
    public void retryOrders() {
        List<OrderRetryJob> jobs = orderRetryRepository.findByStatus("PENDING");
        if (!jobs.isEmpty()) {
            log.info("Processing {} pending order retries", jobs.size());
            jobs.forEach(job -> {
                orchestrator.executeRetry(job);
                orderRetryRepository.save(job);
            });
        }
    }

    @Scheduled(fixedRate = 10000)
    public void retryProducts() {
        List<ProductRetryJob> jobs = productRetryRepository.findByStatus("PENDING");
        if (!jobs.isEmpty()) {
            log.info("Processing {} pending product retries", jobs.size());
            jobs.forEach(job -> {
                orchestrator.executeRetry(job);
                productRetryRepository.save(job);
            });
        }
    }
}
