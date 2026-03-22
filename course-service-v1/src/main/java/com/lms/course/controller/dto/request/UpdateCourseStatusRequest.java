package com.lms.course.controller.dto.request;

import com.lms.course.domain.model.enums.CourseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCourseStatusRequest {

    @NotNull(message = "Status is required")
    private CourseStatus status;

}
