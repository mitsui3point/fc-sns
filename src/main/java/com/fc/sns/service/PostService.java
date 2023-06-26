package com.fc.sns.service;

import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.model.PostDto;
import com.fc.sns.model.entity.Post;
import com.fc.sns.model.entity.User;
import com.fc.sns.repository.PostRepostiory;
import com.fc.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepostiory postRepostiory;
    private final UserRepository userRepository;

    @Transactional
    public void create(String title, String body, String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not founded", userName)));

        Post post = Post.builder()
                .title(title)
                .body(body)
                .user(user)
                .build();
        postRepostiory.save(post);
    }

    @Transactional
    public PostDto modify(Long postId, String title, String body, String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not founded", userName)));

        Post post = postRepostiory.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s is not founded", title)));

        if (!post.isPostWriter(user)) {
            throw new SnsApplicationException(ErrorCode.INVALID_POST_PERMISSION, String.format("%s has no permission with %s", userName, post.getId()));
        }

        post.changeTitle(title);
        post.changeBody(body);
        postRepostiory.flush();// @Transactional 이 실행되기 이전에 @PreUpdate 를 post 엔티티가 실행하지 않기 때문에, flush 한다.

        return PostDto.fromEntity(post);
    }

}
