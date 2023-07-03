package com.fc.sns.repository;

import com.fc.sns.model.entity.Like;
import com.fc.sns.model.entity.Post;
import com.fc.sns.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndPost(User user, Post post);

    //@Query("select count(*) from Like as l where l.post = :post")
    Long countByPost(@Param("post") Post post);

    // 업데이트이긴 하지만 실제 delete.. 일 경우 테이블의 레코드를 모두 가져온 후 삭제대상 레코드만 삭제(비효율적)
    //@Transactional
    @Modifying
    @Query(value = "update Like l set l.deletedAt = NOW() where l.post = :post")
    void deleteAllByPost(Post post);
}
