package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.CourseRecordingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCourseRecordingRepository extends JpaRepository<CourseRecordingEntity, UUID> {
    
    Optional<CourseRecordingEntity> findByResourceUrlId(UUID resourceUrlId);

    @Modifying
    @Query("DELETE FROM CourseRecordingEntity r WHERE r.resourceUrl.resource.course.id = :courseId")
    void deleteByCourseId(UUID courseId);
}
