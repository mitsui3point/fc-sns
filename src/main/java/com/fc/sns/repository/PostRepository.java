package com.fc.sns.repository;

import com.fc.sns.model.entity.Post;
import com.fc.sns.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query(value = "select p from Post p")
    Page<Post> findFetchUserAll(Pageable pageable);
}
