package com.lms.orchestrator.infrastructure.client.enrollmentservicev1;

import com.lms.orchestrator.infrastructure.client.enrollmentservicev1.dto.request.CreateEnrollmentRequest;
import com.lms.orchestrator.infrastructure.client.enrollmentservicev1.dto.response.EnrollmentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentServiceClient {

    private final RestClient enrollmentRestClient;

    @CircuitBreaker(name = "enrollment-service", fallbackMethod = "enrollStudentFallback")
    public EnrollmentResponse enrollStudent(CreateEnrollmentRequest request) {
        return enrollmentRestClient.post()
                .uri("/api/v1/enrollments")
                .body(request)
                .retrieve()
                .body(EnrollmentResponse.class);
    }

    // Fallback
    private EnrollmentResponse enrollStudentFallback(CreateEnrollmentRequest request, Throwable throwable) {
        log.error("Circuit breaker 'enrollment-service' triggered for enrollStudent. Reason: {}", throwable.getMessage());
        throw new RuntimeException("Enrollment service is unavailable. Please try again later.");
    }
}
