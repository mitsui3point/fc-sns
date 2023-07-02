package com.fc.sns.controller.response;

import com.fc.sns.enums.UserRole;
import com.fc.sns.model.UserDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponse {
    private final Long id;
    private final String name;
    private final UserRole role;

    @Builder
    public UserResponse(Long id, String name, UserRole role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    protected UserResponse() {
        this.id = null;
        this.name = null;
        this.role = null;
    }

    public static UserResponse fromUserDto(UserDto userDto) {
        return UserResponse.builder()
                .id(userDto.getId())
                .name(userDto.getUsername())
                .role(userDto.getRole())
                .build();
    }
}
