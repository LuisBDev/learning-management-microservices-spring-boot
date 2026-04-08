package com.lms.enrollment.infrastructure.persistence.repository;

import com.lms.enrollment.infrastructure.persistence.entity.EnrollmentEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaEnrollmentEventRepository extends JpaRepository<EnrollmentEventEntity, UUID> {

    List<EnrollmentEventEntity> findByEnrollmentIdOrderByCreatedAtDesc(UUID enrollmentId);

    @Modifying
    @Query("DELETE FROM EnrollmentEventEntity ev WHERE ev.enrollment.id IN " +
           "(SELECT e.id FROM CourseEnrollmentEntity e WHERE e.courseId = :courseId)")
    void deleteByCourseId(UUID courseId);
}
