-- 게시물 본문 HTML 안의 이미지 주소도 상대 주소로 바꾼다(V3 보완).
-- V3는 eventor.duckdns.org·www.eventor.kr 만 바꿨는데, 본문에는 예전 도메인 www.eventor.store 주소가 562건 있었다.
-- 도메인과 관계없이 http(s)://{호스트}/postimage/ → /uploads/postimage/
UPDATE posts
SET content = REGEXP_REPLACE(content, 'https?://[^/"\' ]+/postimage/', '/uploads/postimage/')
WHERE content REGEXP 'https?://[^/"\' ]+/postimage/';

UPDATE images
SET url = REGEXP_REPLACE(url, '^https?://[^/]+/postimage/', '/uploads/postimage/')
WHERE url REGEXP '^https?://[^/]+/postimage/';
