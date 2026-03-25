package com.lms.course.controller.dto.request;

import com.lms.course.domain.model.enums.ResourceUrlKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUrlResourceRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Builder.Default
    private Boolean visible = true;

    @Builder.Default
    private Boolean published = false;

    @NotBlank(message = "URL is required")
    private String url;

    @Builder.Default
    private ResourceUrlKind urlKind = ResourceUrlKind.GENERIC;

    @Builder.Default
    private Boolean openInNewTab = true;

}
