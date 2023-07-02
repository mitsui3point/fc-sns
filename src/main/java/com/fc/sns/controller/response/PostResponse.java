package com.fc.sns.controller.response;

import com.fc.sns.model.PostDto;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class PostResponse {
    private final Long id;

    private final String title;

    private final String body;

    private final UserResponse user;

    private final Timestamp registeredAt;

    private final Timestamp updatedAt;

    private final Timestamp deletedAt;

    @Builder
    public PostResponse(Long id, String title, String body, UserResponse user, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
        this.registeredAt = registeredAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    protected PostResponse() {
        this.id = null;
        this.title = null;
        this.body = null;
        this.user = null;
        this.registeredAt = null;
        this.updatedAt = null;
        this.deletedAt = null;
    }

    public static PostResponse fromPostDto(PostDto postDto) {
        return PostResponse.builder()
                .id(postDto.getId())
                .title(postDto.getTitle())
                .body(postDto.getBody())
                .user(UserResponse.fromUserDto(postDto.getUserDto()))
                .registeredAt(postDto.getRegisteredAt())
                .updatedAt(postDto.getUpdatedAt())
                .deletedAt(postDto.getDeletedAt())
                .build();
    }
}
