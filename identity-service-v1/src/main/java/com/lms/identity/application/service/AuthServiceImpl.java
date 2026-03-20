package com.lms.identity.application.service;

import com.lms.identity.config.JwtProperties;
import com.lms.identity.controller.dto.request.LoginRequest;
import com.lms.identity.controller.dto.request.RefreshTokenRequest;
import com.lms.identity.controller.dto.request.RegisterRequest;
import com.lms.identity.controller.dto.response.AuthResponse;
import com.lms.identity.exception.EmailAlreadyExistsException;
import com.lms.identity.exception.ResourceNotFoundException;
import com.lms.identity.exception.TokenException;
import com.lms.identity.infrastructure.persistence.entity.RoleEntity;
import com.lms.identity.infrastructure.persistence.entity.UserEntity;
import com.lms.identity.infrastructure.persistence.repository.JpaRoleRepository;
import com.lms.identity.infrastructure.persistence.repository.JpaUserRepository;
import com.lms.identity.security.CustomUserDetails;
import com.lms.identity.security.JwtService;
import com.lms.identity.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JpaUserRepository userRepository;
    private final JpaRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        RoleEntity studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_STUDENT"));

        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(Set.of(studentRole))
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return buildAuthResponse(userDetails);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("User logged in successfully: {}", userDetails.getUsername());

        return buildAuthResponse(userDetails);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new TokenException("Invalid token type: expected refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        CustomUserDetails userDetails = new CustomUserDetails(user);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new TokenException("Invalid or expired refresh token");
        }

        String oldJti = jwtService.extractJti(refreshToken);
        tokenBlacklistService.blacklist(oldJti, jwtService.extractExpiration(refreshToken).toInstant());
        log.info("Refresh token rotated for user: {}", username);

        return buildAuthResponse(userDetails);
    }

    @Override
    @Transactional
    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null) {
            try {
                String accessJti = jwtService.extractJti(accessToken);
                tokenBlacklistService.blacklist(accessJti, jwtService.extractExpiration(accessToken).toInstant());
            } catch (Exception e) {
                log.warn("Failed to blacklist access token: {}", e.getMessage());
            }
        }

        if (refreshToken != null) {
            try {
                String refreshJti = jwtService.extractJti(refreshToken);
                tokenBlacklistService.blacklist(refreshJti, jwtService.extractExpiration(refreshToken).toInstant());
            } catch (Exception e) {
                log.warn("Failed to blacklist refresh token: {}", e.getMessage());
            }
        }

        log.info("User logged out, tokens blacklisted");
    }

    private AuthResponse buildAuthResponse(CustomUserDetails userDetails) {
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();
    }

}
