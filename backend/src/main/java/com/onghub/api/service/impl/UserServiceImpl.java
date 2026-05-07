package com.onghub.api.service.impl;

import com.onghub.api.dto.response.UserResponse;
import com.onghub.api.entity.User;
import com.onghub.api.exception.ResourceNotFoundException;
import com.onghub.api.mapper.UserMapper;
import com.onghub.api.repository.UserRepository;
import com.onghub.api.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }
}
