package com.company.module.board.repository;

import com.company.module.board.entity.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    /**
     * 게시글의 댓글 목록 (삭제 제외, 작성순)
     */
    List<BoardComment> findByPost_PostIdAndIsDeletedFalseOrderByCreatedAtAsc(Long postId);

    /**
     * 게시글의 댓글 수
     */
    long countByPost_PostIdAndIsDeletedFalse(Long postId);
}
