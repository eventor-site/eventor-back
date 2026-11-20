# 작업 기록

사용자 요구사항과 그에 따른 작업을 시간순으로 남긴다. 각 항목은 요구 → 판단 → 결과 순서.
상태: ✅ 완료 · 🔄 진행 중 · ⏳ 대기 · ❓ 사용자 확인 필요

---

## 2026-11-19 대규모 마이그레이션 요청

### 요구사항

| # | 요구 | 상태 | 비고 |
|---|---|---|---|
| R1 | Spring Boot 최신 버전으로 | ✅ | 4.1.2 / Java 25, 로컬 기동·검색 API 확인 |
| R2 | Maven → Gradle | ✅ | Kotlin DSL, pom.xml·.mvn 삭제 |
| R3 | 프론트를 React로 전면 교체, 반응형 | ✅ | 옛 사이트 기능 전체 이전(9·11절) |
| R4 | 프론트 배포를 trendist 방식(정적 빌드 업로드)으로, 프론트 도커 제거 | ✅ | CI 작성, 서버 적용은 deploy.md |
| R5 | 개발 DB: 운영 `eventor`를 `eventor_dev`로 복제해 로컬에서 사용 | ✅ | 처음엔 "운영 DB 직결"이었다가 같은 날 변경 |
| R6 | OAuth는 구글만, 카카오·네이버 제거 | ✅ | 같은 이메일 기존 회원은 구글 로그인 시 자동 연결 |
| R7 | 리프레시 토큰 Redis → DB | ✅ | refresh_tokens(해시 저장, 회전) |
| R8 | 구글 로그인 새 키 적용, 리다이렉트 URL 개선 | ✅❓ | 4절. 구글 콘솔에 새 콜백 등록 필요 |
| R9 | Flyway 도입 | ✅ | V1 기준선·V2 신규 테이블·V3 이미지 상대 경로. eventor_dev 적용 완료, 운영은 컷오버 때 |
| R10 | 이미지 구조 개선, 로컬·운영 같은 구성(프로퍼티로 환경별 경로 지정 제거) | ✅ | 서버 적용은 deploy.md |
| R11 | nginx 이미지 경로·폴더 구조 개선안 | ✅ | deploy/nginx/eventor.conf |
| R12 | 게시물·sitemap 페이지는 SEO 유리한 URL 구조 유지 | ✅ | 12절 |
| R13 | 프론트 디자인 전면 개편(UI/UX) | ✅ | 디자인 토큰·공통 컴포넌트·라이트/다크 |
| R14 | DB 구조 개선 | 🔄 | 1차(인덱스·신규 테이블·email 길이) 완료, 유니크 제약 등은 컷오버 후 |
| R15 | 로컬 포트: 프론트 9080, 백엔드 9081 | ✅ | Vite 9080이 /api·/uploads를 9081로 프록시 |
| R16 | 기술 스택 문서, 작업 기록 문서 | ✅ | docs/README, tech-stack, worklog |
| R17 | 푸터의 개인정보처리방침·약관 등 빠진 영역 복구 | ✅ | 10절 |
| R18 | 추가로 필요한 기능·공통화할 기능 모두 구현·개선 | ✅ | 11~13절 |
| R19 | git 방식은 trendist처럼(main 직접, 커밋 전 확인, `feat: 내용`) | ✅ | 2026-11-20 결정 |
| R20 | 주변 관광지·맛집·숙소 기능 제거(API 만료), 축제는 유지 | ✅ | 15절 |
| R21 | 백엔드 도커 이미지에서 Chrome 제거 | ✅ | 16절 |
| R22 | Node를 호환 범위 안 최신으로 | ✅ | Node 26 LTS, Vite 8·React Router 8·TS 7 (17절) |
| R23 | 휴면 전환 기준 90일 → 1년, 기준에 안 맞는 휴면 회원 활성화 | ✅ | V5 마이그레이션(eventor_dev 16명) |
| R24 | JWT 키 새로 생성, 리프레시 토큰 7일 | ✅ | 운영 ENV_FILE 갱신 필요 |
| R26 | 메인 화면을 트렌디하고 관심 끌 정보로 개편(디자인·기획) | ✅ | 18절 |
| R27 | 실제 운영 시 정보 수급·자동화, 카테고리 적정성 검토·기획 | ✅ | content-ops-plan.md |
| R29 | 관리자에서 연동 API 목록(사이트 링크·아이콘) 관리 | ✅ | 20절 |
| R30 | 개인 프로젝트(사업자 없음) 기준 연동 재검토 | ✅ | content-ops-plan 부록 B |
| R31 | Gemini 키(EVENTOR_API_KEY)를 env에 넣고 불러와 사용 | ✅ | 21절 |
| R32 | 연동 가능한 곳이 적으니 Gemini로 가능한 범위에서 개선·운영안 | ✅ | content-ops-plan 9절, 22절 |
| R33 | Gemini 무료 한도 조사·적용, 기존 데이터 Gemini 마이그레이션, 가능한 작업량 재산정, 자동화, 사용자용 분석 기능 | ✅ | 23절, content-ops-plan 10절 |
| R34 | trendist 와 한도가 겹칠 수 있으니 최대한 효율적으로 | ✅ | 이벤터 몫 40%, 묶음 요청, 해시로 재호출 방지 |
| R35 | 정렬·지역 선택 상자(기본 select) 디자인 개선, 핫딜·커뮤니티·공지 목록 트렌디하게, 이벤트 목록 개선 | ✅ | 24절 |
| R36 | 운영 이미지는 기존 공용 폴더(/home/ubuntu/image) 유지, nginx 설정을 서버 마운트에 맞춤 | ✅ | 25절 |
| R37 | 프론트 CI/CD 를 trendist-front 와 거의 같게 | ✅ | 25절 |
| R28 | 기획서 8절 결정 사항 모두 진행(크롤러 폐기·핫딜 정리·카테고리 개편·제휴·공공 API) | ⏳ | LLM은 Gemini 무료 등급(사용자 제공 키). 키 준비 안내는 content-ops-plan 부록 A |
| R25 | 과거 DB 비밀번호가 든 커밋 삭제 | ✅ | 2026-11-20 force push(17절). 비밀값 자체는 사용자 결정으로 유지, PR #119 참조는 GitHub 지원 요청 필요 |

### 사용자 결정(질문 답변)

- SEO: Vite SPA + 백엔드가 SEO HTML 응답(Next.js 아님)
- Elasticsearch·Redis: 둘 다 제거
- 백엔드 배포: 블루/그린 유지
- 로컬 이미지: 로컬 저장, 파일이 없으면 운영에서 읽기
- git(2026-11-20): trendist와 같은 방식. main에서 바로 작업(브랜치 없음), 작업 후 확인받고 `feat: 내용` 형식으로 커밋. push는 곧 운영 배포(CI)라 따로 요청할 때만

### 1. 분석 결과(기존 코드)

- 백엔드 약 400개 자바 파일, 34개 도메인 패키지. 테스트는 사실상 없음.
- 프론트는 Thymeleaf SSR 서버가 Feign으로 백엔드를 부르고, 쿠키의 JWT를 헤더로 넘기는 구조.
- 발견한 문제
  - 프론트 `FeignClientInterceptor`: 재발급 직후 `refresh-token` 헤더에 액세스 토큰 값을 넣음.
  - 백엔드 CORS `setExposedHeaders` 두 번 호출로 앞 값이 덮임.
  - 구글 로그인 `state`를 만들기만 하고 검증하지 않음(CSRF 취약).
  - `/back/search/sync`, `/back/posts/cleanup`, `/back/images/sync`가 권한 검사 없이 열려 있음.
  - 메인 게시물 목록 검색(`/back/posts/search`)이 Elasticsearch에 의존.
  - 시스템 기본 Maven이 Java 8을 써서 그냥 `mvn`으로는 빌드 실패(JDK 21 지정 시 정상).

### 2. 개발 DB 구성 ✅

- 운영 MySQL 서버(220.71.172.204, eventor.duckdns.org와 같은 서버)에 사용자가 만든 `eventor_dev`에 운영 `eventor`를 통째로 복제.
- `scripts/clone-prod-to-dev.sh`: 운영 덤프(`~/eventor/backups/`에 보관, 운영 백업 겸용) → `eventor_dev` 적재 → 테이블별 행 수 비교. `--reset`이면 테이블을 비우고 다시 복제.
- 결과: 27개 테이블 행 수 모두 일치(게시물 70,128 · 이미지 120,758 · 핫딜 68,732 · 회원 97).
- `application.env`(git 제외)를 정리: 개발 DB URL을 `eventor_dev`로, 새 구글 키, 안 쓰는 Redis·카카오·네이버·Eureka 값 제거. 원본은 `~/eventor/application.env.bak-20261119`.

### 3. 데이터에서 확인한 사실(DB 개선 근거)

- 회원 97명 중 카카오 43 · 네이버 27 · 구글 13 · 아이디/비번 14. → 카카오·네이버 회원 70명은 같은 이메일로 구글 로그인 시 계정 연결이 꼭 필요.
- 회원 상태: 휴면 89 · 활성 3 · 탈퇴 5. → 휴면 해제 흐름이 중요.
- 이미지 URL은 전부 `images.url`에 절대 주소(`https://eventor.duckdns.org/postimage/...` 120,756건, `https://www.eventor.kr/postimage/...` 2건). 본문 HTML에 이미지가 들어간 글은 1건뿐.
- 중복 데이터: 닉네임 `상비이잉` 4건, `[탈퇴]` 5건(탈퇴 시 익명화 값). 즐겨찾기·추천·북마크 중복은 없음.
- 스키마 문제: 아이디·닉네임·(oauth_type, oauth_id)·추천/즐겨찾기 조합에 유니크 제약 없음, 게시물 목록용 복합 인덱스 없음, `users.email` 30자, 예약어 컬럼명 `group`.
- MySQL 8.0.32, 서버 시간대 KST(+09:00).

### 4. 구글 로그인 주소

- 새 콜백(변경): `https://eventor.duckdns.org/api/auth/google/callback`, `http://localhost:9080/api/auth/google/callback`
- 구글 콘솔 "승인된 리디렉션 URI"에 위 두 개(끝 `/` 없이 글자 그대로, 로컬은 반드시 `localhost:9080`로 접속), "승인된 JavaScript 원본"에 `https://eventor.duckdns.org`, `http://localhost:9080` 등록 필요. ❓
- 콜백 주소는 요청의 호스트로 만든다(환경별 설정값 없음). 운영은 nginx의 X-Forwarded-Proto/Host로 https 주소가 된다.

### 5. 백엔드 1단계: 빌드·인프라 의존 제거 ✅

- Gradle(`build.gradle.kts`, wrapper 9.8.2), Boot 4.1.2, Java 25. 실행 jar 이름 `build/libs/eventor-back.jar` 고정.
- Boot 4 변경 대응: Jackson 3(`tools.jackson`), `spring-boot-starter-restclient`(RestClient 자동 설정 분리), QueryDSL 7의 `sum()` → `sumAggregate()`.
- 삭제: Elasticsearch, Redis, dbcp2 데이터소스, Spring Cloud(OpenFeign·Eureka), 카카오·네이버 OAuth, 주석만 남은 파일, 쓰지 않는 Chrome WebDriver 빈(기동 시 Chrome을 띄우던 것).
- 대체
  | 이전 | 이후 |
  |---|---|
  | ES 게시물 검색 | `PostSearchRepository`(QueryDSL) + 제목 FULLTEXT ngram 구문 검색, 2글자 미만은 LIKE. 정렬은 허용 필드만 |
  | Redis 캐시 | Caffeine(`CacheConfig`): 일반 10분, 축제 6시간(관광공사 API 호출 한도 보호) |
  | Redis 리더 선출 | ShedLock. DB를 바꾸는 스케줄만 락, 캐시 비우기는 모든 인스턴스에서 |
  | Redis 인기 검색어 | `search_keywords`·`search_keyword_hits`(점수 식 동일) |
  | Redis 이메일 인증 코드 | `email_verifications`(코드는 SHA-256 해시, 3분) |
  | Redis 로그인 회원 수 집합 | `users.login_at`이 오늘인 회원 수 |
  | Feign(관광공사·VWorld) | RestClient |
- 시간대: DB 데이터가 KST로 저장돼 있음을 확인(포인트 기록 시간 분포가 새벽 3~6시 최저). JVM 기본 시간대를 코드에서 Asia/Seoul로 고정.
- 결과: `eventor_dev`에 V2·V3 적용(20초), `ddl-auto: validate` 통과, 게시물 검색("커피" 1,317건 0.39초)·카테고리·이벤트 상태 필터·인기 검색어 동작 확인.

### 6. 인증 재구성 ✅

- 토큰은 백엔드가 HttpOnly 쿠키로 직접 내려준다(`access_token` 10분, `refresh_token` 7일(처음 14일 → 2026-11-20 7일), SameSite=Lax, Path=/api, 운영 Secure).
  - 이전은 프론트 서버가 쿠키를 관리하고 리프레시 토큰 1일. SPA로 바뀌며 로그인 유지 기간을 늘림(16절에서 7일로 확정).
- 리프레시 토큰: DB에 해시만 저장, 재발급마다 회전, 동시 요청 30초 유예, 유예 지난 재사용은 탈취로 보고 전체 폐기, 행 잠금으로 동시 회전 방지.
- 로그인 흐름: 탈퇴 → 거부 / 휴면 → 해제 티켓(10분) 후 사용자가 확인하면 활성화 / 그 외 → 로그인.
- 구글: `GET /api/auth/google?redirect=…` → 구글 → `/api/auth/google/callback`(state 쿠키 비교) → 결과별 프론트 화면(`/auth/dormant`, `/auth/google/signup`, `/auth/login?error=…`).
  - 콜백 주소는 요청 호스트로 생성(로컬 `http://localhost:9080/...`, 운영 `https://eventor.duckdns.org/...` 확인).
  - 신규 구글 회원은 가입 대기 티켓 → 닉네임 입력 → 가입.
- 보안 수정
  - `/me/recover`(아이디만으로 휴면 해제), `/me/recover/oauth`, `/oauth2/info`(회원 권한 정보 노출) 삭제.
  - 공개 회원가입 API에서 oauthId·oauthType 무시(남의 구글 계정 ID를 미리 등록하는 것 방지).
  - 관리 API(`/api/images/sync`, `/api/posts/cleanup`)에 관리자 권한 추가, ES 동기화 API 삭제.
  - 500 응답에 스택 트레이스를 담던 것 제거(로그에만).
  - 로그인 후 돌아갈 주소는 같은 사이트 경로만(오픈 리다이렉트 방지).
- API 경로 `/back/**` → `/api/**`.

### 7. 이미지 저장 구조 ✅(백엔드)

- `FileStorage`: 저장 루트 `./uploads`(로컬·운영 같은 값), URL `/uploads/…`, 루트 밖 경로 거부, 업로드 폴더 이름 영문·숫자만.
- `UploadsController`: 로컬에서 `/uploads/**` 서빙, 파일이 없으면 운영(`https://eventor.duckdns.org/postimage/…`)으로 302. 운영은 nginx가 직접 서빙.
- V3 마이그레이션: `images.url` 120,758건을 `/uploads/postimage/…`로, 본문 내 이미지 주소 1건 변환.
- sitemap 파일 생성(이미지 폴더에 sitemap.xml을 덧붙이던 방식) 삭제 → SEO 단계에서 DB 기반 동적 생성으로 대체 예정.

### 8. 빌드·배포 파일 🔄

- Dockerfile: `eclipse-temurin:25-jre`, TZ=Asia/Seoul, `/app/uploads` 볼륨, Chrome 유지(크롤러 재가동 대비).
- CI: Gradle 빌드(테스트 포함) → 블루/그린 이미지. 실행 중인 색은 `/api/actuator/version`, 없으면 구버전 `/back/actuator/version`으로 확인.
- 단위 테스트 5개: 리다이렉트 주소 검사, 업로드 경로 조작 차단, 검색 구간 계산.

### 9. React 프론트 1차 🔄

- 사용자 확인: "9080 띄워진 게 없다" → 백엔드만 끝난 상태였음. 프론트 1차를 만들어 9080에 띄움.
- 구성: Vite 5 + React 19 + TypeScript, react-router-dom 6(로컬 Node 18 호환), TanStack Query 5, DOMPurify. 옛 Thymeleaf 프론트(src/, pom.xml, 도커 파일)는 삭제(git 이력에 남음).
- 화면: 메인(히어로 검색·인기 검색어·카테고리 칩·이벤트 가로 레일 5종·핫딜·새 글), 카테고리 목록(이벤트는 카드 그리드 + 진행 상태 탭·정렬, 그 외는 리스트), 검색, 게시물 상세(기간/쇼핑몰·가격 카드, 바로가기, 추천·비추천·찜·공유, 댓글·답글·수정·삭제), 로그인(구글 + 아이디), 휴면 해제, 구글 신규 가입(닉네임), 404.
- 디자인: 로고 오렌지(#ff6a00) 주색, Pretendard, 라이트/다크 자동, 모바일 하단 탭·전체 메뉴 드로어, 데스크톱 이벤트 메가 메뉴, 스켈레톤 로딩.
- 주소는 옛 사이트와 같게 유지(`/posts?categoryName=…`, `/posts/{id}`) — 검색엔진에 색인된 주소가 그대로 열린다.
- 본문 HTML은 DOMPurify로 정화(스크립트 제거, 영상 iframe은 유튜브·비메오·네이버TV만).
- 백엔드 보완
  - `GET /api/categories/tree`(메뉴용 카테고리 트리) 추가.
  - V4 마이그레이션: 본문 HTML 속 옛 도메인(www.eventor.store) 이미지 주소 562건 → `/uploads/postimage/…`.
  - 권한 누락 수정: 이미지 업로드·삭제가 비로그인·남의 글에도 가능했음 → 글쓴이·관리자만. 추천·비추천·찜·북마크·신고·내 정보 수정·비밀번호 변경에 로그인 필요. `/me/loginAt`(아무나 로그인 시각 변경) 삭제.
  - 찜 API 경로 정리: `/api/posts/{id}/favorites`, `/api/favorites/{id}`.
  - 파일 없는 업로드 요청 등 잘못된 요청은 500이 아니라 400.
- 확인: 데스크톱 1400px·모바일 390px 화면 캡처(가로 넘침 없음, 콘솔 오류 없음), 9080 프록시로 API·이미지(운영 폴백)·구글 콜백 주소(`http://localhost:9080/...`) 확인.
- 남은 화면: 글쓰기·수정(에디터·이미지 업로드), 마이페이지(내 글·댓글·찜·북마크·정보 수정·탈퇴), 아이디 회원가입·비밀번호 찾기, 관리자 화면, 약관.

### 10. 푸터·약관 복구 ✅

- 사용자 지적: "개인정보처리방침 같은 매우 중요한 영역이 footer에 있어야 하는 것 아니냐" → 1차 프론트에서 푸터 링크를 빼 버린 실수.
- 옛 푸터 그대로 복구: 광고 문의 이메일(eventor2025@naver.com), 블로그·인스타그램·유튜브·플레이스토어 링크, 개인정보처리방침(`/terms/1`), 서비스 이용약관(`/terms/2`), 저작권 표시. 주소도 옛 사이트와 같게.
- 약관 원문은 `eventor-front/src/content/*.html`로 옮기고, 옛 HTML과 글자가 똑같은지 자동 비교로 확인. 데스크톱은 목차가 따라다닌다.
- 가입·로그인 화면에 약관 동의 문구와 링크 추가(아이디 가입은 필수 체크).
- ❓ 개인정보처리방침 본문은 카카오·네이버 로그인 종료, 구글 로그인 정보 수집 등 바뀐 내용이 반영되지 않았다. 법적 문서라 임의로 고치지 않았으니 개정 여부를 정해 주세요.

### 11. 빠진 기능 이전·추가 ✅

| 기능 | 화면 | 비고 |
|---|---|---|
| 글쓰기·수정 | `/write`, `/write/{id}` | Toast UI 에디터(옛 사이트와 같아 기존 글 HTML 호환), 붙여넣기 이미지 업로드, 임시 저장·이어 쓰기, 이벤트(기간·종료 유형·링크·대표 이미지)·핫딜(쇼핑몰·상품·가격·링크) 입력 |
| 마이페이지 | `/me/{posts,comments,favorites,views,bookmarks,settings}` | 내 글·댓글·찜·최근 본 글·즐겨찾는 카테고리, 정보 수정, 비밀번호 변경, 탈퇴 |
| 아이디 가입 | `/auth/signup` | 아이디·닉네임 중복 확인, 이메일 인증번호 |
| 계정 찾기 | `/auth/find` | 아이디를 메일로 받기, 인증번호로 비밀번호 재설정 |
| 신고 | 상세 화면 | 게시물·댓글 신고(사유 선택) |
| 카테고리 즐겨찾기 | 목록 제목 옆 ☆ | 메인에 바로가기 칩 |
| 축제 | `/tours/festival2`, `/tours/{id}` | 전국 축제(상태·지역 필터), 상세(지도 링크). 메인에 "지금 열리는 축제". (주변 관광지 `/tours`는 15절에서 제거) |
| 관리자 | `/admin/{section}` | 대시보드(일별 통계), 신고 처리, 게시물(고정·이벤트 종료·삭제), 회원(상태·등급·역할), 이용 정지, 인기 검색어, 카테고리·등급·포인트·금지 닉네임·신고 사유·역할·상태 |
| 방문자 집계 | 앱 시작 시 | 브라우저마다 하루 한 번(옛 사이트와 같은 기준) |

공통화
- 관리자 화면 대부분을 설정만으로 만드는 `ResourceManager`(목록·추가·수정·삭제·페이지) — 화면 15개를 파일 하나로.
- 공통 `Modal`, `ReportDialog`, `ErrorBoundary`(오류 시 흰 화면 대신 안내), `PagedList`, API 클라이언트(`request`/`api`).
- 백엔드 `HtmlSanitizer`(저장 시 본문 정화), `StrictQuery`(외부 API 쿼리 인코딩).

### 12. 검색엔진(SEO) ✅

- 백엔드 `SeoController`: `/posts/{id}`, `/posts?categoryName=`, `/sitemap.xml`, `/sitemaps/pages.xml`, `/sitemaps/posts-{n}.xml`.
- 프론트 빌드의 index.html에 제목·설명·canonical·OG·트위터 카드·JSON-LD(Article)·본문을 넣어 응답 → React가 이어받음. 없는·삭제된 글은 404.
- 사이트맵은 DB에서 바로 생성(4만 개씩 나눔, 현재 게시물 2파일). 옛 방식(이미지 폴더의 sitemap.xml에 글 쓸 때마다 덧붙이기) 삭제.
- index.html 위치: `./web/index.html`(운영은 releases 폴더 마운트), 로컬은 Vite에서 받아 옴. Vite도 같은 주소를 백엔드로 넘겨 운영과 같게 동작.

### 13. 보안·버그 점검 결과 ✅

| 문제 | 조치 |
|---|---|
| 역할·등급·상태·상태 유형·신고 사유 생성·수정·삭제, 금지 닉네임 추가·삭제, 카테고리 생성 API에 권한 검사 없음 | 관리자 전용. **모든 쓰기 API에 권한이 있는지 검사하는 테스트**(`ApiAuthorizationRuleTest`) 추가 |
| 통계 조회 API 공개 | 관리자 전용 |
| 회원이 이벤트·공지 카테고리에 글을 쓸 수 있음(프론트에서만 막음) | 백엔드에서 회원은 자유·핫딜만 |
| 본문 HTML 그대로 저장(스크립트 삽입 가능) | 저장 시 jsoup 정화 + 화면 DOMPurify 정화(이중) |
| 아이디만 알면 남의 비밀번호를 바꿔 메일로 보냄 | 계정 이메일 인증번호 확인 후 사용자가 새 비밀번호 지정, 다른 기기 로그인 해제 |
| 회원가입 이메일 인증을 서버가 확인하지 않음 | 인증 완료 기록(30분)이 있어야 가입, 아이디·닉네임 형식·금지어 서버 확인 |
| 인증번호를 예측 가능한 `Random`으로 생성 | `SecureRandom` |
| 아이디 찾기가 존재 여부만 알려 줌(계정 열거) | 그 이메일로 아이디 목록을 메일 발송, 화면 문구는 항상 같게 |
| 관광공사 API 인증키의 '+'가 인코딩되지 않아 축제·관광지 조회가 빈 결과(Feign→RestClient 전환 회귀) | 쿼리 엄격 인코딩, 축제 68건 조회 확인 |
| VWorld 지오코더 인증키 만료(`EXPIRE_KEY`) | 기능 자체를 제거(15절) |
| 기간 없이 부른 관리자 통계 API 500 | 기본 기간(이번 달) |
| 경로 조작 폴더 이름 업로드가 500 | 400 |

- 끝까지 확인: 회원 토큰으로 자유 게시판 임시 글 → 이미지 업로드(./uploads 저장) → 등록(스크립트 제거 확인) → 삭제, 이벤트 카테고리 쓰기 403, 남의 글 이미지 업로드 403.
- 테스트 12개 통과, 프론트 타입 검사·production 빌드 통과(첫 화면 JS 119KB gzip, 에디터·관리자는 필요할 때만 받음).

### 14. 배포 준비 ✅(서버 적용 ❓)

- 프론트 CI: Vite 빌드 → scp → `releases/`에 rsync(폴더를 바꾸지 않아 컨테이너 마운트가 유지됨) → nginx reload. 옛 해시 파일은 7일 보관.
- `deploy/nginx/eventor.conf`: 정적 파일, `/api`, SEO 경로, `/uploads/` 직접 서빙, `/postimage/` → `/uploads/postimage/` 301, `/back/` 410, 해시 파일 1년 캐시.
- `deploy/compose/eventor-back.example.yml`: 블루/그린 컨테이너 마운트.
- `docs/deploy.md`: 준비 → 백업 → 이미지 이동 → 프론트 → 백엔드 → 컷오버 → 되돌리기, 운영 .env 항목.

---

## 2026-11-20

### 15. 주변 관광지·맛집·숙소 제거 ✅

- 요구: 여행 기능은 쓰던 API(VWorld 지오코더)가 더 이상 유효하지 않으니, 축제는 살리고 주변 관광지·맛집·숙소만 제거.
- 판단: 주소 → 좌표 변환(VWorld)이 막히면 위치 기반 목록도 쓸 수 없다. 축제 목록·상세는 관광공사 API(`searchFestival2`, `detail*2`)만 써서 그대로 동작하므로 유지. 축제 상세가 `/tours/{id}` 주소를 쓰므로 주소 체계는 그대로 둔다.
- 백엔드: `GET /api/tours/search`, `GeocoderApiClient`, `TourApiClient.getLocationBasedList2`, `GeoPointResponse`·`SearchTourResponse`·`TourApiResponse`, 주소·지오코더 예외, 설정 `openApi.geocoder`(`GEOCODER_KEY`) 삭제.
- 프론트: `TourSearchPage`, 축제/주변 관광지 탭 삭제. 옛 주소 `/tours`(검색엔진·즐겨찾기)는 `/tours/festival2`로 넘김. 메뉴 이름 "축제·여행" → "축제", 전체 메뉴의 "주변 관광지" 칩 삭제, 상세 화면 경로 표시 "전국 축제".
- 확인: 백엔드 빌드·테스트 통과, 프론트 타입 검사·빌드 통과. 9080에서 `/tours?address=…` → 축제 목록(70건), 카드 클릭 → 상세 정상, 콘솔 오류 없음. `/api/tours/festival2`·`/festivals/ongoing`·`/{id}` 200.

### 16. 보안·운영 정리(Chrome, Node, 휴면, JWT) 🔄

- 배경: 두 저장소가 GitHub 공개 저장소이고, 지금 쓰는 DB 비밀번호·JWT 키가 과거 커밋에 남아 있음을 확인(back: 2025-05 `application-*.yml`·`application.env`, front: 2025-05 yml).
- Chrome: Dockerfile에서 Chrome 설치 제거(크롤러 스케줄 꺼짐). 서버 헬스 체크용 curl만 남김. 크롤러를 다시 켜려면 Chrome 설치를 되살려야 한다.
- Node: 2026-11-20 기준 Active LTS는 26(v26.13.1). 프론트 CI `node-version` 20 → 26, `.nvmrc` 26 추가. 로컬 Node는 사용자가 nvm으로 26.13.1 설치(시스템 /usr/bin/node 18은 그대로).
- 휴면: 마지막 로그인(없으면 가입일)이 1년 지난 활성 회원만 휴면으로 전환(이전 90일). eventor_dev 기준 휴면 89명 중 16명은 최근 1년 안에 로그인한 적이 있어 새 기준이면 휴면이 아니다 → V5로 활성화(17절). 탈퇴 회원 파기(탈퇴 후 90일)는 그대로.
- JWT: 새 키(128자 hex) 생성, 로컬 `application.env`에 적용(이전 파일 `~/eventor/application.env.bak-20261120`). 운영 `ENV_FILE`의 `JWT_SECRET`도 같은 값 또는 새 값으로 바꿔야 함. 바꾸면 기존 로그인은 모두 풀린다.
- 리프레시 토큰: 14일 → 7일(`eventor.jwt.refresh-ttl: 7d`). 운영 구버전(1일) 대비로는 늘어난 것.
- 확인: 백엔드 빌드·테스트 통과, 새 키로 9081 재기동 정상.

### 17. 휴면 복구, 프론트 패키지 최신화, git 이력 정리 🔄

- 휴면: `V5__reactivate_dormant_within_1y.sql` — 휴면 회원 중 최근 1년 안에 로그인한(기록 없으면 1년 안에 가입한) 회원을 활성으로. eventor_dev 적용 결과 활성 3 → 19, 휴면 89 → 73. 운영 DB는 컷오버 때 적용되므로 그때까지 구버전이 90일 기준으로 휴면 처리한 회원도 함께 돌아온다.
- 프론트 패키지(Node 26 기준 최신)

  | 패키지 | 이전 | 이후 | 바꾼 것 |
  |---|---|---|---|
  | vite / @vitejs/plugin-react | 5.4 / 4.7 | 8.3.8 / 6.3.0 | 없음 |
  | react-router-dom | 6.30 | `react-router` 8.5.2 | v8에서 react-router-dom 패키지 폐지 → import를 `react-router`로 |
  | typescript | 5.9 | 7.0.2 | tsconfig `baseUrl` 제거, `paths`를 `./` 상대 경로로 |
  | react / react-dom | 19.1 | 19.3.1 | 없음 |
  | 그 외 | | react-query 5.108, dompurify 3.4.18, @toast-ui/editor 3.2.3, @types/* | |

  - `package.json` `engines.node >=22.22.0`(React Router 8 최소), `.nvmrc` 26, CI Node 26.
  - 확인: 타입 검사·빌드 통과(첫 화면 JS 134KB gzip, 이전 118KB), 9080에서 메인·카테고리 이동·상세·검색→상세→뒤로·축제(/tours 넘김)·로그인·글쓰기 로그인 유도·약관·404, 모바일 검색, 에디터 로드 확인. 콘솔 오류 없음.
  - `npm audit` moderate 2건: @toast-ui/editor 밑의 dompurify 2.x. 에디터는 번들 안에 DOMPurify 2.3.3을 내장해 쓰므로 이 패키지는 실제로 쓰이지 않는다(override 해도 효과가 없어 두지 않음). 에디터는 관리가 멈춘 상태라, 저장 시 서버 jsoup 정화·화면 DOMPurify 3 정화로 막는다. 에디터 교체는 별도 과제.
- git 이력 정리(R25)
  - 두 저장소 전체 이력에서 비밀값 12종을 `***REMOVED***`로 바꿈: DB 비밀번호(현재·예전), 옛 JWT 키, 구글 OAuth 시크릿 2, Gmail 앱 비밀번호 3, 네이버 시크릿 3, 카카오 어드민 키. 커밋 메시지에는 없었음.
  - 방법: GitHub에서 새로 받은 복제본에 `git filter-branch --tree-filter`(git-filter-repo 설치 불가). 원본은 `~/eventor/backups/{eventor-back,eventor-front}-before-rewrite-20261120.bundle`(비밀값 포함, 외부 공유 금지).
  - 검증: 재작성 이력에서 비밀값 0건, 커밋 수 동일(back 469, front 577), main 최신 코드 내용은 원본과 완전히 같음.
  - 남는 것: eventor-front PR #119(`refs/pull/119/head`)는 GitHub 관리 읽기 전용 참조라 지울 수 없고 옛 커밋(비밀값 8곳)을 계속 가리킨다. 완전 삭제는 GitHub 지원팀 요청 필요.

### 18. 메인 화면 개편 ✅

- 요구: 카테고리별 목록만 늘어놓은 메인이 평범하고 구시대적 → 요즘 트렌디하고 관심 끌 정보, 멋진 디자인으로.
- 먼저 확인한 사실: 메인 목록 상당수가 **이미 끝난 이벤트**(마지막 이벤트 등록 2025-06, 핫딜은 2026-08 크롤링 중단 이후 없음, 마감 임박 API는 0건). 디자인만 바꾸면 "예쁜 만료 목록"이 된다.
- 판단: 원칙을 "지금 유효한 정보만"으로. 모든 섹션을 진행 중 이벤트(330건)·실시간 축제 API 기준으로 다시 짰다. 기존 백엔드 API(`/api/posts/search` 진행 중 필터·정렬, 축제, 인기 검색어, 핫딜)만 써서 백엔드 변경 없음.
- 구성(위에서 아래)

  | 섹션 | 내용 |
  |---|---|
  | 인트로 | 오늘 날짜 + 큰 제목(그라데이션 강조) |
  | 스포트라이트 | 진행 중 인기 이벤트 6개(브랜드 중복 없이) 캐러셀. 이미지 색을 흐려 깐 배경, 5초 자동 넘김·진행 막대, 스와이프, 마우스·포커스 시 멈춤 |
  | 라이브 패널 | 진행 중 이벤트 수·석 달 축제 수·이번 주말 축제 수, 실시간 인기 검색어(넓은 화면은 펼침, 모바일은 한 줄씩 넘김) |
  | 카테고리 타일 | 이모지 아이콘 타일(모바일 2줄 가로 스크롤) |
  | 브랜드 스토리 | 진행 중 이벤트가 많은 브랜드를 인스타 스토리처럼(개수 배지), 누르면 브랜드 검색 |
  | 이번 주말 축제 | 짙은 배경 쇼케이스, 이번 주말/진행 중/곧 시작 탭, 포스터형 카드 |
  | 마감 다가오는 이벤트 | 90일 안 마감, D-day + 기간 진행률 막대(7일 이하는 빨강) |
  | 핫딜 TOP 10 | 넷플릭스식 큰 순위 숫자, 제목에서 가격·무료배송 뽑아 표시 |
  | 상시 혜택 | 벤토 그리드(큰 타일 1 + 작은 타일 4) |
  | 최근 등록 | 이미지 있는 진행 중 이벤트, "장기 진행"(1년 넘게 남음) 표시 |
  | 커뮤니티 | 2열 목록 |

- 마감된 이벤트가 나오던 기존 "지금 뜨는/추천/급상승/새로 올라온" 레일은 메인에서 뺐다(백엔드 API는 그대로).
- 접근성: 캐러셀·탭 ARIA, 움직임 줄이기 설정 시 자동 넘김·애니메이션 끔. 라이트/다크 모두 확인.
- 파일: `src/pages/HomePage.tsx`(전면 교체), `src/styles/home.css`(신규, 접두사 `h-`).
- 확인: 타입 검사·빌드 통과, 1400px·390px·다크 캡처에서 가로 넘침 없음·콘솔 오류 없음. 주말 날짜를 UTC로 계산해 하루 밀리던 것, 순위 숫자와 상품명이 겹치던 것을 캡처 보고 고침.
- 한계: 데이터 자체가 오래돼 "최근 등록"이 2025년 글이다 → 콘텐츠 수급은 `content-ops-plan.md`.

### 19. 콘텐츠 수급·카테고리 운영 기획 ✅❓

- 요구: 실제 운영한다면 이벤트·할인 정보를 어떻게 가져오고 자동화할지, 카테고리가 너무 많지 않은지 검토·기획.
- 결과: `docs/content-ops-plan.md`. 핵심
  - 핫딜 6.8만 건은 FM코리아 핫딜 게시판 복사(크롤러 코드 확인), 평균 조회 0 → 법적·SEO 위험, 크롤러 폐기와 기존 글 아카이브 처리 제안.
  - 수급: 공공 API(KOPIS·KOBIS·금감원 예적금·관광공사) + 회원 제보(포인트 보상) → 제휴 피드 → 브랜드 공식 페이지 수집 → 브랜드 직접 등록 순.
  - 자동화: 출처별 수집기 → 원본 보관 → 정규화 → 중복 제거 → 분류(규칙 + LLM) → 검수 대기열 → 게시 → 만료·링크 점검.
  - 카테고리 34개(세부 11개가 30건 미만) → 게시판 4 + 분야 8, 세부는 태그·브랜드·상태 필터로.
- ❓ 결정 대기: 기획서 8절 5가지.
- 2026-11-20 force push 완료: eventor-back main `5ca6151 → e06d510`, eventor-front main `ef193dc → 93d89f5`, eventor-front `recommend-vertical-slider` 브랜치 삭제(사용자 요청). `--force-with-lease`로 확인한 커밋만 덮어씀. 로컬은 `reset --soft origin/main`으로 맞춤(커밋 안 한 작업 상태 그대로), 옛 객체 정리(`reflog expire`·`gc --prune=now`) 후 비밀값 0건 재확인.

### 20. 관리자 > 외부 연동 ✅

- 요구: 관리자 페이지에서 연동된 API를 사이트 링크·아이콘과 함께 보고, 사이트에서 관리할 수 있게.
- DB: `V6__integrations.sql`(목록 13건: 사용 중 5·준비 중 5·종료 3), `V7__integrations_eligibility.sql`(이용 자격·메모). 키 값은 DB에 두지 않고 환경 변수 이름만.
- 백엔드 `integration` 패키지: `GET/POST/PUT/DELETE /api/admin/integrations`, `POST /{id}/check`(모두 관리자 전용, 읽기 포함). 응답에는 환경 변수 이름과 설정 여부만(키 값이 응답에 없는지 확인). 자동 점검: TourAPI(resultCode), Gmail(SMTP 로그인), 구글 로그인(인증 서버 + 키 설정), Gemini(실제 호출). 버튼을 누를 때만 호출.
- 프론트 `admin/IntegrationsAdmin.tsx`·`styles/integrations.css`: 요약(사용 중·준비 중·키 미설정·점검 실패·사업자 필요), 상태 필터, 카드(아이콘·제공처·용도·이용 자격·환경 변수 ✓/✕·메모·점검 결과·사이트/키 콘솔/문서 링크·수정·삭제), 종료된 연동은 접어 둠, 추가·수정 창.
- 아이콘: 각 공식 사이트 파비콘을 받아 64px PNG로 `public/integrations/`에 저장(화면에서 외부 사이트를 부르지 않음).
- 링크는 모두 실제로 열어 확인, 금감원 키 신청 주소는 오류 페이지라 API 안내 주소로 바꿈.
- 확인: 백엔드 빌드·테스트(권한 규칙 포함) 통과, 비로그인 401, 로컬 관리자 토큰으로 목록·점검 4종 정상, 1400px·390px(다크) 캡처.

### 21. Gemini API 연결 ✅

- 요구: AI Studio에서 이벤터용으로 발급한 키(EVENTOR_API_KEY)를 env에 넣고 불러와 쓰기.
- `application.env`(git 제외)에 `GEMINI_API_KEY`로 저장. 설정 `eventor.gemini.api-key/model/timeout`, 모델 기본 `gemini-flash-latest`(현재 gemini-3.8-flash로 연결, `GEMINI_MODEL`로 바꿀 수 있음).
- `llm` 패키지: `LlmClient`(교체 가능한 창구) + `GeminiClient`(REST generateContent, 키는 헤더로, 응답 JSON 스키마 강제, 429는 한도 초과로 구분). 공개된 글만 보낸다는 규칙을 주석에 명시.
- 확인: 키로 모델 목록 조회 → 무료 등급에서 gemini-flash-latest·flash-lite-latest 모두 JSON 응답 정상, 관리자 연결 점검 "정상 응답".
- 남은 것: 수집 파이프라인(분류·요약)에 연결하면 관리자 화면에서 상태를 "사용 중"으로 바꾼다. 운영 ENV_FILE에도 GEMINI_API_KEY 추가 필요(deploy.md).

### 22. 연동 재확인과 Gemini 중심 운영안 ✅

- 사용자 확인: KOPIS 가입 대상은 공연시설·기획제작사(사업자등록증·재직증명서). 금감원 오픈API 약관(사용자가 붙여 준 원문): 개인 즉시 발급·하루 10,000건·1인 1키, **9조 실시간 연동만·복제·저장 금지·출처 표기**.
- Gemini 무료 등급 실측: JSON ✅, URL 읽기(url_context) ✅(스타벅스 이벤트 목록 페이지 읽기 성공), 구글 검색 연동 ❌(429, 할당량 0).
- `V8__integrations_review.sql`: KOPIS 종료·사업자 필요, 금감원 약관 메모, Gemini 용도·실측 결과.
- 운영안: content-ops-plan 9절(G1 재분류 · G2 종료 판정 · G3 링크 제보 자동 채움 · G4 브랜드 페이지 감시 · G5 주간 큐레이션 · G6 요약, 무료 등급 운영 규칙, 바뀐 단계 P0~P3).

### 23. Gemini 분석·자동화·인사이트 기능 ✅

- 요구: 무료 등급 한도를 직접 찾아 적용, 기존 데이터를 Gemini 로 직접 처리(마이그레이션), 어디까지 되는지 확인해 작업량 재산정, 자동화, 게시물마다 사용자에게 의미 있는 분석 기능(DB·기능 설계·구현). trendist(같은 3.5 Flash-Lite, 다른 키)와 한도가 겹칠 수 있으니 효율적으로.
- 한도: 429 응답에는 한도 값이 없음 → 공개 자료(3.5 Flash-Lite 10 RPM·500 RPD, 3.8 Flash 5 RPM·20 RPD, 검색 연동 불가)와 실측(검색 429, gemini-2.5 404, Gemma 503)으로 확정. 이벤터 몫 40%(`budgets`).
- DB `V9__ai_insights.sql`: `post_insights`(분야·혜택·브랜드·신뢰도·입력 해시·3줄 요약·대상·참여 방법·난이도·혜택 크기·진행 여부·재확인 일정), `brands`, `llm_usage`(모델·날짜별 요청·토큰·오류), `insight_reports`.
- 백엔드
  - `llm`: `LlmService`(모델별 분당·하루 몫, 태평양 시간 날짜, 429 쉬기, 사용량 기록), `GeminiClient`(JSON 스키마 + URL 읽기, 토큰·URL 결과 수집), 5xx 는 다시 시도할 오류로 구분.
  - `ai`: `InsightPipeline`(분류 50건/요청, 링크 읽기 4건/요청, 결과 값 검증, 해시로 재호출 방지), `WeeklyReportService`(숫자는 SQL, 해설만 Gemini, Flash → Flash-Lite 대체), `LinkPreviewService`(회원 하루 5번, 성공만 차감), `InsightQueryService`(상세·비슷한 이벤트·트렌드·브랜드), `AiJobs`(3분마다, 월요일 리포트).
  - 검색: `domain`·`benefit` 필터, "진행중"에서 AI 가 종료로 확인한 글 제외.
  - API: `GET /api/posts/{id}/insight`, `/api/insights/overview`, `/api/brands`, `/api/brands/{name}`, `POST /api/ai/link-preview`(회원), `/api/admin/ai/status`·`/run`(관리자). LLM 오류는 500 대신 503 안내.
- 프론트: 상세 AI 카드·비슷한 이벤트(`components/InsightCard.tsx`), 목록 분야·혜택 필터, `/insights`(`InsightsPage`), `/brands/:name`(`BrandPage`), 글쓰기 링크로 채우기, 메인 트렌드 미리보기, 헤더 "트렌드", 관리자 Gemini 사용량 패널. 막대는 한 계열이라 브랜드 색 하나·값 직접 표기·표로 보기 제공.
- 마이그레이션 결과·실측·재산정: content-ops-plan 10절(1,362건 분류, 링크 읽기 187 진행 중·23 종료 확인, 오늘 Flash-Lite 115회·Flash 2회).
- 고친 것: Gemini 가 빈 문자열 enum 을 거부(400) → "없음" 사용. 503 묶음을 실패로 3일 미루던 것 → 다시 시도로(16건 대기열 복구). 링크로 채우기에서 못 읽은 시도까지 횟수 차감 → 성공만.
- 확인: 백엔드 테스트·빌드, 프론트 타입 검사·빌드, 트렌드·브랜드·상세·목록 필터·관리자·모바일 메인 캡처(가로 넘침·콘솔 오류 없음), 링크로 채우기 실제 호출(스타벅스 9초).

### 24. 목록 화면·선택 상자 개편 ✅

- 요구: "최신순"·"전국" 같은 필터의 기본 선택 상자가 너무 기본 디자인 → 개선. 핫딜·커뮤니티·공지 목록이 너무 단순 → UX/UI 관점에서 트렌디하게. 이벤트 목록도 개선.
- 공통 `Dropdown`(`components/Dropdown.tsx`, `styles/dropdown.css`): 둥근 버튼 + 아이콘, 팝오버(설명·✓·아이콘), **모바일은 아래에서 올라오는 시트**, 키보드(↑↓·Home/End·Enter·Esc), listbox ARIA. 정렬(🆕👀👍⏰), 축제 지역(지역별 개수), 이벤트 분야·혜택에 사용. 폼의 기본 select 도 화살표·포커스 링을 통일.
- 목록 머리: 게시판 아이콘·이름·한 줄 소개·개수, **스크롤해도 따라오는 도구 막대**(이벤트는 상태 세그먼트 + 분야·혜택·정렬 드롭다운), 적용 중인 AI 필터 칩과 "모두 지우기", 모바일 떠 있는 글쓰기 버튼.
- 게시판별 항목(`components/BoardItems.tsx`, `styles/board.css`)
  | 게시판 | 모양 |
  |---|---|
  | 핫딜 | 상품 카드 그리드(모바일 2·태블릿 3·데스크톱 5열), 쇼핑몰 칩·무료배송, **가격 크게**, 2주 지난 특가는 "지난 특가" 표시(최근은 NEW) |
  | 커뮤니티 | 피드 카드: 작성자 아바타·등급·시간, 제목, **본문 미리보기 2줄**, 추천·댓글·조회, 인기 글 🔥, 사진 있으면 오른쪽 썸네일 |
  | 공지 | 알림 목록: 📢/📌 아이콘, 필독·NEW, 미리보기 한 줄, 날짜 |
  | 검색 | 게시판 칩·브랜드·가격 또는 AI 혜택 한 줄 |
  | 이벤트 | 카드에 AI 혜택 한 줄(✦ 최대 50% 할인 등) |
- 백엔드: 목록 응답에 댓글 수·고정 여부·핫딜 가격·본문 미리보기(90자)·AI 혜택 한 줄·분야 추가. 처음엔 핫딜 목록이 1.3초로 느려짐 → 원인은 선택 목록의 서브쿼리를 6.8만 행 전체에 계산한 것 → **두 단계 조회**(정렬·페이지로 id 20개 → 그 글만 서브쿼리·본문)로 0.22초(약 6배, 이전보다도 빠름).
- 확인: 타입 검사·빌드·백엔드 테스트, 핫딜·자유·공지·이벤트·검색 데스크톱/모바일/다크 캡처(가로 넘침·콘솔 오류 없음), 드롭다운 마우스·키보드 선택으로 주소 바뀜, 축제 지역 선택(서울 27개).

### 25. 운영 폴더·nginx·프론트 CI 정리 ✅

- 이미지: 사용자 확인 결과 운영 이미지는 `/home/ubuntu/image/postimage/YYYYMMDD`(nginx·백엔드 공용 폴더). 원래 계획(`content/eventor/uploads`로 이동) 대신 **그대로 사용** → 파일 이동·301 없음. 화면 주소 `/uploads/…`와 옛 주소 `/postimage/…` 모두 nginx 가 `/image/postimage` 에서 바로 서빙.
- nginx(사용자가 준 compose 기준): `./content:/var/www`, `./image/postimage:/image/postimage` 마운트가 이미 있어 **compose 변경 없음**. `deploy/nginx/eventor.conf` 의 root 를 `/var/www/eventor/releases` 로. 서버의 `sites-available/default` 안 eventor 블록 두 개만 교체.
- 업로드 폴더: 클라이언트가 보낸 폴더 이름을 형식만 검사하던 것 → `postimage` 만 허용(nginx 가 그 폴더만 마운트).
- 프론트 CI: trendist-front 와 같은 형식(빌드 → `/tmp/eventor-build` → releases 지우고 새로 → nginx reload). 다른 점 두 가지: Node 는 `.nvmrc`(26, React Router 8 이 22.22 이상 요구), 직전 빌드 assets 한 세대 유지(배포 순간 열린 화면에서 따로 받는 파일 404 방지). 처음 짠 방식은 이월된 파일이 계속 이월돼 쌓이는 문제를 로컬 시뮬레이션(배포 4번)으로 발견 → 빌드별 파일 목록(`.assets-current`, releases 밖)으로 직전 빌드 것만 이월.
- 백엔드: releases 를 지웠다 다시 만들면 releases 자체를 마운트한 컨테이너는 옛 폴더를 본다 → **상위 폴더** `/home/ubuntu/content/eventor:/app/web:ro` 마운트, `index-file: ./web/releases/index.html`.
- 백엔드 compose(사용자가 준 서버 `/home/ubuntu/eventor/eventor-back/docker-compose.yml`): 기존 `../../image:/app/image` 를 `../../image:/app/uploads` 로(호스트 폴더 그대로, 컨테이너 쪽 이름만), `../../content/eventor:/app/web:ro` 추가. 포트·env_file·네트워크는 그대로. 예시 파일을 서버 구조와 똑같이 맞춤.
- 배포 스크립트(사용자가 준 서버 `deploy.sh`): 컨테이너 Up 만 확인하고 nginx 를 넘겨, 새 버전 첫 기동(Flyway)·기동 실패 때 502·장애 위험 → `deploy/scripts/deploy.sh`: 두 인스턴스 HTTP 헬스 체크 성공 후에만 전환, 180초 대기, `nginx -t` 후 reload, 실패 시 옛 색 유지·새 색 로그 출력 후 내림. 파일 이름(`docker-compose.blue.yml`/`green.yml`)·포트(8101/8102, 8103/8104)·upstream 파일 경로는 기존 그대로.
- 서버 `version.txt`·`start.sh`: `restart: always` + 배포 스크립트의 옛 색 `down` 으로 대체되어 불필요(사용자 확인 요청에 답함). 컷오버·재부팅 확인 후 삭제하도록 deploy.md "정리" 절에 기록.

