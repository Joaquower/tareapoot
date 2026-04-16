package com.example.brokermessagebe.chain;

import com.example.brokermessagebe.model.BaseRetryJob;

public interface RetryHandler {
    void setNext(RetryHandler next);
    void handle(BaseRetryJob job) throws Exception;
}
