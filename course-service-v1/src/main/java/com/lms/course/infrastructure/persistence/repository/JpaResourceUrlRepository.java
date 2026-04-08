package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.ResourceUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaResourceUrlRepository extends JpaRepository<ResourceUrlEntity, UUID> {
    Optional<ResourceUrlEntity> findByResourceId(UUID resourceId);

    @Modifying
    @Query("DELETE FROM ResourceUrlEntity ru WHERE ru.resource.id IN " +
           "(SELECT r.id FROM CourseResourceEntity r WHERE r.course.id = :courseId)")
    void deleteByCourseId(UUID courseId);
}
