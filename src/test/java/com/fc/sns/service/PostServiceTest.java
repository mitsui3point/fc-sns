package com.fc.sns.service;

import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.fixture.PostFixture;
import com.fc.sns.fixture.UserFixture;
import com.fc.sns.model.entity.Post;
import com.fc.sns.model.entity.User;
import com.fc.sns.repository.PostRepostiory;
import com.fc.sns.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepostiory postRepostiory;

    @Mock
    private UserRepository userRepository;

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트작성이_성공한_경우(User user, Post post) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(postRepostiory.save(any())).thenReturn(post);

        //then
        Assertions.assertDoesNotThrow(() -> {
            postService.create(post.getTitle(), post.getBody(), user.getUserName());
        });
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(postRepostiory, times(1)).save(any());

    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트작성시_요청한_유저가_존재하지_않는_경우(User user, Post post) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            postService.create(post.getTitle(), post.getBody(), user.getUserName());
        });
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트수정이_성공한_경우(User user, Post post) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(postRepostiory.findById(post.getId())).thenReturn(Optional.of(post));
        doNothing().when(postRepostiory).flush();

        //then
        Assertions.assertDoesNotThrow(() -> {
            postService.modify(post.getId(), post.getTitle(), post.getBody(), user.getUserName());
        });
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(postRepostiory, times(1)).findById(post.getId());
        verify(postRepostiory, times(1)).flush();
    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트수정시_요청한_유저가_존재하지_않는_경우(User user, Post post) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            postService.modify(post.getId(), post.getTitle(), post.getBody(), user.getUserName());
        });
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트수정시_작성한_글이_존재하지_않는_경우(User user, Post post) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(postRepostiory.findById(post.getId())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            postService.modify(post.getId(), post.getTitle(), post.getBody(), user.getUserName());
        });
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(postRepostiory, times(1)).findById(post.getId());
    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트수정시_권한이_없는_경우(User user, Post post) {
        //given
        User notWriterUser = UserFixture.get("userName1", "password", 2L);

        //when
        when(userRepository.findByUserName(notWriterUser.getUserName())).thenReturn(Optional.of(notWriterUser));
        when(postRepostiory.findById(post.getId())).thenReturn(Optional.of(post));

        //then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            postService.modify(post.getId(), post.getTitle(), post.getBody(), notWriterUser.getUserName());
        });
        Assertions.assertEquals(ErrorCode.INVALID_POST_PERMISSION, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(notWriterUser.getUserName());
        verify(postRepostiory, times(1)).findById(post.getId());
    }

    private static Stream<Arguments> postFixtureSource() {
        User userFixture = UserFixture.get("userName", "password", 1L);
        Post postFixture = PostFixture.get("title", "body", userFixture, 1L);
        return Stream.of(
                Arguments.of(
                        userFixture,
                        postFixture
                )
        );
    }
}
