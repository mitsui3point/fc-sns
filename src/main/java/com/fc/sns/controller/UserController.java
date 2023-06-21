package com.fc.sns.controller;

import com.fc.sns.controller.request.UserJoinRequest;
import com.fc.sns.controller.request.UserLoginRequest;
import com.fc.sns.controller.response.Response;
import com.fc.sns.controller.response.UserJoinResponse;
import com.fc.sns.controller.response.UserLoginResponse;
import com.fc.sns.model.UserDto;
import com.fc.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    public final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userJoinRequest) {
        UserDto userDto = userService.join(userJoinRequest.getName(), userJoinRequest.getPassword());
        return Response.success(UserJoinResponse.fromUserDto(userDto));
    }

    // TODO : implement
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        String token = userService.login(userLoginRequest.getName(), userLoginRequest.getPassword());
        return Response.success(UserLoginResponse.fromToken(token));
    }
}