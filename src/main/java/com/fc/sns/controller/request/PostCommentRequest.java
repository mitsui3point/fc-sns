package com.fc.sns.controller.request;

import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class PostCommentRequest {

    private final String comment;

    public PostCommentRequest(String comment) {
        Assert.notNull(comment, "comment 는 빈값으로 작성할 수 없습니다.");
        // hasText -> " " False hasLength -> " " True

        this.comment = comment;
    }

    public PostCommentRequest() {
        this.comment = null;
    }
}
