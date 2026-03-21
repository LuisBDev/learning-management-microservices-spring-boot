package com.lms.course.controller.dto.request;

import jakarta.validation.constraints.Max;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCourseRequest {

    @Max(50)
    private String code;

    @Max(200)
    private String title;

    private String summary;

}
