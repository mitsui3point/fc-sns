package com.fc.sns.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCreateRequest {
    private final String title;
    private final String body;

    protected PostCreateRequest() {
        this.title = null;
        this.body = null;
    }
}
