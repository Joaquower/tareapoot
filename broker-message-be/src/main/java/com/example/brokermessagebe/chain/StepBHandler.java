package com.example.brokermessagebe.chain;

import com.example.brokermessagebe.model.BaseRetryJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class StepBHandler extends AbstractRetryHandler {
    private static final Logger log = LoggerFactory.getLogger(StepBHandler.class);
    private final JavaMailSender mailSender;

    public StepBHandler(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void handle(BaseRetryJob job) throws Exception {
        log.info("Executing STEP B (Success Email) for job id: {}", job.getId());
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo("user@example.com");
            mail.setSubject("Creation Success");
            mail.setText("The job " + job.getClass().getSimpleName() + " with ID " + job.getId() + " was created successfully on retry.");
            
            mailSender.send(mail);
            
            job.setStepBStatus("SUCCESS");
            log.info("STEP B SUCCESS for job id: {}", job.getId());
            next(job);
        } catch (Exception e) {
            job.setStepBStatus("FAILED");
            job.setLastErrorMessage("Step B failed: " + e.getMessage());
            throw e;
        }
    }
}
