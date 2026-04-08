package com.lms.orchestrator.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CourseDeletionSagaContext {
    private final UUID courseId;
}
