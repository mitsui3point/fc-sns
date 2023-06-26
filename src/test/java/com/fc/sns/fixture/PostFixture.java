package com.fc.sns.fixture;

import com.fc.sns.model.entity.Post;
import com.fc.sns.model.entity.User;

public class PostFixture {
    public static Post get(String title, String body, User user, Long postId) {
        return Post.builder()
                .id(postId)
                .title(title)
                .body(body)
                .user(user)
                .build();
    }
}
