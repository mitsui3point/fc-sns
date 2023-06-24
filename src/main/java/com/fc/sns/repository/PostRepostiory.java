package com.fc.sns.repository;

import com.fc.sns.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepostiory extends JpaRepository<Post, Long> {
}
