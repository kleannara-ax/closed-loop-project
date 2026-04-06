package com.company.module.board.controller;

import com.company.module.board.dto.BoardCommentDto;
import com.company.module.board.dto.BoardPostDto;
import com.company.module.board.service.BoardPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 게시판 REST API Controller
 * URL Prefix: /board-api
 *
 * - Controller에는 @Transactional 사용 금지
 * - 비즈니스 로직은 Service 계층에 위임
 */
@RestController
@RequestMapping("/board-api")
@RequiredArgsConstructor
public class BoardPostController {

    private final BoardPostService boardPostService;

    // =============================================
    // 게시글 API
    // =============================================

    /**
     * 게시글 목록 조회
     * GET /board-api/posts?page=0&size=10
     */
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getPostList(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword
    ) {
        Page<BoardPostDto.ListResponse> result;
        if (keyword != null && !keyword.isBlank()) {
            result = boardPostService.searchPosts(keyword, pageable);
        } else {
            result = boardPostService.getPostList(pageable);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", result.getContent());
        response.put("page", result.getNumber());
        response.put("size", result.getSize());
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회
     * GET /board-api/posts/{postId}
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<BoardPostDto.Response> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(boardPostService.getPost(postId));
    }

    /**
     * 게시글 등록
     * POST /board-api/posts
     */
    @PostMapping("/posts")
    public ResponseEntity<BoardPostDto.Response> createPost(
            @Valid @RequestBody BoardPostDto.CreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardPostService.createPost(request));
    }

    /**
     * 게시글 수정
     * PUT /board-api/posts/{postId}
     */
    @PutMapping("/posts/{postId}")
    public ResponseEntity<BoardPostDto.Response> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody BoardPostDto.UpdateRequest request
    ) {
        return ResponseEntity.ok(boardPostService.updatePost(postId, request));
    }

    /**
     * 게시글 삭제 (Soft Delete)
     * DELETE /board-api/posts/{postId}
     */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId) {
        boardPostService.deletePost(postId);
        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "게시글이 삭제되었습니다");
        response.put("postId", postId.toString());
        return ResponseEntity.ok(response);
    }

    // =============================================
    // 댓글 API
    // =============================================

    /**
     * 댓글 목록 조회
     * GET /board-api/posts/{postId}/comments
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<BoardCommentDto.Response>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(boardPostService.getComments(postId));
    }

    /**
     * 댓글 등록
     * POST /board-api/posts/{postId}/comments
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<BoardCommentDto.Response> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody BoardCommentDto.CreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardPostService.createComment(postId, request));
    }

    /**
     * 댓글 삭제
     * DELETE /board-api/comments/{commentId}
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId) {
        boardPostService.deleteComment(commentId);
        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "댓글이 삭제되었습니다");
        response.put("commentId", commentId.toString());
        return ResponseEntity.ok(response);
    }
}
