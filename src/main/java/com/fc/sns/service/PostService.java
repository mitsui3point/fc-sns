package com.fc.sns.service;

import com.fc.sns.enums.AlarmType;
import com.fc.sns.exception.ErrorCode;
import com.fc.sns.exception.SnsApplicationException;
import com.fc.sns.model.CommentDto;
import com.fc.sns.model.PostDto;
import com.fc.sns.model.entity.*;
import com.fc.sns.model.json.AlarmArgs;
import com.fc.sns.repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final AlarmRepository alarmRepository;

    @Transactional
    public void create(String title, String body, String userName) {
        User user = getUserOrException(userName);

        Post post = Post.builder()
                .title(title)
                .body(body)
                .user(user)
                .build();
        postRepository.save(post);
    }

    @Transactional
    public PostDto modify(Long postId, String title, String body, String userName) {
        User user = getUserOrException(userName);

        Post post = getPostOrException(postId);

        if (!post.isPostWriter(user)) {
            throw new SnsApplicationException(ErrorCode.INVALID_POST_PERMISSION, String.format("%s has no permission with %s", userName, post.getId()));
        }

        post.changeTitle(title);
        post.changeBody(body);
        postRepository.flush();// @Transactional 이 실행되기 이전에 @PreUpdate 를 post 엔티티가 실행하지 않기 때문에, flush 한다.

        return PostDto.fromEntity(post);
    }

    @Transactional
    public void delete(Long postId, String userName) {
        User user = getUserOrException(userName);

        Post post = getPostOrException(postId);

        if (!post.isPostWriter(user)) {
            throw new SnsApplicationException(ErrorCode.INVALID_POST_PERMISSION, String.format("%s has no permission with %s", userName, post.getId()));
        }

        post.deletedAt();
        postRepository.flush();
    }

    public Page<PostDto> list(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(PostDto::fromEntity);
    }

    public Page<PostDto> my(String userName, Pageable pageable) {
        User user = getUserOrException(userName);
        return postRepository.findAllByUser(user, pageable)
                .map(PostDto::fromEntity);
    }

    @Transactional
    public void like(Long postId, String userName) {
        User user = getUserOrException(userName);

        Post post = getPostOrException(postId);

        likeRepository.findByUserAndPost(user, post).ifPresent(o -> {
            throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s is already like post %d", userName, postId));
        });

        likeRepository.save(
                Like.builder()
                        .post(post)
                        .user(user)
                        .build());

        alarmRepository.save(
                Alarm.builder()
                        .type(AlarmType.NEW_LIKE_ON_POST)
                        .user(post.getUser())
                        .args(AlarmArgs.builder()
                                .fromUserId(user.getId())
                                .targetId(post.getUser().getId())
                                .build())
                        .build());
    }

    public int likeCount(Long postId) {
        Post post = getPostOrException(postId);
        return likeRepository.countByPost(post);
    }

    @Transactional
    public void comment(String comment, Long postId, String userName) {

        User user = getUserOrException(userName);

        Post post = getPostOrException(postId);

        commentRepository.save(
                Comment.builder()
                        .comment(comment)
                        .user(user)
                        .post(post)
                        .build());

        alarmRepository.save(
                Alarm.builder()
                        .type(AlarmType.NEW_COMMENT_ON_POST)
                        .user(post.getUser())
                        .args(AlarmArgs.builder()
                                .fromUserId(user.getId())
                                .targetId(post.getUser().getId())
                                .build())
                        .build());
    }

    public Page<CommentDto> getComments(Long postId, Pageable pageable) {
        Post post = getPostOrException(postId);
        return commentRepository.findAllByPost(pageable, post).map(CommentDto::fromEntity);
    }

    private Post getPostOrException(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s is not founded", postId)));
    }

    private User getUserOrException(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not founded", userName)));
    }
}
