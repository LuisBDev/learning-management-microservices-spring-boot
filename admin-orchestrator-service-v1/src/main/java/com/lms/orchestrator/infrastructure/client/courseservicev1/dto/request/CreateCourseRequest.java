package com.lms.orchestrator.infrastructure.client.courseservicev1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseRequest {
    @NotBlank
    private String code;
    @NotBlank
    private String title;
    private String summary;
}
