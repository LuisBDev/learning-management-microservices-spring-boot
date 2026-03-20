package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.CourseResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaCourseResourceRepository extends JpaRepository<CourseResourceEntity, UUID> {

    List<CourseResourceEntity> findBySectionIdOrderByPositionAsc(UUID sectionId);

    List<CourseResourceEntity> findByCourseId(UUID courseId);

}
