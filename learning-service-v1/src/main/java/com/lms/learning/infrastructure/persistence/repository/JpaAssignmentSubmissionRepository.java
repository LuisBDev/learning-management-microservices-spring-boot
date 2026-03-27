package com.lms.learning.infrastructure.persistence.repository;

import com.lms.learning.infrastructure.persistence.entity.AssignmentSubmissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaAssignmentSubmissionRepository extends JpaRepository<AssignmentSubmissionEntity, UUID> {

    Page<AssignmentSubmissionEntity> findByAssignmentId(UUID assignmentId, Pageable pageable);

    Optional<AssignmentSubmissionEntity> findByAssignmentIdAndStudentUserId(UUID assignmentId, UUID studentUserId);

    Page<AssignmentSubmissionEntity> findByStudentUserId(UUID studentUserId, Pageable pageable);

}
