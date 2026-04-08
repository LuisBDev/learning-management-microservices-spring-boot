package com.lms.course.infrastructure.messaging;

import com.lms.course.application.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseCompensationConsumer {

    private final CourseService courseService;

    @KafkaListener(topics = "course-service-compensation", groupId = "course-group")
    public void handleCourseCompensation(String courseId) {
        log.info("Received compensation event for course ID: {}", courseId);
        try {
            courseService.delete(UUID.fromString(courseId));
            log.info("Successfully deleted course {} as part of SAGA compensation", courseId);
        } catch (Exception e) {
            log.error("Failed to compensate course deletion for ID {}: {}", courseId, e.getMessage());
        }
    }
}
