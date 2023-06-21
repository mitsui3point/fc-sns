package com.fc.sns.controller.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserLoginResponse {
    private String token;

    @Builder
    public UserLoginResponse(String token) {
        this.token = token;
    }

    public static UserLoginResponse fromToken(String token) {
        return UserLoginResponse.builder()
                .token(token)
                .build();
    }
}
