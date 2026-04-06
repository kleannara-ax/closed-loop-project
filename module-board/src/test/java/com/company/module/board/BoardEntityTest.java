package com.company.module.board;

import com.company.module.board.entity.BoardPost;
import com.company.module.board.entity.BoardComment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Module-Board 엔티티 단위 테스트
 */
class BoardEntityTest {

    @Test
    @DisplayName("BoardPost 생성 시 기본값 검증")
    void createBoardPost() {
        BoardPost post = BoardPost.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .writer("테스터")
                .build();

        assertThat(post.getTitle()).isEqualTo("테스트 제목");
        assertThat(post.getContent()).isEqualTo("테스트 내용");
        assertThat(post.getWriter()).isEqualTo("테스터");
        assertThat(post.getViewCount()).isEqualTo(0);
        assertThat(post.getIsDeleted()).isFalse();
    }

    @Test
    @DisplayName("BoardPost 수정 동작 검증")
    void updateBoardPost() {
        BoardPost post = BoardPost.builder()
                .title("원래 제목")
                .content("원래 내용")
                .writer("테스터")
                .build();

        post.update("수정된 제목", "수정된 내용");

        assertThat(post.getTitle()).isEqualTo("수정된 제목");
        assertThat(post.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("BoardPost 조회수 증가 검증")
    void increaseViewCount() {
        BoardPost post = BoardPost.builder()
                .title("제목")
                .content("내용")
                .writer("테스터")
                .build();

        post.increaseViewCount();
        post.increaseViewCount();

        assertThat(post.getViewCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("BoardPost Soft Delete 검증")
    void softDeletePost() {
        BoardPost post = BoardPost.builder()
                .title("제목")
                .content("내용")
                .writer("테스터")
                .build();

        assertThat(post.getIsDeleted()).isFalse();

        post.softDelete();

        assertThat(post.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("BoardComment 생성 및 Soft Delete 검증")
    void createAndDeleteComment() {
        BoardPost post = BoardPost.builder()
                .title("제목")
                .content("내용")
                .writer("테스터")
                .build();

        BoardComment comment = BoardComment.builder()
                .post(post)
                .content("댓글 내용")
                .writer("댓글러")
                .build();

        assertThat(comment.getContent()).isEqualTo("댓글 내용");
        assertThat(comment.getWriter()).isEqualTo("댓글러");
        assertThat(comment.getPost()).isEqualTo(post);
        assertThat(comment.getIsDeleted()).isFalse();

        comment.softDelete();

        assertThat(comment.getIsDeleted()).isTrue();
    }
}
