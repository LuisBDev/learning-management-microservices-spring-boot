package com.lms.enrollment.controller.dto.response;

import com.lms.enrollment.domain.model.enums.EnrollmentEventType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentEventResponse {

    private UUID id;
    private UUID enrollmentId;
    private EnrollmentEventType eventType;
    private String previousStatus;
    private String newStatus;
    private String eventDetail;
    private UUID triggeredBy;
    private Instant createdAt;

}
