package com.company.module.board.service;

import com.company.core.exception.BusinessException;
import com.company.module.board.dto.BoardCommentDto;
import com.company.module.board.dto.BoardPostDto;
import com.company.module.board.entity.BoardComment;
import com.company.module.board.entity.BoardPost;
import com.company.module.board.repository.BoardCommentRepository;
import com.company.module.board.repository.BoardPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시판 서비스
 * - @Transactional은 Service 계층에서만 사용
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BoardPostService {

    private final BoardPostRepository postRepository;
    private final BoardCommentRepository commentRepository;

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // =============================================
    // 게시글 CRUD
    // =============================================

    /**
     * 게시글 목록 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<BoardPostDto.ListResponse> getPostList(Pageable pageable) {
        return postRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable)
                .map(this::toListResponse);
    }

    /**
     * 게시글 검색
     */
    @Transactional(readOnly = true)
    public Page<BoardPostDto.ListResponse> searchPosts(String keyword, Pageable pageable) {
        return postRepository.searchByKeyword(keyword, pageable)
                .map(this::toListResponse);
    }

    /**
     * 게시글 상세 조회 (조회수 증가)
     */
    @Transactional
    public BoardPostDto.Response getPost(Long postId) {
        BoardPost post = findPostOrThrow(postId);
        post.increaseViewCount();
        return toResponse(post);
    }

    /**
     * 게시글 등록
     */
    @Transactional
    public BoardPostDto.Response createPost(BoardPostDto.CreateRequest request) {
        BoardPost post = BoardPost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writer(request.getWriter())
                .build();

        BoardPost saved = postRepository.save(post);
        log.info("[Board] 게시글 등록 완료 - postId={}, title={}", saved.getPostId(), saved.getTitle());

        return toResponse(saved);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public BoardPostDto.Response updatePost(Long postId, BoardPostDto.UpdateRequest request) {
        BoardPost post = findPostOrThrow(postId);
        post.update(request.getTitle(), request.getContent());

        log.info("[Board] 게시글 수정 완료 - postId={}", postId);
        return toResponse(post);
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    @Transactional
    public void deletePost(Long postId) {
        BoardPost post = findPostOrThrow(postId);
        post.softDelete();
        log.info("[Board] 게시글 삭제 완료 - postId={}", postId);
    }

    // =============================================
    // 댓글 CRUD
    // =============================================

    /**
     * 댓글 목록 조회
     */
    @Transactional(readOnly = true)
    public List<BoardCommentDto.Response> getComments(Long postId) {
        findPostOrThrow(postId); // 게시글 존재 확인
        return commentRepository.findByPost_PostIdAndIsDeletedFalseOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::toCommentResponse)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 등록
     */
    @Transactional
    public BoardCommentDto.Response createComment(Long postId, BoardCommentDto.CreateRequest request) {
        BoardPost post = findPostOrThrow(postId);

        BoardComment comment = BoardComment.builder()
                .post(post)
                .content(request.getContent())
                .writer(request.getWriter())
                .build();

        BoardComment saved = commentRepository.save(comment);
        log.info("[Board] 댓글 등록 완료 - postId={}, commentId={}", postId, saved.getCommentId());

        return toCommentResponse(saved);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId) {
        BoardComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다: " + commentId));
        comment.softDelete();
        log.info("[Board] 댓글 삭제 완료 - commentId={}", commentId);
    }

    // =============================================
    // Private Helpers
    // =============================================

    private BoardPost findPostOrThrow(Long postId) {
        BoardPost post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND, "POST_NOT_FOUND", "게시글을 찾을 수 없습니다: " + postId));

        if (post.getIsDeleted()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "POST_DELETED", "삭제된 게시글입니다: " + postId);
        }
        return post;
    }

    private BoardPostDto.Response toResponse(BoardPost post) {
        BoardPostDto.Response dto = new BoardPostDto.Response();
        dto.setPostId(post.getPostId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setWriter(post.getWriter());
        dto.setViewCount(post.getViewCount());
        dto.setCreatedAt(post.getCreatedAt() != null ? post.getCreatedAt().format(DT_FORMAT) : null);
        dto.setUpdatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().format(DT_FORMAT) : null);
        dto.setCommentCount(commentRepository.countByPost_PostIdAndIsDeletedFalse(post.getPostId()));
        return dto;
    }

    private BoardPostDto.ListResponse toListResponse(BoardPost post) {
        BoardPostDto.ListResponse dto = new BoardPostDto.ListResponse();
        dto.setPostId(post.getPostId());
        dto.setTitle(post.getTitle());
        dto.setWriter(post.getWriter());
        dto.setViewCount(post.getViewCount());
        dto.setCreatedAt(post.getCreatedAt() != null ? post.getCreatedAt().format(DT_FORMAT) : null);
        dto.setCommentCount(commentRepository.countByPost_PostIdAndIsDeletedFalse(post.getPostId()));
        return dto;
    }

    private BoardCommentDto.Response toCommentResponse(BoardComment comment) {
        BoardCommentDto.Response dto = new BoardCommentDto.Response();
        dto.setCommentId(comment.getCommentId());
        dto.setPostId(comment.getPost().getPostId());
        dto.setContent(comment.getContent());
        dto.setWriter(comment.getWriter());
        dto.setCreatedAt(comment.getCreatedAt() != null ? comment.getCreatedAt().format(DT_FORMAT) : null);
        return dto;
    }
}
