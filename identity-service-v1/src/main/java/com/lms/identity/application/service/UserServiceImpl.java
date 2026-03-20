package com.lms.identity.application.service;

import com.lms.identity.controller.dto.response.UserResponse;
import com.lms.identity.controller.mapper.UserMapper;
import com.lms.identity.exception.ResourceNotFoundException;
import com.lms.identity.infrastructure.persistence.entity.UserEntity;
import com.lms.identity.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JpaUserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUserById(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toUserResponse(user);
    }

    @Transactional
    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }

}
