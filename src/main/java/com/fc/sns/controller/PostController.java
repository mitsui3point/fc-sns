package com.fc.sns.controller;

import com.fc.sns.controller.request.PostCommentRequest;
import com.fc.sns.controller.request.PostCreateRequest;
import com.fc.sns.controller.request.PostModifyRequest;
import com.fc.sns.controller.response.CommentResponse;
import com.fc.sns.controller.response.PostResponse;
import com.fc.sns.controller.response.Response;
import com.fc.sns.model.PostDto;
import com.fc.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest postCreateRequest,
                                 Authentication authentication) {
        postService.create(postCreateRequest.getTitle(), postCreateRequest.getBody(), authentication.getName());
        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Long postId,
                                         @RequestBody PostModifyRequest postModifyRequest,
                                         Authentication authentication) {
        PostDto postDto = postService.modify(postId, postModifyRequest.getTitle(), postModifyRequest.getBody(), authentication.getName());
        return Response.success(PostResponse.fromPostDto(postDto));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Long postId,
                                 Authentication authentication) {
        postService.delete(postId, authentication.getName());
        return Response.success();
    }

    @GetMapping
    public Response<Page<PostResponse>> list(Pageable pageable) {
        Page<PostResponse> postResponses = postService.list(pageable)
                .map(PostResponse::fromPostDto);
        return Response.success(postResponses);
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> my(Pageable pageable,
                                           Authentication authentication) {
        Page<PostResponse> postResponses = postService.my(authentication.getName(), pageable)
                .map(PostResponse::fromPostDto);
        return Response.success(postResponses);
    }

    @PostMapping("/{postId}/likes")
    public Response<Void> likes(@PathVariable Long postId,
                                Authentication authentication) {
        postService.like(postId, authentication.getName());
        return Response.success();
    }

    @GetMapping("/{postId}/likes")
    public Response<Integer> likeCount(@PathVariable Long postId) {
        int count = postService.likeCount(postId);
        return Response.success(count);
    }
    @PostMapping("/{postId}/comments")
    public Response<Void> comment(@RequestBody PostCommentRequest request,
                                  @PathVariable("postId") Long postId,
                                  Authentication authentication) {
        postService.comment(request.getComment(), postId, authentication.getName());
        return Response.success();
    }

    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> comments(Pageable pageable,
                                                    @PathVariable("postId") Long postId) {
        Page<CommentResponse> commentResponses = postService.getComments(postId, pageable)
                .map(CommentResponse::fromCommentDto);
        return Response.success(commentResponses);
    }
}
