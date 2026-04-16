package com.example.brokermessagebe.chain;

import com.example.brokermessagebe.model.BaseRetryJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StepCHandler extends AbstractRetryHandler {
    private static final Logger log = LoggerFactory.getLogger(StepCHandler.class);

    @Override
    public void handle(BaseRetryJob job) throws Exception {
        log.info("Executing STEP C (Final Update) for job id: {}", job.getId());
        job.setStepCStatus("SUCCESS");
        job.setStatus("SUCCESS");
        log.info("STEP C SUCCESS for job id: {}", job.getId());
    }
}
