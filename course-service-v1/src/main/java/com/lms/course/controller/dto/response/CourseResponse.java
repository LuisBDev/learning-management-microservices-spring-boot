package com.lms.course.controller.dto.response;

import com.lms.course.domain.model.enums.CourseStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private UUID id;
    private String code;
    private String title;
    private String summary;
    private CourseStatus status;
    private UUID createdBy;
    private Instant createdAt;
    private Instant updatedAt;

}
