-- 이미지 URL을 도메인 없는 상대 주소로 바꾼다(docs/migration.md 4절).
--   https://eventor.duckdns.org/postimage/20250101/a.webp → /uploads/postimage/20250101/a.webp
--   https://www.eventor.kr/postimage/...                    → /uploads/postimage/...
-- 파일은 저장 루트(./uploads) 아래 postimage/... 에 그대로 있다고 본다(운영은 기존 image 폴더를 uploads로 마운트).
-- 운영 DB에는 컷오버 때 적용된다(구버전 앱은 절대 주소를 쓰므로 그 전에 적용하면 안 된다).

UPDATE images
SET url = CONCAT('/uploads', SUBSTRING(url, LOCATE('/postimage/', url)))
WHERE url LIKE 'http%://%/postimage/%';

-- 게시물 본문 HTML 안의 이미지 주소(현재 1건)
UPDATE posts
SET content = REPLACE(REPLACE(content,
        'https://eventor.duckdns.org/postimage/', '/uploads/postimage/'),
        'https://www.eventor.kr/postimage/', '/uploads/postimage/')
WHERE content LIKE '%/postimage/%';
