package com.lms.course.controller.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCourseRequest {

    @Size(max = 50, message = "Code must be at most 50 characters")
    private String code;

    @Size(max = 200, message = "Title must be at most 255 characters")
    private String title;

    private String summary;

}
