-- 휴면 전환 기준을 90일 → 1년으로 바꾸면서(2026-11-20), 예전 기준(90일)으로 휴면이 된 회원 중
-- 최근 1년 안에 로그인한(로그인 기록이 없으면 1년 안에 가입한) 회원을 다시 활성으로 돌린다.
-- 운영 DB 에는 컷오버 때 적용되므로, 그때까지 구버전이 90일 기준으로 휴면 처리한 회원도 함께 돌아온다.
UPDATE users u
    JOIN statuses cur ON cur.status_id = u.status_id
    JOIN status_types ct ON ct.status_type_id = cur.status_type_id
    JOIN statuses act ON act.name = '활성'
    JOIN status_types at2 ON at2.status_type_id = act.status_type_id AND at2.name = '회원'
SET u.status_id = act.status_id
WHERE ct.name = '회원'
  AND cur.name = '휴면'
  AND COALESCE(u.login_at, u.created_at) > NOW() - INTERVAL 1 YEAR;
