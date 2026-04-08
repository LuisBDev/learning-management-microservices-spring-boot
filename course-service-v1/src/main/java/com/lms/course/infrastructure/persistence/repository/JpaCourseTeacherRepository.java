package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.CourseTeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaCourseTeacherRepository extends JpaRepository<CourseTeacherEntity, UUID> {
    List<CourseTeacherEntity> findByCourseId(UUID courseId);
    
    @Modifying
    @Query("DELETE FROM CourseTeacherEntity t WHERE t.course.id = :courseId")
    void deleteByCourseId(UUID courseId);
}
