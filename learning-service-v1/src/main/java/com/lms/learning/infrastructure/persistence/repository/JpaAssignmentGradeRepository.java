package com.lms.learning.infrastructure.persistence.repository;

import com.lms.learning.infrastructure.persistence.entity.AssignmentGradeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaAssignmentGradeRepository extends JpaRepository<AssignmentGradeEntity, UUID> {

    Optional<AssignmentGradeEntity> findByAssignmentIdAndStudentUserId(UUID assignmentId, UUID studentUserId);

    Page<AssignmentGradeEntity> findByAssignmentId(UUID assignmentId, Pageable pageable);

    Page<AssignmentGradeEntity> findByStudentUserId(UUID studentUserId, Pageable pageable);

    @Query("select a from AssignmentGradeEntity a where a.courseId = ?1")
    List<AssignmentGradeEntity> findByCourseId(UUID courseId);
}
