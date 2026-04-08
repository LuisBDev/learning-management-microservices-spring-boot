package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.CourseSectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaCourseSectionRepository extends JpaRepository<CourseSectionEntity, UUID> {
    List<CourseSectionEntity> findByCourseIdOrderByPositionAsc(UUID courseId);

    @Modifying
    @Query("DELETE FROM CourseSectionEntity s WHERE s.course.id = :courseId")
    void deleteByCourseId(UUID courseId);
}
