### 🛠 최적화된 작업 로드맵

#### **1단계: 폴더 구조화 및 빈 파일 생성**

먼저 아키텍처에 따라 파일을 생성합니다. (제시하신 `features` 기반 구조 준수)

* `src/layouts/MainLayout.tsx`
* `src/layouts/components/Sidebar.tsx`
* `src/store/uiStore.ts`
* `src/assets/styles/index.css` (또는 `tailwind.config.js`)

#### **2단계: 전역 상태(Store) 설정**

사이드바의 열림/닫힘 상태를 먼저 정의해야 레이아웃을 잡을 수 있습니다.

```typescript
// src/store/uiStore.ts
import { create } from 'zustand';

interface UIState {
  isSidebarOpen: boolean;
  toggleSidebar: () => void;
}

export const useUIStore = create<UIState>((set) => ({
  isSidebarOpen: true, 
  toggleSidebar: () => set((state) => ({ isSidebarOpen: !state.isSidebarOpen })),
}));

```

#### **3단계: 전역 스타일 및 테마 적용**

제시하신 4가지 파스텔 컬러를 프로젝트 전역에서 쓸 수 있게 등록합니다.

```javascript
// tailwind.config.js (추천)
theme: {
  extend: {
    colors: {
      'study-bg': '#FFF7DD',    // 배경 & 사이드바
      'study-blue': '#80A1BA',  // 메인 포인트 (신뢰)
      'study-teal': '#91C4C3',  // 보조 포인트 (퀴즈)
      'study-green': '#B4DEBD', // 액센트 (스터디/완료)
      'study-text': '#454A4F',  // 기본 텍스트
    },
    borderRadius: {
      'google': '12px',         // 구글스러운 라운드 값
    }
  }
}

```

#### **4단계: 레이아웃 및 사이드바 구현 (Framer Motion)**

이제 상태와 스타일이 준비되었으니 애니메이션을 입힌 컴포넌트를 만듭니다.

**Sidebar.tsx 핵심 로직:**

* `isSidebarOpen`에 따라 `width` 애니메이션 적용.
* 퀴즈/스터디 등 핵심 기능에는 포인트 컬러 아이콘과 **배지(Badge)** 추가.

**MainLayout.tsx 핵심 로직:**

* 사이드바와 메인 콘텐츠를 `flex`로 배치.
* `layout` prop을 사용하여 사이드바가 움직일 때 메인 영역도 부드럽게 리사이징되도록 설정.

---

### ✅ 최종 체크리스트 (통일된 지침)

1. **배경의 연속성**: 사이드바와 메인 화면의 배경색은 반드시 동일한 `study-bg`로 설정하여 경계를 허무세요.
2. **부드러운 스프링**: 모든 애니메이션에는 아래 설정을 고정값으로 사용하세요.
* `transition={{ type: 'spring', stiffness: 300, damping: 30 }}`


3. **기능 부각 (Primacy)**:
* **퀴즈 메뉴**: 아이콘 옆에 `#91C4C3` 색상의 숫자 뱃지 배치.
* **스터디 메뉴**: 현재 스터디 중임을 알리는 작은 녹색 점(`#B4DEBD`) 배치.


4. **아이콘**: `material-icons`를 사용하여 구글 캘린더의 시각적 언어를 유지하세요.

이제 **1단계(폴더 및 파일 생성)**부터 시작해 보시겠어요? 막히는 파일의 구체적인 코드가 필요하면 바로 말씀해 주세요!