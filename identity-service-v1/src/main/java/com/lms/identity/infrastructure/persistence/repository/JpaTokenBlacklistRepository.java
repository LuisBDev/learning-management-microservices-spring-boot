package com.lms.identity.infrastructure.persistence.repository;

import com.lms.identity.infrastructure.persistence.entity.TokenBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.UUID;

public interface JpaTokenBlacklistRepository extends JpaRepository<TokenBlacklistEntity, UUID> {

    boolean existsByJti(String jti);

    @Modifying
    @Query("DELETE FROM TokenBlacklistEntity t WHERE t.expiration < :now")
    int deleteExpiredTokens(Instant now);

}
