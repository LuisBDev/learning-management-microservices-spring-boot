package com.lms.orchestrator.infrastructure.client.enrollmentservicev1.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private UUID id;
    private UUID courseId;
    private UUID studentUserId;
}
