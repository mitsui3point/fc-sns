package com.fc.sns.controller.response;

import com.fc.sns.model.PostDto;
import com.fc.sns.model.UserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponse {
    private Long id;

    private String title;

    private String body;

    private UserResponse userResponse;

    private Timestamp registeredAt;

    private Timestamp updatedAt;

    private Timestamp deletedAt;

    @Builder
    public PostResponse(Long id, String title, String body, UserResponse userResponse, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.userResponse = userResponse;
        this.registeredAt = registeredAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static PostResponse fromPostDto(PostDto postDto) {
        return PostResponse.builder()
                .id(postDto.getId())
                .title(postDto.getTitle())
                .body(postDto.getBody())
                .userResponse(UserResponse.fromUserDto(postDto.getUserDto()))
                .registeredAt(postDto.getRegisteredAt())
                .updatedAt(postDto.getUpdatedAt())
                .deletedAt(postDto.getDeletedAt())
                .build();
    }
}
