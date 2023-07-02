package com.fc.sns.repository;

import com.fc.sns.model.entity.Comment;
import com.fc.sns.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPost(Pageable pageable, Post post);
}
