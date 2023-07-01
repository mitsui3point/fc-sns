package com.fc.sns.repository;

import com.fc.sns.model.entity.Like;
import com.fc.sns.model.entity.Post;
import com.fc.sns.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndPost(User user, Post post);

    @Query("select count(*) from Like as l where l.post = :post")
    Integer countByPost(@Param("post") Post post);
}
