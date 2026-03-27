package com.lms.learning.controller.dto.response;

import com.lms.learning.domain.model.enums.SubmissionStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {

    private UUID id;
    private UUID assignmentId;
    private UUID courseId;
    private UUID studentUserId;
    private Instant submittedAt;
    private SubmissionStatus submissionStatus;
    private String studentComment;
    private Instant createdAt;
    private Instant updatedAt;

}
