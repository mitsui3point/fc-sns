package com.fc.sns.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public class UserJoinRequest {
    private final String name;
    private final String password;

    protected UserJoinRequest() {
        this.name = null;
        this.password = null;
    }
}
