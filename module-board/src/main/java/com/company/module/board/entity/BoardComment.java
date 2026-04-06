package com.company.module.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 댓글 엔티티
 * 테이블: MOD_BOARD_COMMENT
 */
@Entity
@Table(name = "MOD_BOARD_COMMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", nullable = false)
    private BoardPost post;

    @Column(name = "CONTENT", nullable = false, length = 1000)
    private String content;

    @Column(name = "WRITER", nullable = false, length = 100)
    private String writer;

    @Column(name = "IS_DELETED", nullable = false)
    private Boolean isDeleted;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public BoardComment(BoardPost post, String content, String writer) {
        this.post = post;
        this.content = content;
        this.writer = writer;
        this.isDeleted = false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}
