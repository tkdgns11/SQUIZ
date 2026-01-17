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
    ├── features/           # [Feature Layer] 도메인별 응집화 (핵심!)
    │   ├── auth/           # 로그인, 회원가입, 랜딩페이지
    │   │   └── index.tsx
    │   ├── dashboard/      # 홈 대시보드, 잔디 그래프, 통계 위젯
    │   │   └── index.tsx
    │   ├── study/          # 스터디 탐색, 목록, 상세, 생성
    │   │   └── index.tsx
    │   ├── workspace/      # 실시간 채팅, 채널 관리, 파일함
    │   │   └── index.tsx
    │   ├── meeting/        # WebRTC 화상 회의, 미팅 기록, AI 요약 탭
    │   │   └── index.tsx
    │   ├── quiz/           # 실시간 퀴즈 대회, 꼬멘틀, 오답 노트
    │   │   └── index.tsx
    │   ├── daily-retro/    # 데일리 리포트 작성, KPT 회고 분석
    │   │   └── index.tsx
    │   ├── attendance/     # BLE 출석 현황, 소명 관리
    │   │   └── index.tsx
    │   ├── recruitment/    # 팀원 모집 게시판, 지원자 관리
    │   │   └── index.tsx
    │   ├── profile/        # 내 프로필, 알림 설정, 연동 관리
    │   │   └── index.tsx
    │   └── admin/          # 서비스 통계, 신고 처리, 공지사항 관리
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
