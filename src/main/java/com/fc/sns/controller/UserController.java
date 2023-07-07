package com.fc.sns.controller;

import com.fc.sns.controller.request.UserJoinRequest;
import com.fc.sns.controller.request.UserLoginRequest;
import com.fc.sns.controller.response.AlarmResponse;
import com.fc.sns.controller.response.Response;
import com.fc.sns.controller.response.UserJoinResponse;
import com.fc.sns.controller.response.UserLoginResponse;
import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.model.UserDto;
import com.fc.sns.service.AlarmService;
import com.fc.sns.service.UserService;
import com.fc.sns.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AlarmService alarmService;

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
        UserDto userDto = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), UserDto.class).orElseThrow(() -> {
            throw new SnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Casting to User class failed");
        });
        Page<AlarmResponse> alarmResponse = userService.alarms(userDto.getId(), pageable).map(AlarmResponse::fromAlarmDto);
        return Response.success(alarmResponse);
    }

    @GetMapping("/alarm/subscribe")
    public SseEmitter subscribe(Authentication authentication) {
        UserDto userDto = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), UserDto.class).orElseThrow(() -> {
            throw new SnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Casting to User class failed");
        });
        return alarmService.connectAlarm(userDto.getId());
    }
}