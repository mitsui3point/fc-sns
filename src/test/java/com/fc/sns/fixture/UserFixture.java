package com.fc.sns.fixture;

import com.fc.sns.model.entity.User;

public class UserFixture {
    public static User get() {
        return new User(1L, "userName", "password");
    }
}
