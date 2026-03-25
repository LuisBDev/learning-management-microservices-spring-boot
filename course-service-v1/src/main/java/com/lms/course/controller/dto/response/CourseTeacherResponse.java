package com.lms.course.controller.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseTeacherResponse {

    private UUID id;
    private UUID courseId;
    private UUID teacherUserId;
    private UUID assignedBy;
    private Instant assignedAt;
    private Instant createdAt;
    private Instant updatedAt;

}
