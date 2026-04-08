package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.AssignmentMaterialFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaAssignmentMaterialFileRepository extends JpaRepository<AssignmentMaterialFileEntity, UUID> {
    List<AssignmentMaterialFileEntity> findByAssignmentId(UUID assignmentId);

    @Modifying
    @Query("DELETE FROM AssignmentMaterialFileEntity amf WHERE amf.assignment.id IN " +
           "(SELECT a.id FROM AssignmentEntity a WHERE a.resource.id IN " +
           "(SELECT r.id FROM CourseResourceEntity r WHERE r.course.id = :courseId))")
    void deleteByCourseId(UUID courseId);
}
