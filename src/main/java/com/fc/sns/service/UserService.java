package com.fc.sns.service;

import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.model.AlarmDto;
import com.fc.sns.model.UserDto;
import com.fc.sns.model.entity.User;
import com.fc.sns.repository.AlarmRepository;
import com.fc.sns.repository.UserCacheRepository;
import com.fc.sns.repository.UserRepository;
import com.fc.sns.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final String secretKey;
    private final Long expiredTimeMs;
    private final AlarmRepository alarmRepository;
    private final UserCacheRepository userCacheRepository;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder, @Value("${jwt.secret-key}") String secretKey, @Value("${jwt.token-expired-time-ms}") Long expiredTimeMs, AlarmRepository alarmRepository, UserCacheRepository userCacheRepository) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.secretKey = secretKey;
        this.expiredTimeMs = expiredTimeMs;
        this.alarmRepository = alarmRepository;
        this.userCacheRepository = userCacheRepository;
    }

    @Transactional
    public UserDto join(String userName, String password) {
        // 회원가입 정보 확인
        if (!StringUtils.hasText(userName.trim())) {
            throw new SnsApplicationException(ErrorCode.NOT_ALLOWED_INVALID_USER_NAME, String.format("User name not allowed empty"));
        }
        if (!StringUtils.hasText(password.trim())) {
            throw new SnsApplicationException(ErrorCode.NOT_ALLOWED_INVALID_PASSWORD, String.format("Password not allowed empty"));
        }
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

    public String login(String userName, String password) {
        // 회원가입 여부 체크
        UserDto userDto = loadUserByUsername(userName);

        // 캐싱
        userCacheRepository.setUserDto(userDto); // (캐싱 데이터 / DB 데이터) -> 캐싱

        // 비밀번호 체크
        if (!encoder.matches(password, userDto.getPassword())) {
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        return JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);
    }

    public UserDto loadUserByUsername(String userName) {
        // 캐싱된 데이터가 없으면 DB 조회
        return userCacheRepository.getUserDto(userName).orElseGet(() ->
                userRepository.findByUserName(userName)
                        .map(UserDto::fromEntity)
                        .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not founded", userName)))
        );
    }

    public Page<AlarmDto> alarms(Long userId, Pageable pageable) {
        return alarmRepository.findAllByUserId(userId, pageable)
                .map(AlarmDto::fromAlarm);
    }

}