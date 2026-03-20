package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.AssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaAssignmentRepository extends JpaRepository<AssignmentEntity, UUID> {

    Optional<AssignmentEntity> findByResourceId(UUID resourceId);

}
