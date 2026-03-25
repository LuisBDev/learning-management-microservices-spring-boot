package com.lms.course.controller.dto.request;

import com.lms.course.domain.model.enums.ResourceUrlKind;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUrlResourceRequest {

    private String title;

    private Boolean visible;
    private Boolean published;

    @NotBlank(message = "URL is required")
    private String url;

    private ResourceUrlKind urlKind;
    private Boolean openInNewTab;

}
