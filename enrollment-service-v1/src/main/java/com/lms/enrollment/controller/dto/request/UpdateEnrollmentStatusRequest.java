package com.lms.enrollment.controller.dto.request;

import com.lms.enrollment.domain.model.enums.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEnrollmentStatusRequest {

    @NotNull(message = "Status is required")
    private EnrollmentStatus status;

    private String eventDetail;

}
