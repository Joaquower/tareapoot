package com.example.productservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private boolean shouldFail = true;

    public ProductController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public String createProduct(@RequestBody Map<String, Object> payload, 
                              @RequestHeader(value = "X-Retry", required = false) String retryHeader) {
        log.info("Received request to create product (Retry: {}): {}", retryHeader, payload);
        
        if (shouldFail) {
            log.error("Product creation failed!");
            
            if (retryHeader == null || !retryHeader.equals("true")) {
                log.info("Sending to Kafka for first time retry.");
                Map<String, Object> retryPayload = new HashMap<>();
                retryPayload.put("data", payload);
                
                Map<String, String> emailStatus = new HashMap<>();
                emailStatus.put("status", "PENDING");
                emailStatus.put("message", "Awaiting retry");
                retryPayload.put("sendEmail", emailStatus);
                
                Map<String, String> updateStatus = new HashMap<>();
                updateStatus.put("status", "PENDING");
                updateStatus.put("message", "Awaiting retry");
                retryPayload.put("updateRetryJobs", updateStatus);
                
                String json = MapToJson(retryPayload);
                kafkaTemplate.send("product_retry_jobs", json);
            }
            
            throw new RuntimeException("Simulated Failure");
        }
        
        log.info("Product created successfully!");
        return "Product Created Successfully";
    }

    @PutMapping("/toggle")
    public String toggleFailure() {
        this.shouldFail = !this.shouldFail;
        return "Should fail: " + this.shouldFail;
    }

    private String MapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        map.forEach((k, v) -> {
            sb.append("\"").append(k).append("\":");
            if (v instanceof Map) {
                sb.append(MapToJson((Map<String, Object>)v));
            } else if (v instanceof Number || v instanceof Boolean) {
                sb.append(v);
            } else {
                sb.append("\"").append(v).append("\"");
            }
            sb.append(",");
        });
        if (sb.length() > 1) sb.setLength(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }
}
