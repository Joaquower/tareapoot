package com.example.brokermessagebe.service;

import com.example.brokermessagebe.chain.*;
import com.example.brokermessagebe.model.BaseRetryJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;

@Service
public class RetryOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(RetryOrchestrator.class);
    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${endpoint.payment}") private String paymentEndpoint;
    @Value("${endpoint.order}") private String orderEndpoint;
    @Value("${endpoint.product}") private String productEndpoint;

    private RetryHandler chain;

    public RetryOrchestrator(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostConstruct
    public void init() {
        StepAHandler stepA = new StepAHandler(restTemplate, paymentEndpoint, orderEndpoint, productEndpoint);
        StepBHandler stepB = new StepBHandler(mailSender);
        StepCHandler stepC = new StepCHandler();

        stepA.setNext(stepB);
        stepB.setNext(stepC);
        this.chain = stepA;
    }

    public void executeRetry(BaseRetryJob job) {
        try {
            job.setRetryCount(job.getRetryCount() + 1);
            job.setLastRetryAt(LocalDateTime.now());
            chain.handle(job);
        } catch (Exception e) {
            log.error("Retry failed for job {}: {}", job.getId(), e.getMessage());
            job.setStatus("FAILED");
            sendFailureEmail(job);
        }
    }

    private void sendFailureEmail(BaseRetryJob job) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo("user@example.com");
            mail.setSubject("Creation Failed - Retry " + job.getRetryCount());
            mail.setText("The job " + job.getClass().getSimpleName() + " with ID " + job.getId() + " failed again. Error: " + job.getLastErrorMessage());
            mailSender.send(mail);
        } catch (Exception e) {
            log.error("Failed to send failure email: {}", e.getMessage());
        }
    }
}
