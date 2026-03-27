package com.lms.enrollment.infrastructure.client.courseservicev1;

import com.lms.enrollment.exception.ResourceNotFoundException;
import com.lms.enrollment.infrastructure.client.courseservicev1.dto.CourseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseServiceClient {

    private final RestClient courseRestClient;

    public CourseResponse getCourseById(UUID courseId) {
        log.info("[INTER-SERVICE] Fetching course {} from course-service", courseId);
        return courseRestClient.get()
                .uri("/api/v1/courses/{id}", courseId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Course", "id", courseId);
                })
                .body(CourseResponse.class);
    }

    public boolean courseExistsAndPublished(UUID courseId) {
        try {
            CourseResponse course = getCourseById(courseId);
            return course != null && "PUBLISHED".equals(course.status());
        } catch (ResourceNotFoundException e) {
            return false;
        } catch (Exception e) {
            log.error("Error checking course {}: {}", courseId, e.getMessage());
            return false;
        }
    }


}
