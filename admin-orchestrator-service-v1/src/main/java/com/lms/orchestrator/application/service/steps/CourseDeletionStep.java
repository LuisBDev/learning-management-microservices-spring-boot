package com.lms.orchestrator.application.service.steps;

import com.lms.orchestrator.domain.model.CourseDeletionSagaContext;
import com.lms.orchestrator.domain.service.SagaStep;
import com.lms.orchestrator.infrastructure.client.courseservicev1.CourseServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseDeletionStep implements SagaStep<CourseDeletionSagaContext, CourseDeletionSagaContext> {

    private final CourseServiceClient courseServiceClient;

    @Override
    public CourseDeletionSagaContext execute(CourseDeletionSagaContext context) {
        log.info("Step: Deleting course {}", context.getCourseId());
        courseServiceClient.deleteCourse(context.getCourseId());
        return context;
    }

    @Override
    public void compensate(CourseDeletionSagaContext context) {
        log.warn("Step: Compensation for course deletion of {} is NOT supported (physical delete).", context.getCourseId());
    }
}
