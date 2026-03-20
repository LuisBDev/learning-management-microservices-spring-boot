package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.ResourceTextEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaResourceTextRepository extends JpaRepository<ResourceTextEntity, UUID> {

    Optional<ResourceTextEntity> findByResourceId(UUID resourceId);

}
