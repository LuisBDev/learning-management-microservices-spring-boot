package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.CourseSectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaCourseSectionRepository extends JpaRepository<CourseSectionEntity, UUID> {

    List<CourseSectionEntity> findByCourseIdOrderByPositionAsc(UUID courseId);

}
