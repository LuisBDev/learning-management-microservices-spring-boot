package com.lms.enrollment.infrastructure.client.courseservicev1.dto;

import java.util.UUID;

public record CourseResponse(UUID id, String code, String title, String status) {
}
