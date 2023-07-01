package com.fc.sns.fixture;

import com.fc.sns.model.entity.Like;
import com.fc.sns.model.entity.Post;
import com.fc.sns.model.entity.User;

public class LikeFixture {
    public static Like get(Post post, User user, Long likeId) {
        return Like.builder()
                .id(likeId)
                .post(post)
                .user(user)
                .build();
    }
}
