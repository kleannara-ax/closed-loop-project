package com.company.module.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 게시글 요청 DTO
 */
public class BoardPostDto {

    // === 생성 요청 ===
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateRequest {

        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 200, message = "제목은 200자 이내로 입력하세요")
        private String title;

        private String content;

        @NotBlank(message = "작성자는 필수입니다")
        @Size(max = 100, message = "작성자는 100자 이내로 입력하세요")
        private String writer;
    }

    // === 수정 요청 ===
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateRequest {

        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 200, message = "제목은 200자 이내로 입력하세요")
        private String title;

        private String content;
    }

    // === 응답 ===
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private Long postId;
        private String title;
        private String content;
        private String writer;
        private Integer viewCount;
        private String createdAt;
        private String updatedAt;
        private long commentCount;
    }

    // === 목록 응답 ===
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ListResponse {
        private Long postId;
        private String title;
        private String writer;
        private Integer viewCount;
        private String createdAt;
        private long commentCount;
    }
}
