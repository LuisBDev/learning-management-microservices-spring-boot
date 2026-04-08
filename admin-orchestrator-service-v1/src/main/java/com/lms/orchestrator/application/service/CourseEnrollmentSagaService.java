package com.lms.orchestrator.application.service;

import com.lms.orchestrator.application.service.steps.CourseStep;
import com.lms.orchestrator.application.service.steps.EnrollmentStep;
import com.lms.orchestrator.domain.exception.SagaExecutionException;
import com.lms.orchestrator.domain.model.CourseEnrollmentSagaRequest;
import com.lms.orchestrator.domain.model.CourseSagaContext;
import com.lms.orchestrator.domain.service.SagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseEnrollmentSagaService {

    private final CourseStep courseStep;
    private final EnrollmentStep enrollmentStep;

    public void executeSaga(CourseEnrollmentSagaRequest request) {
        log.info("Starting Course Enrollment SAGA: {}", request.getCourseName());

        CourseSagaContext context = CourseSagaContext.builder()
                .request(request)
                .build();

        List<SagaStep<CourseSagaContext, CourseSagaContext>> steps = List.of(courseStep, enrollmentStep);
        List<SagaStep<CourseSagaContext, CourseSagaContext>> executedSteps = new ArrayList<>();

        try {
            for (var step : steps) {
                executedSteps.add(step);
                context = step.execute(context);
            }
            log.info("SAGA completed successfully!");
        } catch (Exception e) {
            log.error("SAGA failed: {}. Initiating compensation...", e.getMessage());
            compensate(executedSteps, context);
            throw new SagaExecutionException("Transaction failed and compensation was triggered: " + e.getMessage(), e);
        }
    }

    private void compensate(List<SagaStep<CourseSagaContext, CourseSagaContext>> executedSteps, CourseSagaContext context) {
        log.info("Executing compensation for {} steps in reverse order", executedSteps.size());
        executedSteps.reversed().forEach(step -> {
            try {
                step.compensate(context);
            } catch (Exception e) {
                log.error("Failed to compensate step: {}", e.getMessage());
            }
        });
    }
}
