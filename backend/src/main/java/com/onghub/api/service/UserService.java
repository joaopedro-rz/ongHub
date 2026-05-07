package com.onghub.api.service;

import com.onghub.api.dto.response.UserResponse;

public interface UserService {
    UserResponse getCurrentUser(String email);
}
