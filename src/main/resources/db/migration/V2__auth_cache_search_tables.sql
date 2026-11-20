-- Redis·Elasticsearch 제거에 따라 DB로 옮기는 테이블과 조회 성능용 인덱스(docs/migration.md 1절, worklog 5절).
-- 모두 추가형 변경이라 기존 데이터·구버전 앱에 영향이 없다.

-- 리프레시 토큰(Redis → DB). 토큰 원문 대신 SHA-256 해시를 저장한다.
-- rotated_at: 재발급으로 교체된 시각. 교체 후 잠깐(동시 요청) 동안은 액세스 토큰 재발급만 허용한다.
CREATE TABLE refresh_tokens (
    refresh_token_id BIGINT      NOT NULL AUTO_INCREMENT,
    user_id          BIGINT      NOT NULL,
    token_hash       CHAR(64)    NOT NULL,
    expires_at       DATETIME(6) NOT NULL,
    created_at       DATETIME(6) NOT NULL,
    rotated_at       DATETIME(6) NULL,
    PRIMARY KEY (refresh_token_id),
    UNIQUE KEY uk_refresh_tokens_token_hash (token_hash),
    KEY idx_refresh_tokens_user (user_id),
    KEY idx_refresh_tokens_expires (expires_at),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 이메일 인증 코드(Redis 3분 TTL → DB). 백엔드 인스턴스가 여럿이라 메모리에 둘 수 없다.
CREATE TABLE email_verifications (
    email_verification_id BIGINT       NOT NULL AUTO_INCREMENT,
    purpose               VARCHAR(30)  NOT NULL,
    email                 VARCHAR(255) NOT NULL,
    code                  VARCHAR(255) NOT NULL,
    expires_at            DATETIME(6)  NOT NULL,
    created_at            DATETIME(6)  NOT NULL,
    PRIMARY KEY (email_verification_id),
    UNIQUE KEY uk_email_verifications_purpose_email (purpose, email),
    KEY idx_email_verifications_expires (expires_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 인기 검색어(Redis ZSET → DB)
-- search_keywords: 누적 검색 수, 마지막 검색 시각, 5분마다 다시 계산하는 순위 점수
-- search_keyword_hits: 5분 구간별 검색 수(급상승 계산용, 1시간 지나면 지운다)
CREATE TABLE search_keywords (
    keyword          VARCHAR(100) NOT NULL,
    total_count      BIGINT       NOT NULL DEFAULT 0,
    score            DOUBLE       NOT NULL DEFAULT 0,
    last_searched_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (keyword),
    KEY idx_search_keywords_score (score)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE search_keyword_hits (
    keyword     VARCHAR(100) NOT NULL,
    bucket_time DATETIME     NOT NULL,
    hit_count   BIGINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (keyword, bucket_time),
    KEY idx_search_keyword_hits_bucket (bucket_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 스케줄 작업 중복 실행 방지(ShedLock, Redis 리더 선출 대체)
CREATE TABLE shedlock (
    name       VARCHAR(64)  NOT NULL,
    lock_until TIMESTAMP(3) NOT NULL,
    locked_at  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    locked_by  VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 게시물 제목 검색(Elasticsearch → MySQL FULLTEXT, 한글 2글자 단위 ngram)
ALTER TABLE posts ADD FULLTEXT INDEX ft_posts_title (title) WITH PARSER ngram;

-- 목록·상세 조회 인덱스
-- 카테고리별 최신 글(상태 + 카테고리 + 작성일)
CREATE INDEX idx_posts_status_category_created ON posts (status_id, category_id, created_at);
-- 게시물 썸네일 조회(post_id + is_thumbnail)
CREATE INDEX idx_images_post_thumbnail ON images (post_id, is_thumbnail);
-- 조회 기록 중복 확인(post_id + viewer_id)
CREATE INDEX idx_post_views_post_viewer ON post_views (post_id, viewer_id);
-- 소셜 로그인 회원 찾기, 이메일로 계정 연결
CREATE INDEX idx_users_oauth ON users (oauth_type, oauth_id);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_identifier ON users (identifier);

-- 이메일 길이 30 → 255(구글 계정 이메일이 30자를 넘을 수 있다). 늘리기만 하므로 기존 데이터·구버전 앱과 호환된다
ALTER TABLE users MODIFY email VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL;
