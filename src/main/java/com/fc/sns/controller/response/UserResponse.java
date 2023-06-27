package com.fc.sns.controller.response;

import com.fc.sns.enums.UserRole;
import com.fc.sns.model.UserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserResponse {
    private Long id;
    private String name;
    private UserRole role;

    @Builder
    public UserResponse(Long id, String name, UserRole role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public static UserResponse fromUserDto(UserDto userDto) {
        return UserResponse.builder()
                .id(userDto.getId())
                .name(userDto.getUsername())
                .role(userDto.getRole())
                .build();
    }
}
