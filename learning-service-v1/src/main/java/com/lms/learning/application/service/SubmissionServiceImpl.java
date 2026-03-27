package com.lms.learning.application.service;

import com.lms.learning.controller.dto.request.CreateSubmissionRequest;
import com.lms.learning.controller.dto.response.SubmissionResponse;
import com.lms.learning.controller.mapper.SubmissionMapper;
import com.lms.learning.domain.model.enums.SubmissionStatus;
import com.lms.learning.exception.DuplicateResourceException;
import com.lms.learning.exception.InvalidOperationException;
import com.lms.learning.exception.ResourceNotFoundException;
import com.lms.learning.infrastructure.client.enrollmentservicev1.EnrollmentServiceClient;
import com.lms.learning.infrastructure.persistence.entity.AssignmentSubmissionEntity;
import com.lms.learning.infrastructure.persistence.repository.JpaAssignmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final JpaAssignmentSubmissionRepository submissionRepository;
    private final SubmissionMapper submissionMapper;
    private final EnrollmentServiceClient enrollmentServiceClient;

    @Override
    @Transactional
    public SubmissionResponse submit(CreateSubmissionRequest request) {

        if (!enrollmentServiceClient.isStudentEnrolledAndActive(request.getCourseId(), request.getStudentUserId())) {
            throw new InvalidOperationException(
                    "Student " + request.getStudentUserId() + " is not actively enrolled in course " + request.getCourseId());
        }

        submissionRepository.findByAssignmentIdAndStudentUserId(request.getAssignmentId(), request.getStudentUserId())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Student " + request.getStudentUserId() + " already submitted assignment " + request.getAssignmentId());
                });

        AssignmentSubmissionEntity submission = AssignmentSubmissionEntity.builder()
                .assignmentId(request.getAssignmentId())
                .courseId(request.getCourseId())
                .studentUserId(request.getStudentUserId())
                .submittedAt(Instant.now())
                .submissionStatus(SubmissionStatus.SUBMITTED)
                .studentComment(request.getStudentComment())
                .build();

        submission = submissionRepository.save(submission);
        log.info("Submission created: student {} for assignment {} in course {}",
                request.getStudentUserId(), request.getAssignmentId(), request.getCourseId());
        return submissionMapper.toResponse(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getById(UUID id) {
        return submissionMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubmissionResponse> getByAssignmentId(UUID assignmentId, Pageable pageable) {
        return submissionRepository.findByAssignmentId(assignmentId, pageable).map(submissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubmissionResponse> getByStudentUserId(UUID studentUserId, Pageable pageable) {
        return submissionRepository.findByStudentUserId(studentUserId, pageable).map(submissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getByAssignmentAndStudent(UUID assignmentId, UUID studentUserId) {
        AssignmentSubmissionEntity submission = submissionRepository.findByAssignmentIdAndStudentUserId(assignmentId, studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Submission not found for assignment " + assignmentId + " and student " + studentUserId));
        return submissionMapper.toResponse(submission);
    }

    private AssignmentSubmissionEntity findOrThrow(UUID id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));
    }

}
