package com.fc.sns.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response<T> {
    private final String resultCode;
    private final T result;

    public static Response<Void> error(String errorCode) {
        return new Response<>(errorCode, null);
    }

    public static <T> Response<T> success(T result) {
        return new Response<>("SUCCESS", result);
    }

    public static Response<Void> success() {
        return new Response<>("SUCCESS", null);
    }

    public String toStream() {
        if (result == null) {
            return "{" +
                    "\"resultCode\":" +
                    "\"" + resultCode + "\"," +
                    "\"result\":" + null + "}";
        }
        return "{" +
                "\"resultCode\":" +
                "\"" + resultCode + "\"," +
                "\"result\":" + "\"" + result + "\"}";
    }
}
