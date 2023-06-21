package com.fc.sns.service;

import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.model.UserDto;
import com.fc.sns.model.entity.User;
import com.fc.sns.repository.UserRepository;
import com.fc.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token-expired-time-ms}")
    private Long expiredTimeMs;

    @Transactional
    public UserDto join(String userName, String password) {

        // 회원 가입 하려는 userName 으로 회원가입된 user 가 있는지
        userRepository.findByUserName(userName)
                .ifPresent(user -> {
                    throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
                });
        // 회원 가입 진행 = user를 등록
        User savedUser = userRepository.save(User
                .builder()
                .userName(userName)
                .password(encoder.encode(password))
                .build());

        return UserDto.fromEntity(savedUser);
    }

    // TODO : implement
    public String login(String userName, String password) {
        // 회원가입 여부 체크
        User user = userRepository.findByUserName(userName).orElseThrow(() -> {
            throw new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not founded", userName));
        });

        // 비밀번호 체크
        if (!encoder.matches(password, user.getPassword())) {
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        return JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);
    }
}
