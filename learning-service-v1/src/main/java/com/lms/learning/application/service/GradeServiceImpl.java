package com.lms.learning.application.service;

import com.lms.learning.controller.dto.request.GradeSubmissionRequest;
import com.lms.learning.controller.dto.request.UpdateGradeRequest;
import com.lms.learning.controller.dto.response.GradeResponse;
import com.lms.learning.controller.mapper.GradeMapper;
import com.lms.learning.domain.model.enums.GradeStatus;
import com.lms.learning.exception.DuplicateResourceException;
import com.lms.learning.exception.ResourceNotFoundException;
import com.lms.learning.infrastructure.persistence.entity.AssignmentGradeEntity;
import com.lms.learning.infrastructure.persistence.entity.AssignmentSubmissionEntity;
import com.lms.learning.infrastructure.persistence.repository.JpaAssignmentGradeRepository;
import com.lms.learning.infrastructure.persistence.repository.JpaAssignmentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final JpaAssignmentGradeRepository gradeRepository;
    private final JpaAssignmentSubmissionRepository submissionRepository;
    private final GradeMapper gradeMapper;

    @Override
    @Transactional
    public GradeResponse grade(GradeSubmissionRequest request, UUID gradedByUserId) {
        gradeRepository.findByAssignmentIdAndStudentUserId(request.getAssignmentId(), request.getStudentUserId())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Grade already exists for assignment " + request.getAssignmentId() + " and student " + request.getStudentUserId());
                });


        AssignmentSubmissionEntity submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", request.getSubmissionId()));


        Instant now = Instant.now();
        AssignmentGradeEntity grade = AssignmentGradeEntity.builder()
                .assignmentId(request.getAssignmentId())
                .courseId(request.getCourseId())
                .studentUserId(request.getStudentUserId())
                .submission(submission)
                .score(request.getScore())
                .gradeStatus(request.getGradeStatus())
                .teacherComment(request.getTeacherComment())
                .gradedByUserId(gradedByUserId)
                .gradedAt(request.getGradeStatus() == GradeStatus.GRADED ? now : null)
                .build();

        grade = gradeRepository.save(grade);
        log.info("Grade created: student {} for assignment {} with status {}",
                request.getStudentUserId(), request.getAssignmentId(), request.getGradeStatus());
        return gradeMapper.toResponse(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeResponse getById(UUID id) {
        return gradeMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GradeResponse> getByAssignmentId(UUID assignmentId, Pageable pageable) {
        return gradeRepository.findByAssignmentId(assignmentId, pageable).map(gradeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GradeResponse> getByStudentUserId(UUID studentUserId, Pageable pageable) {
        return gradeRepository.findByStudentUserId(studentUserId, pageable).map(gradeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeResponse getByAssignmentAndStudent(UUID assignmentId, UUID studentUserId) {
        AssignmentGradeEntity grade = gradeRepository.findByAssignmentIdAndStudentUserId(assignmentId, studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Grade not found for assignment " + assignmentId + " and student " + studentUserId));
        return gradeMapper.toResponse(grade);
    }

    @Override
    @Transactional
    public GradeResponse update(UUID id, UpdateGradeRequest request, UUID gradedByUserId) {
        AssignmentGradeEntity grade = findOrThrow(id);

        if (request.getScore() != null) grade.setScore(request.getScore());
        if (request.getGradeStatus() != null) {
            grade.setGradeStatus(request.getGradeStatus());
            if (request.getGradeStatus() == GradeStatus.GRADED) {
                grade.setGradedAt(Instant.now());
            }
        }
        if (request.getTeacherComment() != null) grade.setTeacherComment(request.getTeacherComment());
        grade.setGradedByUserId(gradedByUserId);

        grade = gradeRepository.save(grade);
        log.info("Grade updated: {} with status {}", id, grade.getGradeStatus());
        return gradeMapper.toResponse(grade);
    }

    @Override
    public List<GradeResponse> getByCourseId(UUID courseId) {
        return gradeRepository.findByCourseId(courseId).stream().map(gradeMapper::toResponse).toList();
    }

    private AssignmentGradeEntity findOrThrow(UUID id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", id));
    }

}
