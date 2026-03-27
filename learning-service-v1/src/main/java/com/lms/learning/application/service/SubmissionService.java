package com.lms.learning.application.service;

import com.lms.learning.controller.dto.request.CreateSubmissionRequest;
import com.lms.learning.controller.dto.response.SubmissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SubmissionService {

    SubmissionResponse submit(CreateSubmissionRequest request);

    SubmissionResponse getById(UUID id);

    Page<SubmissionResponse> getByAssignmentId(UUID assignmentId, Pageable pageable);

    Page<SubmissionResponse> getByStudentUserId(UUID studentUserId, Pageable pageable);

    SubmissionResponse getByAssignmentAndStudent(UUID assignmentId, UUID studentUserId);

}
