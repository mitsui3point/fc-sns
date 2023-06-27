package com.fc.sns.model;

import com.fc.sns.model.entity.Post;
import lombok.*;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class PostDto {
    private Long id;

    private String title;

    private String body;

    private UserDto userDto;

    private Timestamp registeredAt;

    private Timestamp updatedAt;

    private Timestamp deletedAt;

    @Builder
    public PostDto(Long id, String title, String body, UserDto userDto, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.userDto = userDto;
        this.registeredAt = registeredAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static PostDto fromEntity(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userDto(UserDto.fromEntity(post.getUser()))
                .registeredAt(post.getRegisteredAt())
                .updatedAt(post.getUpdatedAt())
                .deletedAt(post.getDeletedAt())
                .build();
    }
}
