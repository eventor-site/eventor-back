# 기술 스택

마이그레이션(2026-11) 목표 기준. "이전"은 마이그레이션 전 구성이다. 진행 상황은 [migration.md](migration.md) 6절.

## 백엔드 (`eventor-back`)

| 구분 | 사용 기술 | 이전 |
|---|---|---|
| 언어·런타임 | Java 25 | Java 21 |
| 프레임워크 | Spring Boot 4.1.2 (Web MVC, Security, Validation, Mail, Cache, Actuator, AOP) | Spring Boot 3.3.5 |
| 빌드 | Gradle 9 (Kotlin DSL) | Maven |
| ORM·쿼리 | Spring Data JPA(Hibernate 7), QueryDSL 7(OpenFeign 포크) | Hibernate 6, QueryDSL 5 |
| DB | MySQL 8.0 (`eventor` 운영 / `eventor_dev` 개발, 같은 서버) | 동일 |
| DB 마이그레이션 | Flyway | `ddl-auto: update` |
| 커넥션 풀 | HikariCP(기본) | commons-dbcp2 |
| 검색 | MySQL FULLTEXT(ngram) | Elasticsearch |
| 캐시 | Caffeine | Redis |
| 스케줄 락 | ShedLock(JDBC) | Redis 리더 선출 |
| 인증 | JWT(jjwt) 액세스 토큰 + DB 리프레시 토큰, HttpOnly 쿠키 | JWT + Redis 리프레시 토큰, 프론트 서버가 쿠키 관리 |
| 소셜 로그인 | 구글(OAuth 2.0 Authorization Code) | 구글·카카오·네이버 |
| 외부 API 호출 | Spring `RestClient` | Spring Cloud OpenFeign |
| SEO HTML | React index.html에 메타·본문 주입(게시물 상세·카테고리), 사이트맵 색인 동적 생성 | 프론트 Thymeleaf, sitemap 파일에 덧붙이기 |
| HTML 정화 | jsoup Safelist(저장 시) | 없음 |
| 이미지 처리 | scrimage(webp 변환) | 동일 |
| 크롤러 | Selenium(핫딜, 현재 스케줄 꺼짐. 도커 이미지에 Chrome 없음) | Selenium + Chrome |
| API 문서 | springdoc-openapi 3 | springdoc 2 |
| LLM | Gemini API(Google AI Studio 무료 등급) — 3.5 Flash-Lite(분류·링크 읽기), 3.8 Flash(주간 리포트). REST `generateContent` + JSON 스키마 + URL 읽기, `LlmService` 가 모델별 몫·사용량 관리 | 없음 |
| 외부 연동 | 한국관광공사 Tour API(축제), Gmail SMTP — 전체 목록은 관리자 > 외부 연동 | 관광공사(축제·주변 관광지), VWorld 지오코더, Gmail SMTP |

## 프론트엔드 (`eventor-front`)

| 구분 | 사용 기술 | 이전 |
|---|---|---|
| 프레임워크 | React + TypeScript | Spring Boot + Thymeleaf(SSR) |
| 런타임 | Node 26 LTS(CI, `.nvmrc`) | - |
| 빌드·개발 서버 | Vite (로컬 9080, `/api`·`/uploads` 백엔드 프록시) | Maven, 8001 |
| 화면 | 반응형(모바일 우선), 디자인 전면 개편 | 데스크톱 중심 |
| 서버 상태·캐시 | TanStack Query | - |
| 라우팅 | React Router 8 | 서버 라우팅 |
| 에디터 | Toast UI Editor 3(위지윅, 필요할 때만 불러옴) | Toast UI Editor(CDN) |
| 본문 정화 | DOMPurify | 없음 |
| 배포 | 정적 빌드(dist)를 서버 폴더에 rsync, nginx가 서빙 | Docker 블루/그린 |

## 인프라·배포

| 구분 | 사용 기술 |
|---|---|
| 서버 | Ubuntu, Docker(백엔드·MySQL·nginx) |
| 웹 서버 | nginx(도커) — TLS(Let's Encrypt), 정적 파일, `/api` 프록시, `/uploads` 이미지 |
| CI/CD | GitHub Actions — 백엔드: Gradle 빌드 → Docker Hub → 블루/그린 배포 / 프론트: Vite 빌드 → scp 업로드 |
| 비밀값 | 로컬 `src/main/resources/application.env`(git 제외), 운영 GitHub `ENV_FILE` 시크릿 |

## 로컬 실행

| 대상 | 명령 | 주소 |
|---|---|---|
| 백엔드 | `cd eventor-back && ./gradlew bootRun` | http://localhost:9081 |
| 프론트 | `cd eventor-front && npm run dev` | http://localhost:9080 (브라우저는 이 주소 하나로 접속) |
