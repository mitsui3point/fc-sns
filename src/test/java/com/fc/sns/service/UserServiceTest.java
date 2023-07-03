package com.fc.sns.service;

import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.fixture.UserFixture;
import com.fc.sns.model.UserDto;
import com.fc.sns.model.entity.User;
import com.fc.sns.repository.AlarmRepository;
import com.fc.sns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @MockBean
    private AlarmRepository alarmRepository;

    @ParameterizedTest
    @MethodSource("userFixtureSource")
    void 회원가입이_정상적으로_동작하는_경우(User user) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());
        when(encoder.encode(user.getPassword())).thenReturn("encrypt_password");
        when(userRepository.save(any())).thenReturn(user);

        //then
        Assertions.assertDoesNotThrow(() -> {
            userService.join(user.getUserName(), user.getPassword());
        });
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(encoder, times(1)).encode(user.getPassword());
        verify(userRepository, times(1)).save(any());
    }

    @ParameterizedTest
    @MethodSource("userFixtureSource")
    void 회원가입시_userName으로_회원가입한_유저가_이미_있는경우(User user) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));

        //then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            userService.join(user.getUserName(), user.getPassword());
        });
        Assertions.assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
    }

    @Test
    void 회원가입시_userName이_공백인_경우() {
        //given
        String userName = " ";
        String password = "password";

        //then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            //when
            userService.join(userName, password);
        });
        Assertions.assertEquals(ErrorCode.NOT_ALLOWED_INVALID_USER_NAME, e.getErrorCode());
    }

    @Test
    void 회원가입시_password가_공백인_경우() {
        //given
        String userName = "userName";
        String password = " ";

        //then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            //when
            userService.join(userName, password);
        });
        Assertions.assertEquals(ErrorCode.NOT_ALLOWED_INVALID_PASSWORD, e.getErrorCode());
    }

    @ParameterizedTest
    @MethodSource("userFixtureSource")
    void 로그인이_정상적으로_동작하는_경우(User user) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(encoder.matches(any(), any())).thenReturn(true);

        //then
        Assertions.assertDoesNotThrow(() -> {
            String token = userService.login(user.getUserName(), user.getPassword());
        });
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(encoder, times(1)).matches(any(), any());
    }

    @ParameterizedTest
    @MethodSource("userFixtureSource")
    void 로그인_userName이_없는_경우(User user) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            userService.login(user.getUserName(), user.getPassword());
        });
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
    }

    @ParameterizedTest
    @MethodSource("userFixtureSource")
    void 로그인_password가_일치하지_않는_경우(User user) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));

        //then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            userService.login(user.getUserName(), user.getPassword());
        });
        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
    }

    @ParameterizedTest
    @MethodSource("userFixtureSource")
    void 유저정보_유저아이디로_조회(User user) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));

        //then
        UserDto result = userService.loadUserByUsername(user.getUserName());
        Assertions.assertEquals(result, UserDto.fromEntity(user));
        verify(userRepository, times(1)).findByUserName(user.getUserName());
    }

    @ParameterizedTest
    @MethodSource("userFixtureSource")
    void 유저정보_유저아이디로_조회_실패(User user) {
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            userService.loadUserByUsername(user.getUserName());
        });
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
    }

    @ParameterizedTest
    @MethodSource("userFixtureSource")
    void 알람목록(User user) {
        //when
        Page page = mock(Page.class);
        Pageable pageable = mock(Pageable.class);

        //TODO: to complete test
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(alarmRepository.findAllByUser(user, pageable)).thenReturn(page);
        Assertions.assertDoesNotThrow(() -> {
            userService.alarms(user.getUserName(), pageable);
        });

        //then
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(alarmRepository, times(1)).findAllByUser(user, pageable);
    }

    private static Stream<Arguments> userFixtureSource() {
        String userName = "userName";
        String password = "password";
        Long id = 1L;
        return Stream.of(
                Arguments.of(
                        UserFixture.get(userName, password, id)
                )
        );
    }
}