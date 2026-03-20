package com.lms.identity.security;

import com.lms.identity.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final JwtProperties jwtProperties;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtService(JwtProperties jwtProperties, TokenBlacklistService tokenBlacklistService) {
        this.jwtProperties = jwtProperties;
        this.tokenBlacklistService = tokenBlacklistService;
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey()));
    }

    public String generateAccessToken(UserDetails userDetails) {
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        List<String> roles = authorities.stream()
                .filter(a -> a.startsWith("ROLE_"))
                .toList();

        List<String> permissions = authorities.stream()
                .filter(a -> !a.startsWith("ROLE_"))
                .toList();

        return buildToken(
                Map.of("roles", roles, "permissions", permissions, "token_type", "access"),
                userDetails.getUsername(),
                jwtProperties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(
                Map.of("token_type", "refresh"),
                userDetails.getUsername(),
                jwtProperties.getRefreshTokenExpiration());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            final String jti = extractJti(token);
            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token)
                    && !tokenBlacklistService.isBlacklisted(jti);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            String tokenType = extractAllClaims(token).get("token_type", String.class);
            return "refresh".equals(tokenType);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            String tokenType = extractAllClaims(token).get("token_type", String.class);
            return "access".equals(tokenType);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(extraClaims)
                .id(UUID.randomUUID().toString())
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(signingKey)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
