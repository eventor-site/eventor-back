-- 2026-11-20 개인 프로젝트 기준 재검토 결과 반영
-- KOPIS: 가입 대상이 공연시설·기획제작사이고 사업자등록증·재직증명서가 필요 → 사용 불가
UPDATE integrations
SET status = '종료', eligibility = '사업자 필요',
    eligibility_note = '회원가입 대상: 공연시설·기획제작사. 사업자등록증(고유번호증)·재직증명서 필요.',
    memo = '2026-11-20 검토 결과 개인 프로젝트는 가입 불가로 보류.', sort_order = 930
WHERE code = 'kopis';

-- 금감원: 개인 즉시 발급이지만 약관상 저장·복제 금지 → 수집기 대상이 아님. 쓰려면 실시간 조회 화면으로만
UPDATE integrations
SET eligibility_note = '개인 신청 즉시 발급, 하루 10,000건, 1인 1키, 사용 URL 등록. 약관 9조: 실시간 연동만 가능하고 정보 복제·저장 불가, 화면에 "금융상품통합비교공시시스템(금융감독원) 금융회사 상품정보" 출처 표기 필수.',
    memo = '수집(DB 저장) 방식과 맞지 않아 보류. 쓴다면 예·적금 금리 실시간 비교 화면(저장 없이 호출)으로만.'
WHERE code = 'finlife';

-- Gemini: 무료 등급에서 되는 것·안 되는 것
UPDATE integrations
SET purpose = '이벤트 분류·태그·기간·혜택 추출, 3줄 요약, 공식 이벤트 페이지 읽기(URL 읽기), 링크 제보 자동 채움, 상시 이벤트 종료 판정.',
    memo = '무료 등급 확인(2026-11-20): JSON 응답 ✅, URL 읽기(url_context) ✅, 구글 검색 연동(google_search) ❌ 할당량 0. 한도는 프로젝트 단위(aistudio.google.com/rate-limit). 공개된 글만 보낸다.'
WHERE code = 'gemini';
