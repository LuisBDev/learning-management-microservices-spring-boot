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
public class UpdateRecordingRequest {

    private String title;

    private Boolean visible;
    private Boolean published;

    private String url;

    private Boolean openInNewTab;

    private String recordingName;

    private LocalDate classDate;

    private String commentText;

}
