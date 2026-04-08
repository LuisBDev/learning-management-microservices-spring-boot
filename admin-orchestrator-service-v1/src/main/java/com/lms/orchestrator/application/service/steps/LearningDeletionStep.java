package com.lms.orchestrator.application.service.steps;

import com.lms.orchestrator.domain.model.CourseDeletionSagaContext;
import com.lms.orchestrator.domain.service.SagaStep;
import com.lms.orchestrator.infrastructure.client.learningservicev1.LearningServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LearningDeletionStep implements SagaStep<CourseDeletionSagaContext, CourseDeletionSagaContext> {

    private final LearningServiceClient learningServiceClient;

    @Override
    public CourseDeletionSagaContext execute(CourseDeletionSagaContext context) {
        log.info("Step: Deleting learning data for course {}", context.getCourseId());
        learningServiceClient.deleteCourseLearningData(context.getCourseId());
        return context;
    }

    @Override
    public void compensate(CourseDeletionSagaContext context) {
        // Physical DELETE compensation: Not possible without complex restoration
        log.warn("Step: Compensation for learning deletion of course {} is NOT supported (physical delete). " +
                "Manual intervention or critical logging required.", context.getCourseId());
    }
}
