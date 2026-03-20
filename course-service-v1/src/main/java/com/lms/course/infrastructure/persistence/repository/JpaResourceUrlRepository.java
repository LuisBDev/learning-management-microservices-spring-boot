package com.lms.course.infrastructure.persistence.repository;

import com.lms.course.infrastructure.persistence.entity.ResourceUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaResourceUrlRepository extends JpaRepository<ResourceUrlEntity, UUID> {

    Optional<ResourceUrlEntity> findByResourceId(UUID resourceId);

}
