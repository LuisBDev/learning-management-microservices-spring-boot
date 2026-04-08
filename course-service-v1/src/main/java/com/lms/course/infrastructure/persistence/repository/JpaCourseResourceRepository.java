package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.CourseResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaCourseResourceRepository extends JpaRepository<CourseResourceEntity, UUID> {
    List<CourseResourceEntity> findByCourseId(UUID courseId);
    List<CourseResourceEntity> findBySectionIdOrderByPositionAsc(UUID sectionId);

    @Modifying
    @Query("DELETE FROM CourseResourceEntity r WHERE r.course.id = :courseId")
    void deleteByCourseId(UUID courseId);
}
