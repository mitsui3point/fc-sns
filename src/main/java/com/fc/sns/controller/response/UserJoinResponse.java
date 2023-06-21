package com.fc.sns.controller.response;

import com.fc.sns.enums.UserRole;
import com.fc.sns.model.UserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserJoinResponse {
    private Long id;
    private String name;
    private UserRole role;

    @Builder
    public UserJoinResponse(Long id, String name, UserRole role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public static UserJoinResponse fromUserDto(UserDto userDto) {
        return UserJoinResponse
                .builder()
                .id(userDto.getId())
                .name(userDto.getUserName())
                .role(userDto.getRole())
                .build();
    }
}
