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
public class CourseEnrollmentSagaRequest {
    
    private String courseCode;
    private String courseName;
    private String courseDescription;
    private List<UUID> studentIds;
}
