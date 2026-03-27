package com.lms.enrollment.infrastructure.client.dto;

import java.util.UUID;

public record CourseResponse(UUID id, String code, String title, String status) {
}
