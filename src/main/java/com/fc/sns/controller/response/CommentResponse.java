package com.fc.sns.controller.response;

import com.fc.sns.model.CommentDto;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class CommentResponse {
    private final Long id;

    private final String comment;

    private final Long postId;

    private final String userName;

    private final Timestamp registeredAt;

    private final Timestamp updatedAt;

    private final Timestamp deletedAt;

    @Builder
    public CommentResponse(Long id, String comment, Long postId, String userName, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        this.id = id;
        this.comment = comment;
        this.postId = postId;
        this.userName = userName;
        this.registeredAt = registeredAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    protected CommentResponse() {
        this.id = null;
        this.comment = null;
        this.postId = null;
        this.userName = null;
        this.registeredAt = null;
        this.updatedAt = null;
        this.deletedAt = null;
    }

    public static CommentResponse fromCommentDto(CommentDto commentDto) {
        return CommentResponse.builder()
                .id(commentDto.getId())
                .comment(commentDto.getComment())
                .postId(commentDto.getPostId())
                .userName(commentDto.getUserName())
                .registeredAt(commentDto.getRegisteredAt())
                .updatedAt(commentDto.getUpdatedAt())
                .deletedAt(commentDto.getDeletedAt())
                .build();
    }
}
