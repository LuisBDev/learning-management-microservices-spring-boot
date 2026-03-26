package com.lms.course.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecordingRequest {

    @NotBlank(message = "Resource title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Builder.Default
    private Boolean visible = true;

    @Builder.Default
    private Boolean published = false;

    @NotBlank(message = "URL is required")
    private String url;

    @Builder.Default
    private Boolean openInNewTab = true;

    @NotBlank(message = "Recording name is required")
    @Size(max = 255, message = "Recording name must not exceed 255 characters")
    private String recordingName;

    private LocalDate classDate;

    private String commentText;

}
