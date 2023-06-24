package com.fc.sns.fixture;

import com.fc.sns.model.entity.User;

public class UserFixture {
    public static User get() {
        return User.builder()
                .id(1L)
                .userName("userName")
                .password("pasword")
                .build();
    }

}
