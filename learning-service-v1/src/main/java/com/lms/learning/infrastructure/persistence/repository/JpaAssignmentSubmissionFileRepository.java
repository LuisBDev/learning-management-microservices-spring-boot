package com.lms.learning.infrastructure.persistence.repository;

import com.lms.learning.infrastructure.persistence.entity.AssignmentSubmissionFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaAssignmentSubmissionFileRepository extends JpaRepository<AssignmentSubmissionFileEntity, UUID> {

    @Modifying
    @Query("DELETE FROM AssignmentSubmissionFileEntity f WHERE f.submission.id IN " +
           "(SELECT s.id FROM AssignmentSubmissionEntity s WHERE s.courseId = :courseId)")
    void deleteByCourseId(UUID courseId);
}
