package com.onghub.api.mapper;

import com.onghub.api.dto.response.UserResponse;
import com.onghub.api.entity.Role;
import com.onghub.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserResponse toResponse(User user);

    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream()
            .map(role -> role.getName().name())
            .collect(Collectors.toSet());
    }
}
