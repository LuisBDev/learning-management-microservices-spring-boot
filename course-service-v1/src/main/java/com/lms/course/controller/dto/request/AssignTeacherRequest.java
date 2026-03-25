package com.lms.course.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignTeacherRequest {

    @NotNull(message = "Teacher user ID is required")
    private UUID teacherUserId;

}
