package com.lms.orchestrator.application.service.steps;

import com.lms.orchestrator.domain.model.CourseSagaContext;
import com.lms.orchestrator.domain.service.SagaStep;
import com.lms.orchestrator.infrastructure.client.enrollmentservicev1.EnrollmentServiceClient;
import com.lms.orchestrator.infrastructure.client.enrollmentservicev1.dto.request.CreateEnrollmentRequest;
import com.lms.orchestrator.infrastructure.client.enrollmentservicev1.dto.response.EnrollmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentStep implements SagaStep<CourseSagaContext, CourseSagaContext> {

    private final EnrollmentServiceClient enrollmentServiceClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String ENROLLMENT_COMPENSATION_TOPIC = "enrollment-service-compensation";

    @Override
    public CourseSagaContext execute(CourseSagaContext context) {
        log.info("Executing EnrollmentStep: Enrolling students for course ID: {}", context.getCourseId());
        context.setEnrollmentIds(new ArrayList<>());
        for (UUID studentId : context.getRequest().getStudentIds()) {
            CreateEnrollmentRequest request = CreateEnrollmentRequest.builder()
                    .courseId(context.getCourseId())
                    .studentUserId(studentId)
                    .build();
            EnrollmentResponse response = enrollmentServiceClient.enrollStudent(request);
            context.getEnrollmentIds().add(response.getId());
            log.info("Enrolled student {} with enrollment ID: {}", studentId, response.getId());
        }
        return context;
    }

    @Override
    public void compensate(CourseSagaContext context) {
        if (context.getEnrollmentIds() != null && !context.getEnrollmentIds().isEmpty()) {
            log.info("Compensating EnrollmentStep: Cancelling {} enrollments...", context.getEnrollmentIds().size());
            for (UUID enrollmentId : context.getEnrollmentIds()) {
                kafkaTemplate.send(ENROLLMENT_COMPENSATION_TOPIC, enrollmentId.toString());
            }
        }
    }
}
