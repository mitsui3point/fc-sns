package com.fc.sns.service;

import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.fixture.LikeFixture;
import com.fc.sns.fixture.PostFixture;
import com.fc.sns.fixture.UserFixture;
import com.fc.sns.model.PostDto;
import com.fc.sns.model.entity.Like;
import com.fc.sns.model.entity.Post;
import com.fc.sns.model.entity.User;
import com.fc.sns.repository.LikeRepository;
import com.fc.sns.repository.PostRepository;
import com.fc.sns.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LikeRepository likeRepository;

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트작성이_성공한_경우(User user, Post post) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(postRepository.save(any())).thenReturn(post);

        //then
        assertDoesNotThrow(() -> {
            postService.create(post.getTitle(), post.getBody(), user.getUserName());
        });
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(postRepository, times(1)).save(any());

    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트작성시_요청한_유저가_존재하지_않는_경우(User user, Post post) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.create(post.getTitle(), post.getBody(), user.getUserName());
        });
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트수정이_성공한_경우(User user, Post post) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        doNothing().when(postRepository).flush();

        //then
        assertDoesNotThrow(() -> {
            postService.modify(post.getId(), post.getTitle(), post.getBody(), user.getUserName());
        });
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
        verify(postRepository, times(1)).flush();
    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트수정시_요청한_유저가_존재하지_않는_경우(User user, Post post) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.modify(post.getId(), post.getTitle(), post.getBody(), user.getUserName());
        });
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트수정시_작성한_글이_존재하지_않는_경우(User user, Post post) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.modify(post.getId(), post.getTitle(), post.getBody(), user.getUserName());
        });
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트수정시_권한이_없는_경우(User user, Post post) {
        //given
        User notWriterUser = UserFixture.get("userName1", "password", 2L);

        //when
        when(userRepository.findByUserName(notWriterUser.getUserName())).thenReturn(Optional.of(notWriterUser));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.modify(post.getId(), post.getTitle(), post.getBody(), notWriterUser.getUserName());
        });
        assertEquals(ErrorCode.INVALID_POST_PERMISSION, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(notWriterUser.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트삭제가_성공한_경우(User user, Post post) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        doNothing().when(postRepository).flush();
//        doNothing().when(postRepostiory).delete(post);

        //then
        assertDoesNotThrow(() -> {
            postService.delete(post.getId(), user.getUserName());
        });
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
        verify(postRepository, times(1)).flush();
    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트삭제시_요청한_유저가_존재하지_않는_경우(User user, Post post) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.delete(post.getId(), user.getUserName());
        });
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트삭제시_작성한_글이_존재하지_않는_경우(User user, Post post) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.delete(post.getId(), user.getUserName());
        });
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
    }

    @ParameterizedTest
    @MethodSource("postFixtureSource")
    void 포스트삭제시_권한이_없는_경우(User user, Post post) {
        //given
        User notWriterUser = UserFixture.get("userName1", "password", 2L);

        //when
        when(userRepository.findByUserName(notWriterUser.getUserName())).thenReturn(Optional.of(notWriterUser));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.delete(post.getId(), notWriterUser.getUserName());
        });
        assertEquals(ErrorCode.INVALID_POST_PERMISSION, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(notWriterUser.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
    }

    @Test
    void 피드목록조회를_성공한_경우() {
        //given
        Pageable pageable = mock(Pageable.class);
        Page page = mock(Page.class);

        //when
        when(postRepository.findAll(pageable)).thenReturn(page);

        //then
        assertDoesNotThrow(() -> {
            Page<PostDto> result = postService.list(pageable);
        });
        verify(postRepository, times(1)).findAll(pageable);
    }

    @Test
    void 내_피드목록조회를_성공한_경우() {
        //given
        String userName = "userName";
        Pageable pageable = mock(Pageable.class);
        Page page = mock(Page.class);
        User user = mock(User.class);

        //when
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(user));
        when(postRepository.findAllByUser(user, pageable)).thenReturn(page);

        //then
        assertDoesNotThrow(() -> {
            Page<PostDto> result = postService.my(userName, pageable);
        });
        verify(userRepository, times(1)).findByUserName(userName);
        verify(postRepository, times(1)).findAllByUser(user, pageable);
    }

    @ParameterizedTest
    @MethodSource("likeFixtureSource")
    void 좋아요를_성공한_경우(User user, Post post, Like like) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());
        when(likeRepository.save(any())).thenReturn(like);

        //then
        assertDoesNotThrow(() -> {
            postService.like(post.getId(), user.getUserName());
        });
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
        verify(likeRepository, times(1)).findByUserAndPost(user, post);
        verify(likeRepository, times(1)).save(any());
    }

    @ParameterizedTest
    @MethodSource("likeFixtureSource")
    void 좋아요_요청한_유저가_존재하지_않는_경우(User user, Post post, Like like) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.like(post.getId(), user.getUserName());
        });
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
    }

    @ParameterizedTest
    @MethodSource("likeFixtureSource")
    void 좋아요_작성한_글이_존재하지_않는_경우(User user, Post post, Like like) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.like(post.getId(), user.getUserName());
        });
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
    }

    @ParameterizedTest
    @MethodSource("likeFixtureSource")
    void 좋아요_이미_누른_경우(User user, Post post, Like like) {
        //when
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(like));

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.like(post.getId(), user.getUserName());
        });
        assertEquals(ErrorCode.ALREADY_LIKED, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(user.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
        verify(likeRepository, times(1)).findByUserAndPost(user, post);
    }

    @ParameterizedTest
    @MethodSource("likeFixtureSource")
    void 좋아요갯수_조회_성공한_경우(User user, Post post, Like like) {
        //when
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(likeRepository.countByPost(post)).thenReturn(2);

        //then
        assertDoesNotThrow(() -> {
            int count = postService.likeCount(post.getId());
            assertEquals(count, 2);
        });
        verify(postRepository, times(1)).findById(post.getId());
        verify(likeRepository, times(1)).countByPost(post);
    }

    @ParameterizedTest
    @MethodSource("likeFixtureSource")
    void 좋아요갯수_조회한_글이_존재하지_않는_경우(User user, Post post, Like like) {
        //when
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.likeCount(post.getId());
        });
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        verify(postRepository, times(1)).findById(post.getId());
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
    private static Stream<Arguments> likeFixtureSource() {
        User userFixture = UserFixture.get("userName", "password", 1L);
        Post postFixture = PostFixture.get("title", "body", userFixture, 1L);
        Like likeFixture = LikeFixture.get(postFixture, userFixture, 1L);
        return Stream.of(
                Arguments.of(
                        userFixture,
                        postFixture,
                        likeFixture
                )
        );
    }

}