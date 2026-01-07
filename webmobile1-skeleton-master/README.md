# StudySync - 스켈레톤 프로젝트

SSAFY 2학기 프로젝트를 위한 **스켈레톤 프로젝트**입니다.
Spring Boot 3 + React 18 기반의 인증 시스템이 구현되어 있습니다.

## 1. Tech Stack

| 구분 | 기술 | 버전 |
| ---- | ---- | ---- |
| Frontend | React 18, TypeScript, Vite 5, Zustand, React Router 6 | Node.js 20+ |
| Backend | Spring Boot 3.2.2, Spring Security 6, QueryDSL 5 | Java 21, Gradle 8.5 |
| Database | MySQL 8.0 | `ssafy_web_db` |
| Auth | JWT (auth0 java-jwt 4.4.0) | Bearer Token |
| API Docs | Swagger (springdoc-openapi 2.3.0) | `/swagger-ui/index.html` |

## 2. Prerequisites

- **Java 21** (`JAVA_HOME` 설정)
- **MySQL 8.0**
- **Node.js 20+** + npm
- **IDE**: IntelliJ / STS4 (Lombok 플러그인 설치 필요)

## 3. 빠른 실행

### 3.1 데이터베이스 설정

```sql
CREATE DATABASE IF NOT EXISTS ssafy_web_db COLLATE utf8mb4_general_ci;
```

### 3.2 백엔드 실행

```bash
cd backend

# application.properties에서 DB 계정 설정
# spring.datasource.username=ssafy
# spring.datasource.password=ssafy

./gradlew bootRun
```

- API 서버: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

### 3.3 프론트엔드 실행

```bash
cd frontend
npm install    # 최초 1회
npm run dev    # http://localhost:3000
```

## 4. 프로젝트 구조

```
webmobile1-skeleton-master/
├── backend/
│   ├── build.gradle
│   └── src/main/java/com/ssafy/
│       ├── api/
│       │   ├── controller/
│       │   │   ├── AuthController.java      # 로그인 API
│       │   │   └── UserController.java      # 회원가입, 내 정보 조회
│       │   ├── request/                     # 요청 DTO
│       │   ├── response/                    # 응답 DTO
│       │   └── service/                     # 비즈니스 로직
│       ├── common/
│       │   ├── auth/
│       │   │   ├── JwtAuthenticationFilter.java
│       │   │   ├── SsafyUserDetails.java
│       │   │   └── SsafyUserDetailService.java
│       │   └── util/
│       │       └── JwtTokenUtil.java        # JWT 토큰 생성/검증
│       ├── config/
│       │   ├── SecurityConfig.java          # Spring Security 설정
│       │   └── WebMvcConfig.java            # CORS 설정
│       └── db/
│           ├── entity/                      # JPA 엔티티
│           └── repository/                  # JPA 리포지토리
│
└── frontend/
    ├── package.json
    ├── vite.config.ts
    └── src/
        ├── App.tsx                          # 라우팅 설정
        ├── main.tsx                         # 엔트리 포인트
        ├── store/
        │   ├── authStore.ts                 # 인증 상태 (Zustand)
        │   └── menuStore.ts                 # 메뉴 상태
        └── views/
            ├── main/
            │   ├── MainLayout.tsx           # 레이아웃
            │   └── components/
            │       ├── MainHeader.tsx       # 헤더 (로그인/로그아웃)
            │       ├── MainSidebar.tsx      # 사이드바
            │       ├── MainFooter.tsx       # 푸터
            │       ├── LoginDialog.tsx      # 로그인 다이얼로그
            │       └── RegisterDialog.tsx   # 회원가입 다이얼로그
            ├── home/
            │   └── Home.tsx                 # 홈 페이지 (더미)
            ├── history/
            │   └── History.tsx              # 히스토리 페이지 (더미)
            └── conferences/
                └── ConferenceDetail.tsx     # 상세 페이지 (더미)
```

## 5. API 명세

| Method | Endpoint | 설명 | 인증 |
| ------ | -------- | ---- | ---- |
| POST | `/api/v1/users` | 회원가입 | X |
| POST | `/api/v1/auth/login` | 로그인 (JWT 발급) | X |
| GET | `/api/v1/users/me` | 내 정보 조회 | O |

## 6. 주요 포트

| 서비스 | 포트 |
| ------ | ---- |
| Backend (Spring Boot) | 8080 |
| Frontend (Vite) | 3000 |
| MySQL | 3306 |

## 7. 구현된 기능

- [x] 회원가입
- [x] 로그인 (JWT 토큰 발급)
- [x] 로그인 상태 유지 (localStorage)
- [x] 로그아웃
- [x] 인증 필터 (JwtAuthenticationFilter)
- [x] Swagger API 문서

## 8. 추가 구현 필요

- [ ] Conference (화상회의) API
- [ ] 히스토리 페이지 연동
- [ ] 사용자 프로필 수정
- [ ] 기타 비즈니스 로직

## 9. Troubleshooting

| 문제 | 해결 |
| ---- | ---- |
| Gradle 빌드 실패 | Java 21 설치 및 `JAVA_HOME` 설정 확인 |
| Lombok 에러 (STS4) | [Lombok 다운로드](https://projectlombok.org/download) 후 설치 |
| DB 연결 실패 | MySQL 실행 여부, 계정/비밀번호 확인 |
| 프론트 API 호출 실패 | 백엔드 먼저 실행 후 프론트 실행 |
| CORS 에러 | `WebMvcConfig.java` CORS 설정 확인 |

### Lombok 설정

**IntelliJ IDEA** (플러그인 기본 내장)
- Settings → Build → Compiler → Annotation Processors → **Enable annotation processing** 체크

**STS4 / Eclipse** (수동 설치 필요)
1. [lombok.jar 다운로드](https://projectlombok.org/download)
2. 다운받은 `lombok.jar` 더블클릭 실행
3. IDE 경로 선택 → **Install / Update** 클릭
4. IDE 재시작 → 프로젝트 우클릭 → **Gradle → Refresh Gradle Project**
