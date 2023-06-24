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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

    @Test
    void 포스트작성이_성공한_경우() {
        //given
        User user = UserFixture.get();
        String title = "title";
        String body = "body";
        String userName = user.getUserName();
        Post post = PostFixture.get();

        //when
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(user));
        when(postRepostiory.save(any())).thenReturn(post);

        //then
        Assertions.assertDoesNotThrow(() -> {
            postService.create(title, body, userName);
        });
        verify(userRepository, times(1)).findByUserName(userName);
        verify(postRepostiory, times(1)).save(any());

    }

    @Test
    void 포스트작성시_요청한_유저가_존재하지_않는_경우() {
        //given
        User user = UserFixture.get();
        String title = "title";
        String body = "body";
        String userName = user.getUserName();
        Post post = PostFixture.get();

        //when
        lenient().when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());
        lenient().when(postRepostiory.save(any())).thenReturn(post);

        //then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> {
            postService.create(title, body, userName);
        });
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
        verify(userRepository, times(1)).findByUserName(userName);
        verify(postRepostiory, times(0)).save(any());
    }
}
