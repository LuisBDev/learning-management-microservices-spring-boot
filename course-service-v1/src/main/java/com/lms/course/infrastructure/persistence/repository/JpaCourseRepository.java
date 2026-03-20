package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaCourseRepository extends JpaRepository<CourseEntity, UUID> {

    Optional<CourseEntity> findByCode(String code);

    boolean existsByCode(String code);

}
