package com.lms.orchestrator.domain.service;

public interface SagaStep<T, R> {
    R execute(T data);
    void compensate(T data);
}
