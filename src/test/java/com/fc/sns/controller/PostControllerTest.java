package com.fc.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fc.sns.controller.request.PostCreateRequest;
import com.fc.sns.controller.request.PostModifyRequest;
import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.fixture.PostFixture;
import com.fc.sns.fixture.UserFixture;
import com.fc.sns.model.PostDto;
import com.fc.sns.service.PostService;
import com.fc.sns.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@ImportResource(locations = {"com.fc.sns.controller.*", "com.fc.sns.service.*", "com.fc.sns.repository.*"})
public class PostControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    void 포스트작성() throws Exception {
        //given
        String title = "title";
        String body = "body";

        doNothing().when(postService).create(any(), any(), any());
        //when
        mvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PostCreateRequest(title, body)))
                ).andDo(print())
                //then
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 포스트작성시_로그인하지않은_경우() throws Exception {
        //given
        String title = "title";
        String body = "body";

        //when
        mvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PostCreateRequest(title, body)))
                ).andDo(print())
                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트수정() throws Exception {
        //given
        String title = "title";
        String body = "body";

        //when
        when(postService.modify(any(), eq(title), eq(body), any()))
                .thenReturn(PostDto.fromEntity(PostFixture.get(title, body, UserFixture.get("user", "password", 1L), 1L)));

        //when
        mvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PostModifyRequest(title, body)))
                ).andDo(print())
                //then
                .andExpect(status().isOk());
        verify(postService, times(1)).modify(any(), any(), any(), any());
    }

    @Test
    @WithAnonymousUser
    void 포스트수정시_로그인하지않은경우() throws Exception {
        //given
        String title = "title";
        String body = "body";

        //when
        mvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PostModifyRequest(title, body)))
                ).andDo(print())
                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트수정시_본인이_작성한_글이_아니라면_에러발생() throws Exception {
        //given
        String title = "title";
        String body = "body";

        doThrow(new SnsApplicationException(ErrorCode.INVALID_POST_PERMISSION)).when(postService).modify(eq(1L), eq(title), eq(body), any());

        //when
        mvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PostModifyRequest(title, body)))
                ).andDo(print())
                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트수정시_수정하려는_글이_없는경우_에러발생() throws Exception {
        //given
        String title = "title";
        String body = "body";

        //when
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).modify(eq(1L), eq(title), eq(body), any());

        mvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PostModifyRequest(title, body)))
                ).andDo(print())
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void 포스트삭제() throws Exception {
        //when
        doNothing().when(postService).delete(eq(1L), any());

        mvc.perform(delete("/api/v1/posts/1")
                ).andDo(print())
                //then
                .andExpect(status().isOk());
        verify(postService, times(1)).delete(any(), any());
    }

    @Test
    @WithAnonymousUser
    void 포스트삭제시_로그인하지않은경우() throws Exception {
        //when
        mvc.perform(delete("/api/v1/posts/1")
                ).andDo(print())
                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트삭제시_본인이_작성한_글이_아니라면_에러발생() throws Exception {
        //when
        doThrow(new SnsApplicationException(ErrorCode.INVALID_POST_PERMISSION)).when(postService).delete(eq(1L), any());

        mvc.perform(delete("/api/v1/posts/1")
                ).andDo(print())
                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트삭제시_삭제하려는_글이_없는경우_에러발생() throws Exception {
        //when
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).delete(eq(1L), any());

        mvc.perform(delete("/api/v1/posts/1")
                ).andDo(print())
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void 피드목록() throws Exception {
        //when
        when(postService.list(any())).thenReturn(mock(Page.class));
        mvc.perform(get("/api/v1/posts")
                ).andDo(print())
                //then
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 피드목록요청시_로그인하지않은_경우() throws Exception {
        //when
        mvc.perform(get("/api/v1/posts")
                ).andDo(print())
                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 내_피드목록() throws Exception {
        //when
        when(postService.my(any(), any())).thenReturn(mock(Page.class));
        mvc.perform(get("/api/v1/posts/my")
                ).andDo(print())
                //then
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 내_피드목록요청시_로그인하지않은_경우() throws Exception {
        mvc.perform(get("/api/v1/posts/my")
                ).andDo(print())
                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 좋아요기능() throws Exception {
        //when
        doNothing().when(postService).like(any(), any());

        mvc.perform(post("/api/v1/posts/1/likes")
                ).andDo(print())
                //then
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 좋아요기능요청시_로그인하지않은_경우() throws Exception {
        mvc.perform(post("/api/v1/posts/1/likes")
                ).andDo(print())
                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 좋아요기능요청시_게시물이_없는_경우() throws Exception {

        //when
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).doNothing().when(postService).like(any(), any());

        mvc.perform(post("/api/v1/posts/1/likes")
                ).andDo(print())
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void 좋아요기능요청시_이미_좋아요를_한_게시물일_경우() throws Exception {

        //when
        doThrow(new SnsApplicationException(ErrorCode.ALREADY_LIKED)).doNothing().when(postService).like(any(), any());

        mvc.perform(post("/api/v1/posts/1/likes")
                ).andDo(print())
                //then
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void 좋아요갯수() throws Exception {
        mvc.perform(get("/api/v1/posts/1/likes")
                ).andDo(print())
                //then
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 좋아요갯수요청시_로그인하지않은_경우() throws Exception {
        mvc.perform(get("/api/v1/posts/1/likes")
                ).andDo(print())
                //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 좋아요갯수요청시_게시물이_없는_경우() throws Exception {
        //when
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).likeCount(any());

        mvc.perform(get("/api/v1/posts/1/likes")
                ).andDo(print())
                //then
                .andExpect(status().isNotFound());
    }
}
