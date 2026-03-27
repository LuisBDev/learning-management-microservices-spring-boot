package com.lms.learning.controller.dto.request;

import com.lms.learning.domain.model.enums.GradeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeSubmissionRequest {

    @NotNull(message = "Assignment ID is required")
    private UUID assignmentId;

    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @NotNull(message = "Student user ID is required")
    private UUID studentUserId;

    @NotNull(message = "Submission ID is required")
    private UUID submissionId;

    private BigDecimal score;

    @NotNull(message = "Grade status is required")
    private GradeStatus gradeStatus;

    private String teacherComment;

}
