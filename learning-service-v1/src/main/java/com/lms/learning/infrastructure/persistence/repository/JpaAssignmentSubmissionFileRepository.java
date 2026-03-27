package com.lms.learning.infrastructure.persistence.repository;

import com.lms.learning.infrastructure.persistence.entity.AssignmentSubmissionFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaAssignmentSubmissionFileRepository extends JpaRepository<AssignmentSubmissionFileEntity, UUID> {

    List<AssignmentSubmissionFileEntity> findBySubmissionId(UUID submissionId);

}
