package com.lms.enrollment.infrastructure.persistence.repository;

import com.lms.enrollment.infrastructure.persistence.entity.CourseEnrollmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCourseEnrollmentRepository extends JpaRepository<CourseEnrollmentEntity, UUID> {

    Optional<CourseEnrollmentEntity> findByCourseIdAndStudentUserId(UUID courseId, UUID studentUserId);

    Page<CourseEnrollmentEntity> findByCourseId(UUID courseId, Pageable pageable);

    Page<CourseEnrollmentEntity> findByStudentUserId(UUID studentUserId, Pageable pageable);

    boolean existsByCourseIdAndStudentUserId(UUID courseId, UUID studentUserId);
    
    @Modifying
    @Query("DELETE FROM CourseEnrollmentEntity e WHERE e.courseId = :courseId")
    void deleteByCourseId(UUID courseId);
}
