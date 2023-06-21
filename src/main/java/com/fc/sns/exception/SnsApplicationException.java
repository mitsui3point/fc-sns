package com.fc.sns.exception;

import lombok.Getter;

import javax.persistence.Entity;

@Getter
public class SnsApplicationException extends RuntimeException {
    private ErrorCode errorCode;
    private String message;

    public SnsApplicationException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public SnsApplicationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = null;
    }

    @Override
    public String getMessage() {
        if (message == null) {
            message = errorCode.getMessage();
        }
        return String.format("%s. %s", errorCode.getMessage(), message);
    }

}
