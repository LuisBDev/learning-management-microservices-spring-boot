package com.lms.learning.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubmissionRequest {

    @NotNull(message = "Assignment ID is required")
    private UUID assignmentId;

    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @NotNull(message = "Student user ID is required")
    private UUID studentUserId;

    private String studentComment;

}
