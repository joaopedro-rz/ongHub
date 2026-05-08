package com.onghub.api.service.impl;

import com.onghub.api.dto.request.UserProfileUpdateRequest;
import com.onghub.api.dto.response.UserResponse;
import com.onghub.api.entity.User;
import com.onghub.api.exception.ResourceNotFoundException;
import com.onghub.api.mapper.UserMapper;
import com.onghub.api.repository.RefreshTokenRepository;
import com.onghub.api.repository.UserRepository;
import com.onghub.api.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserServiceImpl(
        UserRepository userRepository,
        UserMapper userMapper,
        RefreshTokenRepository refreshTokenRepository
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        return userMapper.toResponse(requireActiveUser(email));
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String email, UserProfileUpdateRequest request) {
        User user = requireActiveUser(email);
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name().trim());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone().isBlank() ? null : request.phone().trim());
        }
        if (request.profileImageUrl() != null) {
            user.setProfileImageUrl(request.profileImageUrl().isBlank() ? null : request.profileImageUrl().trim());
        }
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deactivateAccount(String email) {
        User user = requireActiveUser(email);
        user.setDeletedAt(LocalDateTime.now());
        user.setEnabled(false);
        refreshTokenRepository.revokeAllActiveForUser(user.getId());
        userRepository.save(user);
    }

    private User requireActiveUser(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getDeletedAt() != null) {
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }
}
