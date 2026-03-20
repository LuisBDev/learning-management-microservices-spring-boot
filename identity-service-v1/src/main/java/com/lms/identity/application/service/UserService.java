package com.lms.identity.application.service;

import com.lms.identity.controller.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(UUID id);

    void deleteUser(UUID id);

}
