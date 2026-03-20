package com.lms.identity.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "token_blacklist")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlacklistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String jti;

    @Column(nullable = false)
    private Instant expiration;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

}
