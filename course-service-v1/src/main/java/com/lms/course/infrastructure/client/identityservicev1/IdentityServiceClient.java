package com.lms.course.infrastructure.client.identityservicev1;

import com.lms.course.exception.ResourceNotFoundException;
import com.lms.course.infrastructure.client.identityservicev1.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentityServiceClient {

    private final RestClient identityRestClient;

    public UserResponse getUserById(UUID userId) {
        log.info("[INTER-SERVICE] Fetching user {} from Identity Service", userId);
        return identityRestClient.get()
                .uri("/api/v1/users/{id}", userId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResourceNotFoundException("Course", "id", userId);
                })
                .body(UserResponse.class);
    }


}
