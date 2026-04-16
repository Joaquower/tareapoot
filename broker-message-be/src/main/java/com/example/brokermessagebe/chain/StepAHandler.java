package com.example.brokermessagebe.chain;

import com.example.brokermessagebe.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class StepAHandler extends AbstractRetryHandler {
    private static final Logger log = LoggerFactory.getLogger(StepAHandler.class);
    private final RestTemplate restTemplate;
    private final String paymentEndpoint;
    private final String orderEndpoint;
    private final String productEndpoint;

    public StepAHandler(RestTemplate restTemplate, String paymentEndpoint, String orderEndpoint, String productEndpoint) {
        this.restTemplate = restTemplate;
        this.paymentEndpoint = paymentEndpoint;
        this.orderEndpoint = orderEndpoint;
        this.productEndpoint = productEndpoint;
    }

    @Override
    public void handle(BaseRetryJob job) throws Exception {
        log.info("Executing STEP A (Retry Creation) for job id: {}", job.getId());
        String url = "";
        if (job instanceof PaymentRetryJob) url = paymentEndpoint;
        else if (job instanceof OrderRetryJob) url = orderEndpoint;
        else if (job instanceof ProductRetryJob) url = productEndpoint;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Retry", "true"); // Add header to avoid recursion
            
            HttpEntity<String> request = new HttpEntity<>(job.getData(), headers);
            
            restTemplate.postForEntity(url, request, String.class);
            
            job.setStepAStatus("SUCCESS");
            log.info("STEP A SUCCESS for job id: {}", job.getId());
            next(job);
        } catch (Exception e) {
            job.setStepAStatus("FAILED");
            job.setLastErrorMessage("Step A failed: " + e.getMessage());
            throw e;
        }
    }
}
