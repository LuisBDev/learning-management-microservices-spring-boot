package com.lms.orchestrator.application.service;

import com.lms.orchestrator.application.service.steps.CourseDeletionStep;
import com.lms.orchestrator.application.service.steps.EnrollmentDeletionStep;
import com.lms.orchestrator.application.service.steps.LearningDeletionStep;
import com.lms.orchestrator.domain.exception.SagaExecutionException;
import com.lms.orchestrator.domain.model.CourseDeletionSagaContext;
import com.lms.orchestrator.domain.service.SagaStep;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseDeletionSagaService {

    private final LearningDeletionStep learningDeletionStep;
    private final EnrollmentDeletionStep enrollmentDeletionStep;
    private final CourseDeletionStep courseDeletionStep;

    @Retry(name = "courseDeletionSaga")
    public void executeSaga(UUID courseId) {
        log.info("Starting Course Deletion SAGA for course: {}", courseId);

        CourseDeletionSagaContext context = CourseDeletionSagaContext.builder()
                .courseId(courseId)
                .build();

        List<SagaStep<CourseDeletionSagaContext, CourseDeletionSagaContext>> steps = List.of(
                learningDeletionStep,
                enrollmentDeletionStep,
                courseDeletionStep
        );

        List<SagaStep<CourseDeletionSagaContext, CourseDeletionSagaContext>> executedSteps = new ArrayList<>();

        try {
            for (SagaStep<CourseDeletionSagaContext, CourseDeletionSagaContext> step : steps) {
                executedSteps.add(step);
                context = step.execute(context);
            }
            log.info("Course Deletion SAGA completed successfully for: {}", courseId);
        } catch (Exception e) {
            log.error("Course Deletion SAGA failed: {}. Retrying via @Retry or compensating...", e.getMessage());
            compensate(executedSteps, context);
            throw new SagaExecutionException("Physical Course Deletion failed. Data might be partially deleted.", e);
        }
    }

    private void compensate(List<SagaStep<CourseDeletionSagaContext, CourseDeletionSagaContext>> executedSteps, CourseDeletionSagaContext context) {
        log.info("Executing compensation for {} steps (Log-only for physical deletes)", executedSteps.size());
        executedSteps.reversed().forEach(step -> {
            try {
                step.compensate(context);
            } catch (Exception e) {
                log.error("Failed to compensate step: {}", e.getMessage());
            }
        });
    }
}
