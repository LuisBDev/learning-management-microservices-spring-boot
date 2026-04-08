package com.lms.orchestrator.infrastructure.client.courseservicev1.dto.response;

import lombok.*;

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
}
