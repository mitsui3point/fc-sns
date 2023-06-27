package com.fc.sns.repository;

import com.fc.sns.model.entity.Post;
import com.fc.sns.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByUser(User user, Pageable pageable);
}
