package com.example.brokermessagebe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "retry_jobs")
public class RetryJobRecord extends BaseRetryJob {
    private String jobType;

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
}
