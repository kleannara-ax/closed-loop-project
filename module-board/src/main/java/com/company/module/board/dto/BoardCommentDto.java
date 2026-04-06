package com.company.module.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 댓글 요청/응답 DTO
 */
public class BoardCommentDto {

    // === 생성 요청 ===
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateRequest {

        @NotBlank(message = "내용은 필수입니다")
        @Size(max = 1000, message = "댓글은 1000자 이내로 입력하세요")
        private String content;

        @NotBlank(message = "작성자는 필수입니다")
        private String writer;
    }

    // === 응답 ===
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private Long commentId;
        private Long postId;
        private String content;
        private String writer;
        private String createdAt;
    }
}
