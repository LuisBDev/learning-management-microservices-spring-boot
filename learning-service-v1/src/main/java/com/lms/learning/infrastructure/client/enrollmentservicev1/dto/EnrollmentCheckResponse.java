package com.lms.learning.infrastructure.client.enrollmentservicev1.dto;

import java.util.UUID;

public record EnrollmentCheckResponse(UUID id, UUID courseId, UUID studentUserId, String status) {
}