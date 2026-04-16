package com.example.brokermessagebe.dto;

import lombok.Data;
import java.util.Map;

@Data
public class RetryPayload {
    private Map<String, Object> data;
    private StatusInfo sendEmail;
    private StatusInfo updateRetryJobs;

    @Data
    public static class StatusInfo {
        private String status;
        private String message;
    }
}
