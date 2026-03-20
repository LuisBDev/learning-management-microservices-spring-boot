package com.lms.identity.application.service;

import com.lms.identity.controller.dto.request.LoginRequest;
import com.lms.identity.controller.dto.request.RefreshTokenRequest;
import com.lms.identity.controller.dto.request.RegisterRequest;
import com.lms.identity.controller.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String accessToken, String refreshToken);

}
