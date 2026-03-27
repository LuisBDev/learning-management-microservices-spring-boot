package com.lms.learning.infrastructure.client.enrollmentservicev1;

import com.lms.learning.infrastructure.client.enrollmentservicev1.dto.EnrollmentCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentServiceClient {

    private final RestClient enrollmentRestClient;

    public boolean isStudentEnrolledAndActive(UUID courseId, UUID studentUserId) {
        try {
            log.info("[INTER-SERVICE] Checking enrollment for student {} in course {}", studentUserId, courseId);
            EnrollmentCheckResponse enrollment = enrollmentRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/enrollments/check")
                            .queryParam("courseId", courseId)
                            .queryParam("studentUserId", studentUserId)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) ->
                            log.info("No enrollment found for student {} in course {}", studentUserId, courseId)
                    )
                    .body(EnrollmentCheckResponse.class);

            return enrollment != null && "ACTIVE".equals(enrollment.status());
        } catch (Exception e) {
            log.error("Error checking enrollment for student {} in course {}: {}",
                    studentUserId, courseId, e.getMessage());
            return false;
        }
    }


}
