package com.lms.orchestrator.application.service.steps;

import com.lms.orchestrator.domain.model.CourseDeletionSagaContext;
import com.lms.orchestrator.domain.service.SagaStep;
import com.lms.orchestrator.infrastructure.client.enrollmentservicev1.EnrollmentServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentDeletionStep implements SagaStep<CourseDeletionSagaContext, CourseDeletionSagaContext> {

    private final EnrollmentServiceClient enrollmentServiceClient;

    @Override
    public CourseDeletionSagaContext execute(CourseDeletionSagaContext context) {
        log.info("Step: Deleting enrollment data for course {}", context.getCourseId());
        enrollmentServiceClient.deleteEnrollmentsByCourse(context.getCourseId());
        return context;
    }

    @Override
    public void compensate(CourseDeletionSagaContext context) {
        log.warn("Step: Compensation for enrollment deletion of course {} is NOT supported (physical delete).", context.getCourseId());
    }
}
