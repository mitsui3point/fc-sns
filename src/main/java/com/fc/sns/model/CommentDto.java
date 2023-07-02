package com.fc.sns.model;

import com.fc.sns.model.entity.Comment;
import lombok.*;

import java.sql.Timestamp;

@Getter
@EqualsAndHashCode
public class CommentDto {
    private final Long id;

    private final String comment;

    private final Long postId;

    private final String userName;

    private final Timestamp registeredAt;

    private final Timestamp updatedAt;

    private final Timestamp deletedAt;

    @Builder
    public CommentDto(Long id, String comment, Long postId, String userName, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        this.id = id;
        this.comment = comment;
        this.postId = postId;
        this.userName = userName;
        this.registeredAt = registeredAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    protected CommentDto() {
        this.id = null;
        this.comment = null;
        this.postId = null;
        this.userName = null;
        this.registeredAt = null;
        this.updatedAt = null;
        this.deletedAt = null;
    }

    public static CommentDto fromEntity(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .postId(comment.getPost().getId())
                .userName(comment.getUser().getUserName())
                .registeredAt(comment.getRegisteredAt())
                .updatedAt(comment.getUpdatedAt())
                .deletedAt(comment.getDeletedAt())
                .build();
    }
}
