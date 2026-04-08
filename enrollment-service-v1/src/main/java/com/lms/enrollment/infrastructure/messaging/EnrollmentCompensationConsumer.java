package com.lms.enrollment.infrastructure.messaging;

import com.lms.enrollment.application.service.EnrollmentService;
import com.lms.enrollment.controller.dto.request.UpdateEnrollmentStatusRequest;
import com.lms.enrollment.domain.model.enums.EnrollmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentCompensationConsumer {

    private final EnrollmentService enrollmentService;
    private static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @KafkaListener(topics = "enrollment-service-compensation", groupId = "enrollment-group")
    public void handleEnrollmentCompensation(String enrollmentId) {
        log.info("Received compensation event for enrollment ID: {}", enrollmentId);
        try {
            UpdateEnrollmentStatusRequest request = UpdateEnrollmentStatusRequest.builder()
                    .status(EnrollmentStatus.CANCELLED)
                    .eventDetail("SAGA Compensation: Automatic cancellation due to transaction failure")
                    .build();
            
            enrollmentService.updateStatus(UUID.fromString(enrollmentId), request, SYSTEM_USER_ID);
            log.info("Successfully cancelled enrollment {} as part of SAGA compensation", enrollmentId);
        } catch (Exception e) {
            log.error("Failed to compensate enrollment cancellation for ID {}: {}", enrollmentId, e.getMessage());
        }
    }
}
