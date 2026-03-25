package com.lms.course.controller.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTextResourceRequest {

    private String title;

    private Boolean visible;
    private Boolean published;
    private String contentText;

}
