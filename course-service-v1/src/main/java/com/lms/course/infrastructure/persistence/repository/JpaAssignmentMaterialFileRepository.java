package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.AssignmentMaterialFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaAssignmentMaterialFileRepository extends JpaRepository<AssignmentMaterialFileEntity, UUID> {

    List<AssignmentMaterialFileEntity> findByAssignmentId(UUID assignmentId);

}
