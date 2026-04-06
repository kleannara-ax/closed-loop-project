-- =============================================
-- Module-Board DDL 스크립트
-- DB: MariaDB
-- Table Prefix: MOD_BOARD_
-- JPA ddl-auto: none 이므로 수동 실행 필요
-- =============================================

-- 1. 게시글 테이블
CREATE TABLE IF NOT EXISTS MOD_BOARD_POST (
    POST_ID      BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '게시글 ID',
    TITLE        VARCHAR(200)    NOT NULL                 COMMENT '제목',
    CONTENT      TEXT            NULL                     COMMENT '내용',
    WRITER       VARCHAR(100)    NOT NULL                 COMMENT '작성자',
    VIEW_COUNT   INT             NOT NULL DEFAULT 0       COMMENT '조회수',
    IS_DELETED   TINYINT(1)      NOT NULL DEFAULT 0       COMMENT '삭제 여부 (0: 미삭제, 1: 삭제)',
    CREATED_AT   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '등록일시',
    UPDATED_AT   DATETIME        NULL     DEFAULT CURRENT_TIMESTAMP  ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (POST_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='게시글';

-- 인덱스
CREATE INDEX IDX_MOD_BOARD_POST_WRITER     ON MOD_BOARD_POST (WRITER);
CREATE INDEX IDX_MOD_BOARD_POST_CREATED_AT ON MOD_BOARD_POST (CREATED_AT DESC);
CREATE INDEX IDX_MOD_BOARD_POST_DELETED    ON MOD_BOARD_POST (IS_DELETED);


-- 2. 댓글 테이블
CREATE TABLE IF NOT EXISTS MOD_BOARD_COMMENT (
    COMMENT_ID   BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '댓글 ID',
    POST_ID      BIGINT          NOT NULL                 COMMENT '게시글 ID (FK)',
    CONTENT      VARCHAR(1000)   NOT NULL                 COMMENT '댓글 내용',
    WRITER       VARCHAR(100)    NOT NULL                 COMMENT '작성자',
    IS_DELETED   TINYINT(1)      NOT NULL DEFAULT 0       COMMENT '삭제 여부',
    CREATED_AT   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '등록일시',
    PRIMARY KEY (COMMENT_ID),
    CONSTRAINT FK_MOD_BOARD_COMMENT_POST
        FOREIGN KEY (POST_ID) REFERENCES MOD_BOARD_POST (POST_ID)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='게시글 댓글';

-- 인덱스
CREATE INDEX IDX_MOD_BOARD_COMMENT_POST_ID ON MOD_BOARD_COMMENT (POST_ID);
CREATE INDEX IDX_MOD_BOARD_COMMENT_WRITER  ON MOD_BOARD_COMMENT (WRITER);


-- =============================================
-- 샘플 데이터 (테스트용)
-- =============================================
INSERT INTO MOD_BOARD_POST (TITLE, CONTENT, WRITER, VIEW_COUNT, IS_DELETED) VALUES
    ('공지사항: 시스템 점검 안내', '2026년 4월 10일 02:00~06:00 시스템 점검이 진행됩니다.', '관리자', 42, 0),
    ('신규 기능 업데이트 안내', '게시판 모듈이 추가되었습니다. 자유롭게 사용해 주세요.', '관리자', 28, 0),
    ('Spring Boot 3.x 마이그레이션 후기', 'Jakarta EE 전환 시 주의할 점을 공유합니다.', '홍길동', 15, 0);

INSERT INTO MOD_BOARD_COMMENT (POST_ID, CONTENT, WRITER, IS_DELETED) VALUES
    (1, '확인했습니다. 감사합니다.', '김철수', 0),
    (1, '점검 시간이 길군요.', '이영희', 0),
    (3, '좋은 정보 감사합니다!', '박준영', 0);
