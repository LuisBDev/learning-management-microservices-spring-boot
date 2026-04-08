package com.lms.orchestrator.infrastructure.client.learningservicev1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LearningServiceClient {

    private final RestClient learningRestClient;

    public void deleteCourseLearningData(UUID courseId) {
        log.info("Calling learning-service to delete data for course: {}", courseId);
        learningRestClient.delete()
                .uri("/api/v1/learning/courses/{courseId}", courseId)
                .retrieve()
                .toBodilessEntity();
    }
}
