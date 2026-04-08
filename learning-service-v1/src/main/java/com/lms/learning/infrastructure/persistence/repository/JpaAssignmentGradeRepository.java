package com.lms.learning.infrastructure.persistence.repository;

import com.lms.learning.infrastructure.persistence.entity.AssignmentGradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaAssignmentGradeRepository extends JpaRepository<AssignmentGradeEntity, UUID> {
    
    @Modifying
    @Query("DELETE FROM AssignmentGradeEntity g WHERE g.courseId = :courseId")
    void deleteByCourseId(UUID courseId);
}
