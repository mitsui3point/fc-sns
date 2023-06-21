package com.fc.sns.model;


import com.fc.sns.enums.UserRole;
import com.fc.sns.model.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {
    private Long id;
    private String userName;
    private String password;
    private UserRole role;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    @Builder
    public UserDto(Long id, String userName, String password, UserRole role, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.registeredAt = registeredAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .password(user.getPassword())
                .role(user.getRole())
                .registeredAt(user.getRegisteredAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }

}
