package com.fc.sns.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLoginRequest {
    private final String name;
    private final String password;

    protected UserLoginRequest(String name) {
        this.name = null;
        this.password = null;
    }
}
