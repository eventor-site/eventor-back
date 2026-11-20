-- 개인 프로젝트(사업자등록 없음) 기준으로 연동마다 이용 자격을 적는다(2026-11-20).
ALTER TABLE integrations
    ADD COLUMN eligibility VARCHAR(10) NOT NULL DEFAULT '확인 필요' COMMENT '개인 가능 / 사업자 필요 / 확인 필요' AFTER status,
    ADD COLUMN eligibility_note VARCHAR(300) NULL AFTER eligibility;

UPDATE integrations SET eligibility = '개인 가능', eligibility_note = '공공데이터포털 개인 회원으로 활용신청(이미 사용 중).' WHERE code = 'tour';
UPDATE integrations SET eligibility = '개인 가능', eligibility_note = '기본 범위(openid·email·profile)만 쓰면 구글 앱 검수 없이 운영 가능. 동의 화면에 개인정보처리방침 주소(/terms/1) 필요.' WHERE code = 'google-oauth';
UPDATE integrations SET eligibility = '개인 가능', eligibility_note = '개인 Gmail 계정은 하루 약 500통 한도(SMTP는 더 낮게 막힐 수 있음). 회원이 늘면 전용 메일 발송 서비스 검토.' WHERE code = 'gmail';
UPDATE integrations SET eligibility = '개인 가능', eligibility_note = '사이트 소유 확인만 하면 된다.' WHERE code IN ('google-search-console', 'naver-search-advisor');
UPDATE integrations SET eligibility = '확인 필요', eligibility_note = '개인 프로젝트에서 널리 쓰이지만, 회원가입 때 개인으로 오픈API 신청이 되는지 직접 확인.' WHERE code = 'kopis';
UPDATE integrations SET eligibility = '개인 가능', eligibility_note = '개인 이용자는 하루 호출 한도, 기관은 IP 등록 방식.' WHERE code = 'finlife';
UPDATE integrations SET eligibility = '개인 가능', eligibility_note = '개인 회원 키 발급.' WHERE code = 'kobis';
UPDATE integrations SET eligibility = '개인 가능', eligibility_note = '개인 회원은 3.3% 원천징수 후 정산, 다음 해 5월 종합소득세 신고. 계속·반복 수익이면 사업자등록 대상이 될 수 있어 수익이 생기면 세무 확인.' WHERE code = 'coupang-partners';
UPDATE integrations SET eligibility = '개인 가능', eligibility_note = '개인 구글 계정으로 무료 등급 사용.' WHERE code = 'gemini';
UPDATE integrations SET eligibility = '개인 가능', eligibility_note = NULL WHERE code IN ('vworld', 'kakao-login', 'naver-login');
