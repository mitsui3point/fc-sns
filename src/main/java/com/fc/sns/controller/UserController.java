package com.fc.sns.controller;

import com.fc.sns.controller.request.UserJoinRequest;
import com.fc.sns.controller.request.UserLoginRequest;
import com.fc.sns.controller.response.AlarmResponse;
import com.fc.sns.controller.response.Response;
import com.fc.sns.controller.response.UserJoinResponse;
import com.fc.sns.controller.response.UserLoginResponse;
import com.fc.sns.model.UserDto;
import com.fc.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userJoinRequest) {
        UserDto userDto = userService.join(userJoinRequest.getName(), userJoinRequest.getPassword());
        return Response.success(UserJoinResponse.fromUserDto(userDto));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        String token = userService.login(userLoginRequest.getName(), userLoginRequest.getPassword());
        return Response.success(UserLoginResponse.fromToken(token));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication) {
        Page<AlarmResponse> alarmResponse = userService.alarms(authentication.getName(), pageable).map(AlarmResponse::fromAlarmDto);
        return Response.success(alarmResponse);
    }
}