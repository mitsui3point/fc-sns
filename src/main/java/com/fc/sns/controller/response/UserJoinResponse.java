package com.fc.sns.controller.response;

import com.fc.sns.enums.UserRole;
import com.fc.sns.model.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserJoinResponse {
    private final Long id;
    private final String name;
    private final UserRole role;

//    @Builder
//    public UserJoinResponse(Long id, String name, UserRole role) {
//        this.id = id;
//        this.name = name;
//        this.role = role;
//    }

//    protected UserJoinResponse() {
//        this.id = null;
//        this.name = null;
//        this.role = null;
//    }

    public static UserJoinResponse fromUserDto(UserDto userDto) {
        return new UserJoinResponse(userDto.getId(), userDto.getUsername(), userDto.getRole());
//        return UserJoinResponse
//                .builder()
//                .id(userDto.getId())
//                .name(userDto.getUsername())
//                .role(userDto.getRole())
//                .build();
    }
}
