package com.lms.orchestrator.domain.exception;

public class SagaExecutionException extends RuntimeException {
    public SagaExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
