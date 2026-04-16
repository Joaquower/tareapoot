package com.example.brokermessagebe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments_retry_jobs")
public class PaymentRetryJob extends BaseRetryJob {
}
