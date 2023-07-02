package com.fc.sns.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostModifyRequest {
    private final String title;
    private final String body;

    protected PostModifyRequest() {
        this.title = null;
        this.body = null;
    }
}
