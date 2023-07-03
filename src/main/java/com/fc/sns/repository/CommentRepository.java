package com.fc.sns.repository;

import com.fc.sns.model.entity.Comment;
import com.fc.sns.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPost(Pageable pageable, Post post);

    // 업데이트이긴 하지만 실제 delete.. 일 경우 테이블의 레코드를 모두 가져온 후 삭제대상 레코드만 삭제(비효율적)
    //@Transactional
    @Modifying
    @Query(value = "update Comment c set c.deletedAt = NOW() where c.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}
