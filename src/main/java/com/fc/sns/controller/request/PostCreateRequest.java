package com.fc.sns.controller.request;

import lombok.Getter;

@Getter
public class PostCreateRequest {
    private final String title;
    private final String body;

    public PostCreateRequest(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
