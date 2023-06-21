package com.fc.sns.service;

import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.fixture.UserFixture;
import com.fc.sns.model.entity.User;
import com.fc.sns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @Test
    void 회원가입이_정상적으로_동작하는_경우() {
        String userName = "userName";
        String password = "password";
        User fixture = UserFixture.get();

        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userRepository.save(any())).thenReturn(fixture);

        Assertions.assertDoesNotThrow(() -> {
            userService.join(userName, password);
        });
    }

    @Test
    void 회원가입시_userName으로_회원가입한_유저가_이미_있는경우() {
        String userName = "userName";
        String password = "password";
        User fixture = UserFixture.get();

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userRepository.save(any())).thenReturn(fixture);

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            userService.join(userName, password);
        });
        Assertions.assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());
    }

    @Test
    void 로그인이_정상적으로_동작하는_경우() {
        String userName = "userName";
        String password = "password";
        User fixture = UserFixture.get();

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.matches(any(), any())).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> {
            String token = userService.login(userName, password);
        });
    }

    @Test
    void 로그인_userName이_없는_경우() {
        String userName = "userName";
        String password = "password";

        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(encoder.matches(any(), any())).thenReturn(false);

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            userService.login(userName, password);
        });
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 로그인_password가_일치하지_않는_경우() {
        String userName = "userName";
        String password = "password";
        User fixture = UserFixture.get();

        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            userService.login(userName, password);
        });
        Assertions.assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
    }
}