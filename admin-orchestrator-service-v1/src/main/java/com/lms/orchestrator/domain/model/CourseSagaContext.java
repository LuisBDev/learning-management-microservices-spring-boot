package com.lms.orchestrator.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSagaContext {
    private CourseEnrollmentSagaRequest request;
    private UUID courseId;
    private List<UUID> enrollmentIds;
}
