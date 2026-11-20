# Eventor 마이그레이션 설계서

작성 2026-11-19. 진행하면서 각 절과 맨 아래 "진행 기록"을 갱신한다.

## 1. 목표와 결정 사항

| 항목 | 이전 | 이후 |
|---|---|---|
| 백엔드 빌드 | Maven, Spring Boot 3.3.5, Java 21 | Gradle(KTS), Spring Boot 4.1.2, Java 25 |
| 프론트 | Spring Boot + Thymeleaf(SSR), Docker 블루/그린 | React(Vite + TS) 정적 빌드, nginx가 직접 서빙(도커 없음) |
| SEO 페이지 | 프론트 Thymeleaf | 백엔드가 게시물 상세·카테고리·sitemap.xml을 메타/본문이 담긴 HTML로 응답, React가 이어받음 |
| 검색 | Elasticsearch | MySQL FULLTEXT(ngram) |
| 캐시 | Redis | Caffeine(인스턴스별, 짧은 TTL) |
| 인기 검색어 | Redis ZSET | DB 테이블 |
| 이메일 인증 코드 | Redis(3분 TTL) | DB 테이블(인스턴스가 2개 이상이라 메모리 불가) |
| 스케줄 리더 선출 | Redis 락 | ShedLock(JDBC) |
| 리프레시 토큰 | Redis | DB 테이블(토큰 해시 저장) |
| OAuth | 구글·카카오·네이버 | 구글만. 기존 카카오·네이버 계정은 같은 이메일로 구글 로그인 시 연결 |
| 외부 API 호출 | Spring Cloud OpenFeign | Spring `RestClient` (Spring Cloud 제거) |
| DB 스키마 관리 | `ddl-auto: update` | Flyway(V1 = 현재 운영 스키마 기준선) |
| 이미지 | DB에 `https://eventor.duckdns.org/postimage/...` 절대 URL, 프로필별 경로 | DB에 `/uploads/...` 상대 경로, 저장 루트는 어디서나 `./uploads` |
| 백엔드 배포 | 블루/그린 | 블루/그린 유지 |
| 로컬 포트 | 프론트 8001, 백엔드 8101 | 프론트 9080(Vite), 백엔드 9081 |

## 2. DB 구성

- 운영 MySQL 서버(`220.71.172.204:3306`, 도커 내부명 `mysql-server-1`)에 개발용 DB `eventor_dev`를 만들고 운영 DB 데이터를 복제한다.
- 로컬(dev 프로필)은 `eventor_dev`, 운영(prod 프로필)은 운영 DB를 쓴다.
- Flyway 마이그레이션은 `eventor_dev`에서 먼저 돌려 검증하고, 운영 DB에는 컷오버 때 적용한다.
  - 컷오버 전까지 운영 구버전이 운영 DB를 쓰므로 운영 DB는 건드리지 않는다.
  - 컷오버 직전 운영 DB를 백업(mysqldump)한다.
- `ddl-auto`는 `validate`(로컬)·`none`(운영). 엔티티와 스키마가 다르면 시작 시 오류로 알게 한다.
- 다시 복제할 때(운영 데이터를 최신으로): `eventor_dev`를 지우고 다시 만든 뒤 덤프를 붓고, Flyway가 마이그레이션을 다시 적용한다.

## 3. 인증

- 브라우저는 같은 도메인의 `/api/**`를 직접 부른다(프론트 서버 없음).
- 토큰은 백엔드가 HttpOnly 쿠키로 내려준다. `access_token`(10분) / `refresh_token`(7일), 둘 다 Path=/api.
  운영은 `Secure`, 모두 `SameSite=Lax`.
- 리프레시 토큰은 DB `refresh_token` 테이블에 SHA-256 해시로 저장. 재발급할 때마다 회전하고, 동시 요청 경합을 막으려고 회전된 토큰은 30초 동안 액세스 토큰 재발급만 허용한다.
- 권한은 기존처럼 `@AuthorizeRole` AOP. 역할은 재발급 때 DB에서 다시 읽는다.
- 구글 로그인: Authorization Code 흐름 + `state` 검증(서명 쿠키).
  - 콜백: `https://eventor.duckdns.org/api/auth/google/callback`, `http://localhost:9080/api/auth/google/callback`
  - 클라이언트 ID·보안 비밀번호는 git에 올리지 않는 `application.env`와 GitHub `ENV_FILE` 시크릿에만 둔다.

## 4. 이미지 저장 구조

- 저장 루트 `eventor.storage.root`(기본 `./uploads`). 로컬은 `eventor-back/uploads`, 운영 컨테이너는 `/app/uploads`(호스트 `/home/ubuntu/content/eventor/uploads` 마운트).
- 공개 URL은 항상 `/uploads/{상대경로}`. DB에는 이 상대 URL만 저장(도메인 없음).
- 운영 nginx가 `/uploads/`를 호스트 폴더에서 직접 서빙. 로컬은 Vite가 `/uploads`를 백엔드로 넘기고, 백엔드가 파일이 없으면 운영 도메인으로 302(읽기 폴백).
- 기존 `/postimage/...` URL은 데이터 마이그레이션으로 `/uploads/postimage/...`로 바꾸고, nginx에서 옛 주소를 301로 넘긴다.

## 5. nginx·서버 폴더 구조(제안)

```
/home/ubuntu/content/eventor/
├── releases/      # React 빌드(dist) — GitHub Actions가 업로드
└── uploads/       # 이미지 원본 — 백엔드 컨테이너가 쓰고 nginx가 읽기 전용으로 서빙
    ├── postimage/
    └── sitemap/   # (제거 예정: sitemap은 백엔드가 DB에서 바로 생성)
```

## 6. 단계

| 단계 | 내용 | 상태 |
|---|---|---|
| 0 | eventor_dev 생성·운영 데이터 복제(scripts/clone-prod-to-dev.sh) | 완료 |
| 1 | Gradle·Boot 4·Java 25, ES·Redis·Feign·카카오·네이버 제거 | 완료 |
| 2 | Flyway V1 기준선 + V2 신규 테이블·인덱스 + V3·V4 이미지 상대 경로 | eventor_dev 완료, 운영은 컷오버 때 |
| 3 | 인증 재구성(쿠키, DB 리프레시 토큰, 구글 state) | 완료 |
| 4 | 이미지 경로 통일 | 완료(서버 이동은 deploy.md 3) |
| 5 | API `/api/**` 정리 | 완료 |
| 6 | React 프론트 | 완료 |
| 7 | SEO HTML·sitemap | 완료 |
| 8 | 배포(프론트 정적, 백엔드 블루/그린, nginx) | 파일 준비 완료, 서버 적용 대기(deploy.md) |
| 9 | 컷오버 후 DB 정리 | 대기 |

## 7. 진행 기록

- 2026-11-19: 분석, 설계서 작성.
