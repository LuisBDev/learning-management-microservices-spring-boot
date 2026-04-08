package com.lms.orchestrator.application.service.steps;

import com.lms.orchestrator.domain.model.CourseSagaContext;
import com.lms.orchestrator.domain.service.SagaStep;
import com.lms.orchestrator.infrastructure.client.courseservicev1.CourseServiceClient;
import com.lms.orchestrator.infrastructure.client.courseservicev1.dto.request.CreateCourseRequest;
import com.lms.orchestrator.infrastructure.client.courseservicev1.dto.response.CourseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseStep implements SagaStep<CourseSagaContext, CourseSagaContext> {

    private final CourseServiceClient courseServiceClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String COURSE_COMPENSATION_TOPIC = "course-service-compensation";

    @Override
    public CourseSagaContext execute(CourseSagaContext context) {
        log.info("Executing CourseStep: Creating course...");
        CreateCourseRequest request = CreateCourseRequest.builder()
                .code(context.getRequest().getCourseCode())
                .title(context.getRequest().getCourseName())
                .summary(context.getRequest().getCourseDescription())
                .build();

        CourseResponse response = courseServiceClient.createCourse(request);
        context.setCourseId(response.getId());
        log.info("Course created with ID: {}", response.getId());
        return context;
    }

    @Override
    public void compensate(CourseSagaContext context) {
        if (context.getCourseId() != null) {
            log.info("Compensating CourseStep: Sending event to delete course {}", context.getCourseId());
            kafkaTemplate.send(COURSE_COMPENSATION_TOPIC, context.getCourseId().toString());
        }
    }
}
