package com.lms.course.controller.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssignmentResourceRequest {

    private String title;

    private Boolean visible;
    private Boolean published;
    private String instructionsText;
    private Instant availableFrom;
    private Instant dueAt;
    private BigDecimal maxScore;
    private Boolean allowResubmission;

}
