package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.CourseTeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaCourseTeacherRepository extends JpaRepository<CourseTeacherEntity, UUID> {

    List<CourseTeacherEntity> findByCourseId(UUID courseId);

    boolean existsByCourseIdAndTeacherUserId(UUID courseId, UUID teacherUserId);

}
