-- =============================================
-- Module-Waste DDL: 폐기물 순환 추적 테이블
-- Table Prefix: MOD_WASTE_
-- =============================================

CREATE TABLE IF NOT EXISTS MOD_WASTE_TRACKING (
    TRACKING_ID   BIGINT       NOT NULL AUTO_INCREMENT,
    TRACKING_CODE VARCHAR(30)  NOT NULL,
    WASTE_TYPE    VARCHAR(20)  NOT NULL,
    STATUS        VARCHAR(20)  NOT NULL DEFAULT 'discharge',
    CENTER_NAME   VARCHAR(100) DEFAULT NULL,
    WEIGHT_KG     DECIMAL(10,2) DEFAULT NULL,
    IS_DELETED    TINYINT(1)   NOT NULL DEFAULT 0,
    CREATED_AT    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT    DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    COMPLETED_AT  DATETIME     DEFAULT NULL,
    PRIMARY KEY (TRACKING_ID),
    UNIQUE KEY UK_TRACKING_CODE (TRACKING_CODE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IDX_WASTE_TRK_STATUS ON MOD_WASTE_TRACKING (STATUS);
CREATE INDEX IDX_WASTE_TRK_TYPE ON MOD_WASTE_TRACKING (WASTE_TYPE);
CREATE INDEX IDX_WASTE_TRK_CREATED ON MOD_WASTE_TRACKING (CREATED_AT DESC);

CREATE TABLE IF NOT EXISTS MOD_WASTE_STAGE (
    STAGE_ID         BIGINT       NOT NULL AUTO_INCREMENT,
    TRACKING_ID      BIGINT       NOT NULL,
    STAGE_TYPE       VARCHAR(20)  NOT NULL,
    CENTER_NAME      VARCHAR(100) DEFAULT NULL,
    INPUT_WEIGHT_KG  DECIMAL(10,2) DEFAULT NULL,
    OUTPUT_WEIGHT_KG DECIMAL(10,2) DEFAULT NULL,
    WORKER_NAME      VARCHAR(50)  DEFAULT NULL,
    MEMO             VARCHAR(500) DEFAULT NULL,
    PROCESSED_AT     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (STAGE_ID),
    CONSTRAINT FK_STAGE_TRACKING FOREIGN KEY (TRACKING_ID)
        REFERENCES MOD_WASTE_TRACKING (TRACKING_ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IDX_WASTE_STG_TRACKING ON MOD_WASTE_STAGE (TRACKING_ID);

-- 샘플 데이터
INSERT INTO MOD_WASTE_TRACKING (TRACKING_CODE, WASTE_TYPE, STATUS, CENTER_NAME, WEIGHT_KG, IS_DELETED, COMPLETED_AT) VALUES
('WT-20260401-0001', 'paper',   'completed',   '서울 강남 재활용센터', 1500.00, 0, NOW()),
('WT-20260402-0002', 'vinyl',   'recycling',   '서울 서초 재활용센터', 800.50,  0, NULL),
('WT-20260403-0003', 'general', 'collection',  '경기 수원 재활용센터', 2300.00, 0, NULL),
('WT-20260404-0004', 'metal',   'compression', '인천 중구 재활용센터', 450.75,  0, NULL),
('WT-20260405-0005', 'paper',   'discharge',   '서울 송파 재활용센터', 950.00,  0, NULL);

INSERT INTO MOD_WASTE_STAGE (TRACKING_ID, STAGE_TYPE, CENTER_NAME, INPUT_WEIGHT_KG, OUTPUT_WEIGHT_KG, WORKER_NAME, MEMO) VALUES
(1, 'discharge',   '서울 강남 재활용센터', 1500.00, 1500.00, '김배출', '종이류 배출'),
(1, 'collection',  '서울 강남 재활용센터', 1500.00, 1480.00, '이수거', '수거 완료'),
(1, 'compression', '서울 강남 재활용센터', 1480.00, 1200.00, '박압축', '압축 처리'),
(1, 'recycling',   '서울 강남 재활용센터', 1200.00, 1100.00, '최재활', '재활용 완료'),
(1, 'production',  '서울 강남 재활용센터', 1100.00, 1000.00, '정생산', '재생지 생산'),
(2, 'discharge',   '서울 서초 재활용센터', 800.50,  800.50,  '김배출', '비닐류 배출'),
(2, 'collection',  '서울 서초 재활용센터', 800.50,  790.00,  '이수거', '수거 완료'),
(2, 'compression', '서울 서초 재활용센터', 790.00,  650.00,  '박압축', '압축 처리'),
(2, 'recycling',   '서울 서초 재활용센터', 650.00,  600.00,  '최재활', '재활용 진행 중'),
(3, 'discharge',   '경기 수원 재활용센터', 2300.00, 2300.00, '김배출', '일반 폐기물 배출'),
(3, 'collection',  '경기 수원 재활용센터', 2300.00, 2250.00, '이수거', '수거 완료'),
(4, 'discharge',   '인천 중구 재활용센터', 450.75,  450.75,  '김배출', '금속류 배출'),
(4, 'collection',  '인천 중구 재활용센터', 450.75,  445.00,  '이수거', '수거 완료'),
(4, 'compression', '인천 중구 재활용센터', 445.00,  380.00,  '박압축', '압축 처리'),
(5, 'discharge',   '서울 송파 재활용센터', 950.00,  950.00,  '김배출', '종이류 배출');
