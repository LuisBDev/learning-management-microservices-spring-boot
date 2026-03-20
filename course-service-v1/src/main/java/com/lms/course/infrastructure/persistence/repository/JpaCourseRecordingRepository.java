package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.CourseRecordingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaCourseRecordingRepository extends JpaRepository<CourseRecordingEntity, UUID> {

    Optional<CourseRecordingEntity> findByResourceUrlId(UUID resourceUrlId);

}
