package com.lms.course.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityContextHelper {

    private SecurityContextHelper() {
    }

    public static JwtAuthenticatedUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtAuthenticatedUser user) {
            return user;
        }
        return null;
    }

    public static UUID getCurrentUserId() {
        JwtAuthenticatedUser user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

}
