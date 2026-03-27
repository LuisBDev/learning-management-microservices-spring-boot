package com.lms.enrollment.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnrollmentRequest {

    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @NotNull(message = "Student user ID is required")
    private UUID studentUserId;

}
