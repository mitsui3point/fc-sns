package com.fc.sns.service;

import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.fixture.UserFixture;
import com.fc.sns.model.UserDto;
import com.fc.sns.model.entity.User;
import com.fc.sns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

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

    @Test
    void 회원가입이_정상적으로_동작하는_경우() {
        //given
        String userName = "userName";
        String password = "password";
        User fixture = UserFixture.get();

        //when
        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userRepository.save(any())).thenReturn(fixture);

        //then
        Assertions.assertDoesNotThrow(() -> {
            userService.join(userName, password);
        });

        verify(userRepository, times(1)).findByUserName(userName);
        verify(encoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void 회원가입시_userName으로_회원가입한_유저가_이미_있는경우() {
        //given
        String userName = "userName";
        String password = "password";
        User fixture = UserFixture.get();

        //when
        lenient().when(userRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        lenient().when(encoder.encode(password)).thenReturn("encrypt_password");
        lenient().when(userRepository.save(any())).thenReturn(fixture);

        //then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            userService.join(userName, password);
        });
        Assertions.assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());

        verify(userRepository, times(1)).findByUserName(userName);
        verify(encoder, times(0)).encode(password);
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void 로그인이_정상적으로_동작하는_경우() {
        //given
        String userName = "userName";
        String password = "password";
        User fixture = UserFixture.get();

        //when
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.matches(any(), any())).thenReturn(true);

        //then
        Assertions.assertDoesNotThrow(() -> {
            String token = userService.login(userName, password);
        });
        verify(userRepository, times(1)).findByUserName(userName);
        verify(encoder, times(1)).matches(any(), any());
    }

    @Test
    void 로그인_userName이_없는_경우() {
        //given
        String userName = "userName";
        String password = "password";

        //when
        lenient().when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());
        lenient().when(encoder.matches(any(), any())).thenReturn(false);

        //then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            userService.login(userName, password);
        });
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(userName);
        verify(encoder, times(0)).matches(any(), any());
    }

    @Test
    void 로그인_password가_일치하지_않는_경우() {
        //given
        String userName = "userName";
        String password = "password";
        User fixture = UserFixture.get();

        //when
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        //then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            userService.login(userName, password);
        });
        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(userName);
    }

    @Test
    void 유저정보_유저아이디로_조회() {
        //given
        User fixture = UserFixture.get();
        String userName = fixture.getUserName();

        //when
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        //then
        UserDto result = userService.loadUserByUsername(userName);
        Assertions.assertEquals(result, UserDto.fromEntity(fixture));
        verify(userRepository, times(1)).findByUserName(userName);
    }

    @Test
    void 유저정보_유저아이디로_조회_실패() {
        User fixture = UserFixture.get();
        String userName = fixture.getUserName();

        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            userService.loadUserByUsername(userName);
        });
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(userName);
    }
}