package com.lms.identity.infrastructure.persistence.repository;

import com.lms.identity.infrastructure.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPermissionRepository extends JpaRepository<PermissionEntity, Integer> {

}
