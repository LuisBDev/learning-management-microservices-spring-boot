package com.lms.enrollment.infrastructure.client.identityservicev1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentityServiceClient {

    private final RestClient identityRestClient;

    public boolean userExists(UUID userId) {
        log.info("[INTER-SERVICE] Checking existence of user with ID: {} from Identity Service", userId);
        try {
            return identityRestClient.get()
                    .uri("/api/v1/users/{id}", userId)
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode()
                    .is2xxSuccessful();
        } catch (Exception e) {
            log.error("Error checking user existence for ID {}: {}", userId, e.getMessage());
            return false;
        }
    }
}
