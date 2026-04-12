package com.lms.learning.infrastructure.persistence.repository;

import com.lms.learning.infrastructure.persistence.entity.AssignmentGradeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAssignmentGradeRepository extends JpaRepository<AssignmentGradeEntity, UUID> {

    Optional<AssignmentGradeEntity> findByAssignmentIdAndStudentUserId(UUID assignmentId, UUID studentUserId);

    Page<AssignmentGradeEntity> findByAssignmentId(UUID assignmentId, Pageable pageable);

    Page<AssignmentGradeEntity> findByStudentUserId(UUID studentUserId, Pageable pageable);

    List<AssignmentGradeEntity> findByCourseId(UUID courseId);

    @Modifying
    @Query("DELETE FROM AssignmentGradeEntity g WHERE g.courseId = :courseId")
    void deleteByCourseId(UUID courseId);
}
