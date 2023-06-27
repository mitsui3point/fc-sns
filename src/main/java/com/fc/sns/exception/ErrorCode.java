package com.fc.sns.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "User name is duplicated"),
    NOT_ALLOWED_INVALID_USER_NAME(HttpStatus.BAD_REQUEST, "User name is not empty"),
    NOT_ALLOWED_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "Password is not empty"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Password is invalid"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token is invalid"),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post not founded"),
    INVALID_POST_PERMISSION(HttpStatus.UNAUTHORIZED, "Post permission is invalid"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    ;

    private HttpStatus status;
    private String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
