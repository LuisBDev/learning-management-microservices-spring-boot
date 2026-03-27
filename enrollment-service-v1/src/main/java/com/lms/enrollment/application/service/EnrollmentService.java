package com.lms.enrollment.application.service;

import com.lms.enrollment.controller.dto.request.CreateEnrollmentRequest;
import com.lms.enrollment.controller.dto.request.UpdateEnrollmentStatusRequest;
import com.lms.enrollment.controller.dto.response.EnrollmentEventResponse;
import com.lms.enrollment.controller.dto.response.EnrollmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {

    EnrollmentResponse enroll(CreateEnrollmentRequest request, UUID triggeredBy);

    EnrollmentResponse getById(UUID id);

    EnrollmentResponse getByCourseAndStudent(UUID courseId, UUID studentUserId);

    Page<EnrollmentResponse> getByCourseId(UUID courseId, Pageable pageable);

    Page<EnrollmentResponse> getByStudentUserId(UUID studentUserId, Pageable pageable);

    EnrollmentResponse updateStatus(UUID id, UpdateEnrollmentStatusRequest request, UUID triggeredBy);

    List<EnrollmentEventResponse> getEnrollmentHistory(UUID enrollmentId);

}
