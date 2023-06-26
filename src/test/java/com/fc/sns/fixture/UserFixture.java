package com.fc.sns.fixture;

import com.fc.sns.model.entity.User;

public class UserFixture {
    public static User get(String userName, String password, Long id) {
        return User.builder()
                .id(id)
                .userName(userName)
                .password(password)
                .build();
    }

}
