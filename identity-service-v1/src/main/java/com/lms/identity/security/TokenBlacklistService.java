package com.lms.identity.security;

import com.lms.identity.infrastructure.persistence.entity.TokenBlacklistEntity;
import com.lms.identity.infrastructure.persistence.repository.JpaTokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final JpaTokenBlacklistRepository tokenBlacklistRepository;

    @Transactional
    public void blacklist(String jti, Instant expiration) {
        if (tokenBlacklistRepository.existsByJti(jti)) {
            log.debug("Token JTI {} already blacklisted", jti);
            return;
        }

        TokenBlacklistEntity entry = TokenBlacklistEntity.builder()
                .jti(jti)
                .expiration(expiration)
                .build();

        tokenBlacklistRepository.save(entry);
        log.info("Token blacklisted with JTI: {}", jti);
    }

    @Transactional(readOnly = true)
    public boolean isBlacklisted(String jti) {
        return tokenBlacklistRepository.existsByJti(jti);
    }

    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void cleanupExpiredTokens() {
        int deleted = tokenBlacklistRepository.deleteExpiredTokens(Instant.now());
        if (deleted > 0) {
            log.info("Cleaned up {} expired blacklisted tokens", deleted);
        }
    }

}
