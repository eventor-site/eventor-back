-- Gemini 로 만드는 게시물 분석·인사이트(2026-11-20). 운영 규칙: content-ops-plan 9절, 10절.
-- 키·회원 정보는 저장하지 않는다. 공개 글을 읽어 만든 결과만 둔다.

-- 브랜드 사전(제목의 [브랜드] 와 Gemini 추출 결과를 이름 하나로 모은다)
CREATE TABLE brands
(
    brand_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(40) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_brands_name UNIQUE (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 게시물(이벤트)마다 분석 결과 한 줄. 같은 입력(source_hash)이면 다시 부르지 않는다
CREATE TABLE post_insights
(
    post_id          BIGINT       NOT NULL PRIMARY KEY,
    domain           VARCHAR(20)  NULL COMMENT '분야 8개 중 하나',
    benefit_types    VARCHAR(120) NULL COMMENT '혜택 유형(쉼표 구분)',
    brand_id         BIGINT       NULL,
    confidence       DECIMAL(3, 2) NULL,
    source_hash      CHAR(64)     NULL COMMENT '분류에 쓴 입력(제목·기간·링크)의 해시',
    classified_at    DATETIME(6)  NULL,
    classify_model   VARCHAR(40)  NULL,
    -- 링크를 읽고 만든 상세 분석(진행 중 이벤트만)
    summary          VARCHAR(600) NULL COMMENT '3줄 요약(줄바꿈 구분)',
    target           VARCHAR(200) NULL COMMENT '참여 대상',
    how_to           VARCHAR(300) NULL COMMENT '참여 방법',
    difficulty       VARCHAR(10)  NULL COMMENT '쉬움/보통/어려움',
    value_note       VARCHAR(120) NULL COMMENT '혜택 크기 한 줄',
    live_status      VARCHAR(10)  NULL COMMENT '진행 중/종료/불명(링크 확인 결과)',
    live_reason      VARCHAR(200) NULL,
    deep_status      VARCHAR(10)  NULL COMMENT '대기/완료/실패/제외',
    deep_attempts    INT          NOT NULL DEFAULT 0,
    deep_checked_at  DATETIME(6)  NULL,
    next_deep_at     DATETIME(6)  NULL COMMENT '다음 링크 재확인 시각(상시 이벤트)',
    deep_model       VARCHAR(40)  NULL,
    created_at       DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at       DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_post_insights_post FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE,
    CONSTRAINT fk_post_insights_brand FOREIGN KEY (brand_id) REFERENCES brands (brand_id),
    INDEX idx_post_insights_domain (domain),
    INDEX idx_post_insights_brand (brand_id),
    INDEX idx_post_insights_deep (deep_status, next_deep_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 모델별 하루 사용량(Gemini 하루 한도는 태평양 시간 자정에 초기화되므로 그 날짜 기준)
CREATE TABLE llm_usage
(
    usage_date      DATE        NOT NULL,
    model           VARCHAR(40) NOT NULL,
    requests        INT         NOT NULL DEFAULT 0,
    input_tokens    BIGINT      NOT NULL DEFAULT 0,
    output_tokens   BIGINT      NOT NULL DEFAULT 0,
    tool_tokens     BIGINT      NOT NULL DEFAULT 0,
    errors          INT         NOT NULL DEFAULT 0,
    rate_limited    INT         NOT NULL DEFAULT 0,
    updated_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (usage_date, model)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 주간 혜택 리포트(통계는 DB에서, 해설만 Gemini)
CREATE TABLE insight_reports
(
    report_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    week_start DATE         NOT NULL,
    headline   VARCHAR(120) NOT NULL,
    body       TEXT         NOT NULL COMMENT '해설(문단 줄바꿈)',
    highlights TEXT         NULL COMMENT 'JSON 배열: [{title, reason, postId}]',
    stats_json TEXT         NOT NULL,
    model      VARCHAR(40)  NULL,
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT uk_insight_reports_week UNIQUE (week_start)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
