package com.fc.sns.fixture;

import com.fc.sns.model.entity.Post;

public class PostFixture {
    public static Post get() {
        return Post.builder().id(1L).title("title").body("body").user(UserFixture.get()).build();
    }
}
