package com.lms.enrollment.infrastructure.persistence.repository;

import com.lms.enrollment.infrastructure.persistence.entity.CourseEnrollmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaCourseEnrollmentRepository extends JpaRepository<CourseEnrollmentEntity, UUID> {

    Page<CourseEnrollmentEntity> findByCourseId(UUID courseId, Pageable pageable);

    Page<CourseEnrollmentEntity> findByStudentUserId(UUID studentUserId, Pageable pageable);

    Optional<CourseEnrollmentEntity> findByCourseIdAndStudentUserId(UUID courseId, UUID studentUserId);

    boolean existsByCourseIdAndStudentUserId(UUID courseId, UUID studentUserId);

}
