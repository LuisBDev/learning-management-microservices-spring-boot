package com.lms.course.controller.dto.response;

import com.lms.course.domain.model.enums.ResourceUrlKind;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRecordingResponse {

    private UUID id;
    private UUID resourceId;
    private UUID courseId;
    private UUID sectionId;
    private String resourceTitle;
    private String url;
    private ResourceUrlKind urlKind;
    private Boolean openInNewTab;
    private String recordingName;
    private LocalDate classDate;
    private String commentText;
    private Boolean visible;
    private Boolean published;
    private Instant uploadedAt;
    private Instant updatedAt;

}
