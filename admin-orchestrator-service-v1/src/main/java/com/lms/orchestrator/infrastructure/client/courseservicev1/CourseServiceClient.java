package com.lms.orchestrator.infrastructure.client.courseservicev1;

import com.lms.orchestrator.infrastructure.client.courseservicev1.dto.request.CreateCourseRequest;
import com.lms.orchestrator.infrastructure.client.courseservicev1.dto.response.CourseResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseServiceClient {

    private final RestClient courseRestClient;

    @CircuitBreaker(name = "course-service", fallbackMethod = "createCourseFallback")
    public CourseResponse createCourse(CreateCourseRequest request) {
        return courseRestClient.post()
                .uri("/api/v1/courses")
                .body(request)
                .retrieve()
                .body(CourseResponse.class);
    }

    @CircuitBreaker(name = "course-service", fallbackMethod = "deleteCourseFallback")
    public void deleteCourse(UUID courseId) {
        courseRestClient.delete()
                .uri("/api/v1/courses/{id}", courseId)
                .retrieve()
                .toBodilessEntity();
    }

    // Fallbacks
    private CourseResponse createCourseFallback(CreateCourseRequest request, Throwable throwable) {
        log.error("Circuit breaker 'course-service' triggered for createCourse. Reason: {}", throwable.getMessage());
        throw new RuntimeException("Course service is unavailable. Please try again later.");
    }

    private void deleteCourseFallback(UUID courseId, Throwable throwable) {
        log.error("Circuit breaker 'course-service' triggered for deleteCourse for ID {}. Reason: {}", courseId, throwable.getMessage());
    }
}
