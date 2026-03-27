package com.lms.learning.application.service;

import com.lms.learning.controller.dto.request.GradeSubmissionRequest;
import com.lms.learning.controller.dto.request.UpdateGradeRequest;
import com.lms.learning.controller.dto.response.GradeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GradeService {

    GradeResponse grade(GradeSubmissionRequest request, UUID gradedByUserId);

    GradeResponse getById(UUID id);

    Page<GradeResponse> getByAssignmentId(UUID assignmentId, Pageable pageable);

    Page<GradeResponse> getByStudentUserId(UUID studentUserId, Pageable pageable);

    GradeResponse getByAssignmentAndStudent(UUID assignmentId, UUID studentUserId);

    GradeResponse update(UUID id, UpdateGradeRequest request, UUID gradedByUserId);

}
