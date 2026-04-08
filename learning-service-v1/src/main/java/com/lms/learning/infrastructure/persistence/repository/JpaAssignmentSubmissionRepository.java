package com.lms.learning.infrastructure.persistence.repository;

import com.lms.learning.infrastructure.persistence.entity.AssignmentSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaAssignmentSubmissionRepository extends JpaRepository<AssignmentSubmissionEntity, UUID> {
    
    @Modifying
    @Query("DELETE FROM AssignmentSubmissionEntity s WHERE s.courseId = :courseId")
    void deleteByCourseId(UUID courseId);
}
