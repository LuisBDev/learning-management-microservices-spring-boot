package com.lms.learning.infrastructure.persistence.repository;

import com.lms.learning.infrastructure.persistence.entity.AssignmentSubmissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAssignmentSubmissionRepository extends JpaRepository<AssignmentSubmissionEntity, UUID> {

    Optional<AssignmentSubmissionEntity> findByAssignmentIdAndStudentUserId(UUID assignmentId, UUID studentUserId);

    Page<AssignmentSubmissionEntity> findByAssignmentId(UUID assignmentId, Pageable pageable);

    Page<AssignmentSubmissionEntity> findByStudentUserId(UUID studentUserId, Pageable pageable);
    
    @Modifying
    @Query("DELETE FROM AssignmentSubmissionEntity s WHERE s.courseId = :courseId")
    void deleteByCourseId(UUID courseId);
}
