package com.lms.course.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssignmentResourceRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Builder.Default
    private Boolean visible = true;

    @Builder.Default
    private Boolean published = true;

    private String instructionsText;
    private Instant availableFrom;
    private Instant dueAt;
    private BigDecimal maxScore;

    @Builder.Default
    private Boolean allowResubmission = false;

}
