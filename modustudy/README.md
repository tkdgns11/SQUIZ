최초 작성자 : 윤상훈(0107)
최종 수정자 : 윤상훈(0107) 변경사유 :  

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
frontend/
├── public/                 # 정적 자원
└── src/
    ├── main.tsx            # 엔트리 포인트
    ├── App.tsx             # 전역 설정 (QueryClient, Theme, 전역 가드)
    │
    ├── api/                # [Service Layer] API 통신 관련
    │   ├── axios.ts        # Axios 인스턴스 (Interceptor, 토큰 주입)
    │   └── endpoints/      # 도메인별 API 함수 (authApi, studyApi, aiApi 등)
    │
    ├── assets/             # 스타일 및 미디어
    │   ├── images/
    │   ├── icons/
    │   └── styles/         # Tailwind CSS 또는 Global SCSS
    │
    ├── shared/             # [Shared Layer] 전역 공통 모듈
    │   ├── components/     # UI Kit (Button, Modal, Input, Spinner 등)
    │   ├── hooks/          # useAuth, useSocket, useIntersectionObserver
    │   ├── utils/          # date-formatter, storage-util
    │   └── types/          # 전역 공목 타입 (User, APIResponse 등)
    │
    ├── layouts/            # [Layout Layer] 페이지 레이아웃
    │   ├── MainLayout.tsx  # 헤더 + 사이드바 포함 기본 레이아웃
    │   ├── AuthLayout.tsx  # 로그인/회원가입 전용 심플 레이아웃
    │   └── WorkspaceLayout.tsx # 화상회의/채팅 전용 와이드 레이아웃
    │
    ├── features/           # [Feature Layer] 도메인별 응집화 (핵심!)
    │   ├── auth/           # 로그인, 회원가입, 랜딩페이지
    │   ├── dashboard/      # 홈 대시보드, 잔디 그래프, 통계 위젯
    │   ├── study/          # 스터디 탐색, 목록, 상세, 생성
    │   ├── workspace/      # 실시간 채팅, 채널 관리, 파일함
    │   ├── meeting/        # WebRTC 화상 회의, 미팅 기록, AI 요약 탭
    │   ├── quiz/           # 실시간 퀴즈 대회, 꼬멘틀, 오답 노트
    │   ├── daily-retro/    # 데일리 리포트 작성, KPT 회고 분석
    │   ├── attendance/     # BLE 출석 현황, 소명 관리
    │   ├── recruitment/    # 팀원 모집 게시판, 지원자 관리
    │   ├── profile/        # 내 프로필, 알림 설정, 연동 관리
    │   └── admin/          # 서비스 통계, 신고 처리, 공지사항 관리
    │
    ├── routes/             # [Routing Layer] 경로 설정
    │   ├── index.tsx       # 메인 라우터 (Lazy Loading 적용)
    │   ├── PublicRoute.tsx # 비로그인 사용자 전용
    │   └── PrivateRoute.tsx # 로그인 필수 가드
    │
    ├── store/              # [State Layer] 전역 상태 (Zustand)
    │   ├── authStore.ts    # 사용자 인증 및 토큰 정보
    │   ├── workspaceStore.ts # 현재 활성화된 채널/팀 정보
    │   └── uiStore.ts      # 모달, 사이드바 토글 상태
    │
    └── types/              # 도메인별 상세 타입 정의 (.d.ts)
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
