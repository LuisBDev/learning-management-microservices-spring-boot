package com.lms.enrollment.infrastructure.persistence.repository;

import com.lms.enrollment.infrastructure.persistence.entity.EnrollmentEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaEnrollmentEventRepository extends JpaRepository<EnrollmentEventEntity, UUID> {

    List<EnrollmentEventEntity> findByEnrollmentIdOrderByCreatedAtDesc(UUID enrollmentId);

}
