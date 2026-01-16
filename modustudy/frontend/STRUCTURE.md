# Frontend 폴더 구조

```
frontend/
├── public/                 # 정적 자원
│   └── index.html
│
└── src/
    ├── main.tsx            # 엔트리 포인트
    ├── App.tsx             # 전역 설정 (QueryClient, Theme, 전역 가드)
    │
    ├── api/                # [Service Layer] API 통신 관련
    │   ├── axios.ts        # Axios 인스턴스 (Interceptor, 토큰 주입)
    │   └── endpoints/      # 도메인별 API 함수
    │       ├── authApi.ts  # 인증 관련 API
    │       ├── studyApi.ts # 스터디 관련 API
    │       └── aiApi.ts    # AI 관련 API
    │
    ├── assets/             # 스타일 및 미디어
    │   ├── images/         # 이미지 파일
    │   ├── icons/          # 아이콘 파일
    │   └── styles/         # 전역 스타일
    │       └── index.css   # Global CSS
    │
    ├── shared/             # [Shared Layer] 전역 공통 모듈
    │   ├── components/     # UI Kit
    │   │   ├── Button.tsx
    │   │   ├── Modal.tsx
    │   │   ├── Input.tsx
    │   │   └── Spinner.tsx
    │   ├── hooks/          # 공통 커스텀 훅
    │   │   ├── useAuth.ts
    │   │   ├── useSocket.ts
    │   │   └── useIntersectionObserver.ts
    │   ├── utils/          # 유틸리티 함수
    │   │   ├── date-formatter.ts
    │   │   └── storage-util.ts
    │   └── types/          # 전역 공통 타입
    │       └── index.ts
    │
    ├── layouts/            # [Layout Layer] 페이지 레이아웃
    │   ├── MainLayout.tsx      # 헤더 + 사이드바 포함 기본 레이아웃
    │   ├── AuthLayout.tsx      # 로그인/회원가입 전용 심플 레이아웃
    │   └── WorkspaceLayout.tsx # 화상회의/채팅 전용 와이드 레이아웃
    │
    ├── features/           # [Feature Layer] 도메인별 응집화 (백엔드 기준 통일)
    │   ├── user/           # 사용자 인증, 프로필, 소셜 계정
    │   │   └── index.tsx
    │   ├── friend/         # 친구 관계 관리
    │   │   └── index.tsx
    │   ├── dm/             # 다이렉트 메시지
    │   │   └── index.tsx
    │   ├── study/          # 스터디 관리, 멤버, 세션
    │   │   └── index.tsx
    │   ├── chat/           # 채팅/채널 관리, 메시지
    │   │   └── index.tsx
    │   ├── meeting/        # WebRTC 화상 회의, 참가자, 회의록
    │   │   └── index.tsx
    │   ├── quiz/           # 퀴즈 대회, 문제, 코스
    │   │   └── index.tsx
    │   ├── attendance/     # 출석 관리, 세션 메모
    │   │   └── index.tsx
    │   ├── retrospect/     # 회고 작성, 회고 항목
    │   │   └── index.tsx
    │   ├── gamification/   # 배지, 경험치, 레벨 시스템
    │   │   └── index.tsx
    │   ├── material/       # 학습 자료, 커리큘럼, 진도
    │   │   └── index.tsx
    │   ├── notification/   # 알림 관리, 알림 설정, FCM
    │   │   └── index.tsx
    │   ├── report/         # 보고서 관리
    │   │   └── index.tsx
    │   ├── daily/          # 데일리 리포트, 일일 항목
    │   │   └── index.tsx
    │   ├── board/          # 게시판, 댓글, 좋아요
    │   │   └── index.tsx
    │   ├── recruitment/    # 팀원 모집, 지원 관리
    │   │   └── index.tsx
    │   ├── comendte/       # 꼬멘틀 게임
    │   │   └── index.tsx
    │   ├── news/           # IT 뉴스, 북마크
    │   │   └── index.tsx
    │   ├── ai/             # AI 피드백 서비스
    │   │   └── index.tsx
    │   ├── dashboard/      # 홈 대시보드 (프론트 전용)
    │   │   └── index.tsx
    │   └── admin/          # 관리자 페이지 (프론트 전용)
    │       └── index.tsx
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
    └── types/              # 도메인별 상세 타입 정의
        └── index.d.ts
```

## 구조 설명

### 📁 레이어별 역할

1. **Service Layer** (`api/`)
   - API 통신 담당
   - Axios 인스턴스 설정 및 인터셉터
   - 도메인별 API 엔드포인트 관리

2. **Shared Layer** (`shared/`)
   - 전역적으로 재사용되는 컴포넌트, 훅, 유틸리티
   - UI Kit 컴포넌트 (Button, Modal, Input 등)
   - 공통 비즈니스 로직 (useAuth, useSocket 등)

3. **Layout Layer** (`layouts/`)
   - 페이지 레이아웃 템플릿
   - 인증 여부, 페이지 타입에 따른 다양한 레이아웃

4. **Feature Layer** (`features/`)
   - 도메인별 기능 응집화
   - 각 기능은 독립적으로 관리
   - 컴포넌트, 훅, 타입 등을 기능별로 그룹화

5. **Routing Layer** (`routes/`)
   - 라우팅 설정 및 가드
   - Lazy Loading 적용
   - 권한별 라우트 분리

6. **State Layer** (`store/`)
   - 전역 상태 관리 (Zustand)
   - 도메인별 스토어 분리

## 다음 단계

1. `package.json` 설정 및 의존성 설치
2. TypeScript 설정 (`tsconfig.json`)
3. Vite 설정 (`vite.config.ts`)
4. 각 레이어별 상세 구현
