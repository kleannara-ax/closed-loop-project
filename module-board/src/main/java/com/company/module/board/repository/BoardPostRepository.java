package com.company.module.board.repository;

import com.company.module.board.entity.BoardPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    /**
     * 삭제되지 않은 게시글 목록 (페이징)
     */
    Page<BoardPost> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 제목 또는 내용 검색 (삭제 제외)
     */
    @Query("SELECT p FROM BoardPost p " +
           "WHERE p.isDeleted = false " +
           "AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) " +
           "ORDER BY p.createdAt DESC")
    Page<BoardPost> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 작성자별 게시글 조회
     */
    Page<BoardPost> findByWriterAndIsDeletedFalseOrderByCreatedAtDesc(String writer, Pageable pageable);
}
