package com.lms.identity.controller.mapper;

import com.lms.identity.controller.dto.response.UserResponse;
import com.lms.identity.infrastructure.persistence.entity.PermissionEntity;
import com.lms.identity.infrastructure.persistence.entity.RoleEntity;
import com.lms.identity.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToNames")
    @Mapping(target = "permissions", source = "roles", qualifiedByName = "rolesToPermissions")
    UserResponse toUserResponse(UserEntity user);

    @Named("rolesToNames")
    default Set<String> rolesToNames(Set<RoleEntity> roles) {
        if (roles == null) return Collections.emptySet();
        return roles.stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
    }

    @Named("rolesToPermissions")
    default Set<String> rolesToPermissions(Set<RoleEntity> roles) {
        if (roles == null) return Collections.emptySet();
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(PermissionEntity::getName)
                .collect(Collectors.toSet());
    }

}
