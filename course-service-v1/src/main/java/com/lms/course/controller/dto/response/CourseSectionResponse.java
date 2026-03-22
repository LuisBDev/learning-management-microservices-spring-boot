package com.lms.course.controller.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSectionResponse {

    private UUID id;
    private UUID courseId;
    private String title;
    private String description;
    private Integer position;
    private Boolean visible;
    private Instant createdAt;
    private Instant updatedAt;

}
