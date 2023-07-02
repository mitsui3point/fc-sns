package com.fc.sns.fixture;

import com.fc.sns.model.entity.Comment;
import com.fc.sns.model.entity.Post;
import com.fc.sns.model.entity.User;

public class CommentFixture {
    public static Comment get(User user, Post post, String comment, Long commentId) {
        return Comment.builder()
                .id(commentId)
                .user(user)
                .comment(comment)
                .post(post)
                .build();
    }
}
