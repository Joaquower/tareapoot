package com.example.brokermessagebe.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseRetryJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String data;

    private String stepAStatus = "PENDING";
    private String stepBStatus = "PENDING";
    private String stepCStatus = "PENDING";
    
    private String lastErrorMessage;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastRetryAt;
    private int retryCount = 0;
    private String status = "PENDING";

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public String getStepAStatus() { return stepAStatus; }
    public void setStepAStatus(String stepAStatus) { this.stepAStatus = stepAStatus; }
    public String getStepBStatus() { return stepBStatus; }
    public void setStepBStatus(String stepBStatus) { this.stepBStatus = stepBStatus; }
    public String getStepCStatus() { return stepCStatus; }
    public void setStepCStatus(String stepCStatus) { this.stepCStatus = stepCStatus; }
    public String getLastErrorMessage() { return lastErrorMessage; }
    public void setLastErrorMessage(String lastErrorMessage) { this.lastErrorMessage = lastErrorMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getLastRetryAt() { return lastRetryAt; }
    public void setLastRetryAt(LocalDateTime lastRetryAt) { this.lastRetryAt = lastRetryAt; }
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
