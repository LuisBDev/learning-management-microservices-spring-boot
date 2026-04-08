package com.lms.orchestrator.infrastructure.client.enrollmentservicev1.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnrollmentRequest {
    private UUID courseId;
    private UUID studentUserId;
}
