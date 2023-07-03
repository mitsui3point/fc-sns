package com.fc.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fc.sns.controller.request.UserJoinRequest;
import com.fc.sns.controller.request.UserLoginRequest;
import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.model.UserDto;
import com.fc.sns.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ImportResource(locations = {"com.fc.sns.controller.*", "com.fc.sns.service.*", "com.fc.sns.repository.*"})
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void 회원가입() throws Exception {
        //given
        String userName = "userName";
        String password = "password";

        when(userService.join(userName, password)).thenReturn(mock(UserDto.class));

        //when
        mvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserJoinRequest(userName, password)))
                ).andDo(print())
                //then
                .andExpect(status().isOk());
    }

    @Test
    void 회원가입시_이미_회원가입된_userName_으로_회원가입을_하는경우_에러반환() throws Exception {
        //given
        String userName = "userName";
        String password = "password";

        when(userService.join(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName)));

        //when
        mvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserJoinRequest(userName, password)))
                ).andDo(print())
                //then
                .andExpect(status().isConflict());
    }

    @Test
    void 회원가입시_userName_공백_or_빈값으로_회원가입을_하는경우_에러반환() throws Exception {
        //given
        String userName = " ";
        String password = "password";

        when(userService.join(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.NOT_ALLOWED_INVALID_USER_NAME, String.format("User name not allowed empty")));

        //when
        mvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserJoinRequest(userName, password)))
                ).andDo(print())
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입시_password_공백_or_빈값으로_회원가입을_하는경우_에러반환() throws Exception {
        //given
        String userName = "userName";
        String password = " ";

        when(userService.join(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.NOT_ALLOWED_INVALID_PASSWORD, String.format("Password not allowed empty")));

        //when
        mvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserJoinRequest(userName, password)))
                ).andDo(print())
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void 로그인() throws Exception {
        //given
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenReturn("test_token");

        //when
        mvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserLoginRequest(userName, password)))
                ).andDo(print())
                //then
                .andExpect(status().isOk());
    }

    @Test
    void 로그인시_회원가입이_안된_userName을_입력할_경우() throws Exception {
        //given
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not founded", userName)));

        //when
        mvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserLoginRequest(userName, password)))
                ).andDo(print())
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    void 로그인시_잘못된_password를_입력할_경우() throws Exception {
        //given
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.INVALID_PASSWORD));

        //when
        mvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserLoginRequest(userName, password)))
                ).andDo(print())
                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 알람기능() throws Exception {
        when(userService.alarms(any(), any())).thenReturn(Page.empty());
        setAuthentication();
        mvc.perform(get("/api/v1/users/alarm")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 알람기능요청시_로그인하지_않은_경우() throws Exception {
        when(userService.alarms(any(), any())).thenReturn(Page.empty());
        mvc.perform(get("/api/v1/users/alarm")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private void setAuthentication() {
        UserDto userDto = mock(UserDto.class);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                userDto,
                null,
                userDto.getAuthorities()
        ));
    }
}
