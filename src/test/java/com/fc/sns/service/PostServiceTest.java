package com.fc.sns.service;

import com.fc.sns.enums.AlarmType;
import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.fixture.*;
import com.fc.sns.model.CommentDto;
import com.fc.sns.model.PostDto;
import com.fc.sns.model.entity.*;
import com.fc.sns.model.json.AlarmArgs;
import com.fc.sns.repository.*;
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

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private AlarmRepository alarmRepository;


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
    void 좋아요를_성공한_경우(User likeUser, Post post, Like like, Alarm alarm) {
        //when
        when(userRepository.findByUserName(likeUser.getUserName())).thenReturn(Optional.of(likeUser));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(likeUser, post)).thenReturn(Optional.empty());
        when(likeRepository.save(any())).thenReturn(like);
        when(alarmRepository.save(any())).thenReturn(alarm);

        //then
        assertDoesNotThrow(() -> {
            postService.like(post.getId(), likeUser.getUserName());
        });
        verify(userRepository, times(1)).findByUserName(likeUser.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
        verify(likeRepository, times(1)).findByUserAndPost(likeUser, post);
        verify(likeRepository, times(1)).save(any());
        verify(alarmRepository, times(1)).save(any());
    }

    @ParameterizedTest
    @MethodSource("likeFixtureSource")
    void 좋아요_요청한_유저가_존재하지_않는_경우(User likeUser, Post post, Like like, Alarm alarm) {
        //when
        when(userRepository.findByUserName(likeUser.getUserName())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.like(post.getId(), likeUser.getUserName());
        });
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(likeUser.getUserName());
    }

    @ParameterizedTest
    @MethodSource("likeFixtureSource")
    void 좋아요_작성한_글이_존재하지_않는_경우(User likeUser, Post post, Like like, Alarm alarm) {
        //when
        when(userRepository.findByUserName(likeUser.getUserName())).thenReturn(Optional.of(likeUser));
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.like(post.getId(), likeUser.getUserName());
        });
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(likeUser.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
    }

    @ParameterizedTest
    @MethodSource("likeFixtureSource")
    void 좋아요_이미_누른_경우(User likeUser, Post post, Like like, Alarm alarm) {
        //when
        when(userRepository.findByUserName(likeUser.getUserName())).thenReturn(Optional.of(likeUser));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(likeUser, post)).thenReturn(Optional.of(like));

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.like(post.getId(), likeUser.getUserName());
        });
        assertEquals(ErrorCode.ALREADY_LIKED, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(likeUser.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
        verify(likeRepository, times(1)).findByUserAndPost(likeUser, post);
    }

    @ParameterizedTest
    @MethodSource("likeFixtureSource")
    void 좋아요갯수_조회_성공한_경우(User likeUser, Post post, Like like, Alarm alarm) {
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
    void 좋아요갯수_조회한_글이_존재하지_않는_경우(User likeUser, Post post, Like like, Alarm alarm) {
        //when
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.likeCount(post.getId());
        });
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        verify(postRepository, times(1)).findById(post.getId());
    }

    @ParameterizedTest
    @MethodSource("commentFixtureSource")
    void 댓글등록을_성공한_경우(User commentUser, Post post, Comment comment, Alarm alarm) {
        //when
        when(userRepository.findByUserName(commentUser.getUserName())).thenReturn(Optional.of(commentUser));
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(commentRepository.save(any())).thenReturn(comment);
        when(alarmRepository.save(any())).thenReturn(alarm);

        //then
        assertDoesNotThrow(() -> {
            postService.comment(comment.getComment(), post.getId(), commentUser.getUserName());
        });
        verify(userRepository, times(1)).findByUserName(commentUser.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
        verify(commentRepository, times(1)).save(any());
        verify(alarmRepository, times(1)).save(any());
    }

    @ParameterizedTest
    @MethodSource("commentFixtureSource")
    void 댓글등록_요청한_유저가_존재하지_않는_경우(User commentUser, Post post, Comment comment, Alarm alarm) {
        //when
        when(userRepository.findByUserName(commentUser.getUserName())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.comment(comment.getComment(), post.getId(), commentUser.getUserName());
        });
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(commentUser.getUserName());
    }

    @ParameterizedTest
    @MethodSource("commentFixtureSource")
    void 댓글등록_작성한_글이_존재하지_않는_경우(User commentUser, Post post, Comment comment, Alarm alarm) {
        //when
        when(userRepository.findByUserName(commentUser.getUserName())).thenReturn(Optional.of(commentUser));
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        //then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> {
            postService.comment(comment.getComment(), post.getId(), commentUser.getUserName());
        });
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(commentUser.getUserName());
        verify(postRepository, times(1)).findById(post.getId());
    }

    @Test
    void 댓글목록조회를_성공한_경우() {
        //given
        Pageable pageable = mock(Pageable.class);
        Page page = mock(Page.class);
        User user = UserFixture.get("userName", "user", 1L);
        Post post = PostFixture.get("title", "body", user, 1L);

        //when
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(commentRepository.findAllByPost(pageable, post)).thenReturn(mock(Page.class));
        //then
        assertDoesNotThrow(() -> {
            Page<CommentDto> result = postService.getComments(post.getId(), pageable);
        });
        verify(postRepository, times(1)).findById(post.getId());
        verify(commentRepository, times(1)).findAllByPost(pageable, post);
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
        User postUserFixture = UserFixture.get("userName", "password", 1L);
        Post postFixture = PostFixture.get("title", "body", postUserFixture, 1L);

        User likeUserFixture = UserFixture.get("likeUserName", "p", 2L);
        Like likeFixture = LikeFixture.get(postFixture, likeUserFixture, 1L);

        Alarm alarmFixture = AlarmFixture.get(1L, postUserFixture, AlarmType.NEW_LIKE_ON_POST,
                AlarmArgs.builder()
                        .fromUserId(postFixture.getUser().getId())
                        .targetId(likeUserFixture.getId())
                        .build()
        );
        return Stream.of(
                Arguments.of(
                        likeUserFixture,
                        postFixture,
                        likeFixture,
                        alarmFixture
                )
        );
    }
    private static Stream<Arguments> commentFixtureSource() {
        User postUserFixture = UserFixture.get("userName", "password", 1L);
        Post postFixture = PostFixture.get("title", "body", postUserFixture, 1L);

        User commentUserFixture = UserFixture.get("commentUserName", "password", 2L);
        Comment commentFixture = CommentFixture.get(commentUserFixture, postFixture, "content",1L);

        Alarm alarmFixture = AlarmFixture.get(1L, postUserFixture, AlarmType.NEW_LIKE_ON_POST,
                AlarmArgs.builder()
                        .fromUserId(postFixture.getUser().getId())
                        .targetId(commentUserFixture.getId())
                        .build()
        );
        return Stream.of(
                Arguments.of(
                        commentUserFixture,
                        postFixture,
                        commentFixture,
                        alarmFixture
                )
        );
    }
}