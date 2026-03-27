package com.lms.learning.controller.dto.response;

import com.lms.learning.domain.model.enums.GradeStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeResponse {

    private UUID id;
    private UUID assignmentId;
    private UUID courseId;
    private UUID studentUserId;
    private UUID submissionId;
    private BigDecimal score;
    private GradeStatus gradeStatus;
    private String teacherComment;
    private UUID gradedByUserId;
    private Instant gradedAt;
    private Instant updatedAt;

}
