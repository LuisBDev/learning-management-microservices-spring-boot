package com.lms.enrollment.controller.dto.response;

import com.lms.enrollment.domain.model.enums.EnrollmentStatus;
import lombok.*;

import java.time.Instant;
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
    private EnrollmentStatus status;
    private Instant enrolledAt;
    private Instant updatedAt;

}
