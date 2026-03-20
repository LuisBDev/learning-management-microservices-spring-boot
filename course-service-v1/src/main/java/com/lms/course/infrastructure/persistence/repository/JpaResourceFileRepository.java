package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.ResourceFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaResourceFileRepository extends JpaRepository<ResourceFileEntity, UUID> {

    Optional<ResourceFileEntity> findByResourceId(UUID resourceId);

}
