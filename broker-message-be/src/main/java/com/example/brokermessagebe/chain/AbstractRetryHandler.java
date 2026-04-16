package com.example.brokermessagebe.chain;

import com.example.brokermessagebe.model.BaseRetryJob;

public abstract class AbstractRetryHandler implements RetryHandler {
    protected RetryHandler next;

    @Override
    public void setNext(RetryHandler next) {
        this.next = next;
    }

    protected void next(BaseRetryJob job) throws Exception {
        if (next != null) {
            next.handle(job);
        }
    }
}
