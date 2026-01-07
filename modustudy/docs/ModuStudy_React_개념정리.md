최초 작성자 : 윤상훈(0108)
최종 수정자 : 윤상훈(0108) 변경 사유 : 

# WebMobile1 Skeleton - React 개념 정리 및 흐름

> SSAFY 2학기 프로젝트 프로젝트 코드 이해를 위한 React 개념 가이드

---

## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [기술 스택](#2-기술-스택)
3. [프로젝트 구조](#3-프로젝트-구조)
4. [TypeScript 기초](#4-typescript-기초)
5. [핵심 React 개념](#5-핵심-react-개념)
6. [상태 관리 (Zustand)](#6-상태-관리-zustand)
7. [라우팅 (React Router v6)](#7-라우팅-react-router-v6)
8. [API 통신 (Axios)](#8-api-통신-axios)
9. [컴포넌트 흐름도](#9-컴포넌트-흐름도)
10. [데이터 흐름 상세](#10-데이터-흐름-상세)
11. [주요 파일별 역할](#11-주요-파일별-역할)

---

## 1. 프로젝트 개요

이 프로젝트는 **화상 회의/미팅 플랫폼**의 기존으로, 인증(로그인/회원가입)과 기본 UI 구조가 구현되어 있습니다.

### 주요 특징
- **프론트엔드**: React 18 + TypeScript + Vite
- **백엔드**: Spring Boot 3 + Spring Security + JWT
- **상태관리**: Zustand (Redux 대안, 더 간단함)
- **빌드도구**: Vite (Webpack보다 빠름)

---

## 2. 기술 스택

### Frontend 의존성 (`package.json`)

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| `react` | 18.3.1 | UI 프레임워크 |
| `react-dom` | 18.3.1 | DOM 렌더링 |
| `react-router-dom` | 6.x | 클라이언트 사이드 라우팅 |
| `zustand` | 4.x | 전역 상태 관리 |
| `axios` | 1.x | HTTP 클라이언트 (API 호출) |
| `@mdi/font` | - | Material Design 아이콘 |
| `typescript` | 5.4.5 | 타입 안전성 |
| `vite` | 5.1.0 | 빌드 도구 & 개발 서버 |

---

## 3. 프로젝트 구조

```
frontend/
├── src/
│   ├── main.tsx                 # 앱 진입점 (ReactDOM.render)
│   ├── App.tsx                  # 라우팅 설정
│   │
│   ├── store/                   # Zustand 전역 상태
│   │   ├── authStore.ts         # 인증 상태 (토큰, 로그인/로그아웃)
│   │   └── menuStore.ts         # 메뉴 상태 (사이드바)
│   │
│   ├── views/                   # 페이지 컴포넌트
│   │   ├── main/
│   │   │   ├── MainLayout.tsx   # 레이아웃 (헤더+사이드바+푸터)
│   │   │   └── components/
│   │   │       ├── MainHeader.tsx
│   │   │       ├── MainSidebar.tsx
│   │   │       ├── MainFooter.tsx
│   │   │       ├── LoginDialog.tsx
│   │   │       └── RegisterDialog.tsx
│   │   ├── home/
│   │   │   └── Home.tsx         # 홈 페이지
│   │   ├── history/
│   │   │   └── History.tsx      # 히스토리 페이지
│   │   └── conferences/
│   │       └── ConferenceDetail.tsx
│   │
│   ├── common/
│   │   ├── css/common.css       # 전역 스타일
│   │   ├── api/                 # API 서비스 (확장용)
│   │   └── lib/                 # 유틸리티 (확장용)
│   │
│   └── assets/                  # 정적 리소스
│       ├── fonts/               # 스포카 한 산스 폰트
│       └── images/              # 로고, 이미지
│
├── vite.config.ts               # Vite 설정
├── tsconfig.json                # TypeScript 설정
└── index.html                   # HTML 템플릿
```

---

## 4. TypeScript 기초

> **TypeScript를 처음 접하는 분들을 위한 기초 가이드입니다.**

### 4.1 TypeScript란?

TypeScript는 **JavaScript에 타입(Type)을 추가한 언어**입니다.

```
JavaScript: 동적 타입 → 실행해봐야 오류 발견
TypeScript: 정적 타입 → 코드 작성 시점에 오류 발견 (빨간 줄!)
```

#### 왜 사용하나요?

```typescript
// JavaScript - 문제를 실행 전에 모름
function greet(name) {
  return "Hello, " + name.toUpperCase();
}
greet(123);  // 런타임 에러! (실행해봐야 앎)

// TypeScript - 작성 중에 빨간 줄로 알려줌
function greet(name: string): string {
  return "Hello, " + name.toUpperCase();
}
greet(123);  // ❌ 컴파일 에러: number는 string에 할당 불가
greet("철수");  // ✅ 정상 동작
```

**핵심 장점:**
- **자동완성**: 객체 뒤에 `.` 찍으면 사용 가능한 메서드/속성 표시
- **오류 사전 방지**: 빨간 줄로 버그 미리 발견
- **협업 용이**: 타입이 곧 문서 역할

---

### 4.2 기본 타입

#### 원시 타입 (Primitive Types)

```typescript
// 변수명: 타입 = 값
let name: string = "철수";
let age: number = 25;
let isStudent: boolean = true;
let nothing: null = null;
let notDefined: undefined = undefined;
```

#### 배열 (Array)

```typescript
// 방법 1: 타입[]
let numbers: number[] = [1, 2, 3];
let names: string[] = ["철수", "영희"];

// 방법 2: Array<타입> (제네릭 문법)
let numbers2: Array<number> = [1, 2, 3];
```

#### 객체 (Object)

```typescript
// 인라인 타입 정의
let user: { name: string; age: number } = {
  name: "철수",
  age: 25
};
```

#### 함수 (Function)

```typescript
// 파라미터 타입 + 반환 타입
function add(a: number, b: number): number {
  return a + b;
}

// 화살표 함수
const multiply = (a: number, b: number): number => {
  return a * b;
};

// 반환값 없으면 void
function log(message: string): void {
  console.log(message);
}
```

---

### 4.3 타입 추론 (Type Inference)

TypeScript는 타입을 **자동으로 추론**합니다. 모든 곳에 타입을 쓸 필요 없어요!

```typescript
// 명시적 타입
let name: string = "철수";

// 타입 추론 (TypeScript가 알아서 string으로 인식)
let name2 = "철수";  // 이것도 string 타입!

// 함수 반환값도 추론됨
function add(a: number, b: number) {
  return a + b;  // 반환 타입: number (자동 추론)
}
```

**권장**: 변수 선언 시 초기값이 있으면 타입 생략해도 OK!

---

### 4.4 인터페이스 (Interface)

**객체의 형태(구조)를 정의**합니다. 가장 많이 사용!

```typescript
// 인터페이스 정의
interface User {
  id: number;
  name: string;
  email: string;
  age?: number;        // ? = 선택적 속성 (있어도 되고 없어도 됨)
  readonly createdAt: Date;  // readonly = 수정 불가
}

// 사용
const user: User = {
  id: 1,
  name: "철수",
  email: "cs@test.com",
  createdAt: new Date()
};

user.name = "영희";      // ✅ OK
user.createdAt = new Date();  // ❌ 에러: readonly 속성
```

#### React 컴포넌트 Props에서 사용

```typescript
// Props 인터페이스 정의
interface ButtonProps {
  label: string;
  onClick: () => void;
  disabled?: boolean;
}

// 컴포넌트에서 사용
const Button: React.FC<ButtonProps> = ({ label, onClick, disabled }) => {
  return (
    <button onClick={onClick} disabled={disabled}>
      {label}
    </button>
  );
};
```

---

### 4.5 타입 별칭 (Type Alias)

`type` 키워드로 타입에 이름을 붙입니다.

```typescript
// 기본 사용
type ID = number | string;  // number 또는 string
type Status = "pending" | "approved" | "rejected";  // 리터럴 타입

let userId: ID = 123;
userId = "ABC123";  // 둘 다 OK

let status: Status = "pending";
status = "invalid";  // ❌ 에러: 정의된 값만 가능
```

#### Interface vs Type

```typescript
// 거의 같은 기능!
interface UserInterface {
  name: string;
  age: number;
}

type UserType = {
  name: string;
  age: number;
};

// 차이점:
// - interface: 선언 병합 가능, 상속(extends) 용이
// - type: 유니온(|), 인터섹션(&) 등 복잡한 타입에 유리
```

**권장**: 객체 형태는 `interface`, 그 외(유니온 등)는 `type` 사용

---

### 4.6 유니온 타입 (Union Type)

여러 타입 중 하나를 가질 수 있습니다.

```typescript
// string 또는 number
let id: string | number;
id = "ABC";  // ✅
id = 123;    // ✅
id = true;   // ❌ boolean은 안됨

// 함수 파라미터에서
function printId(id: string | number) {
  if (typeof id === "string") {
    console.log(id.toUpperCase());  // string일 때만 가능
  } else {
    console.log(id);  // number
  }
}
```

---

### 4.7 제네릭 (Generics)

**타입을 파라미터처럼 전달**할 수 있습니다. 재사용 가능한 컴포넌트를 만들 때 유용!

```typescript
// T는 타입 파라미터 (어떤 타입이든 가능)
function identity<T>(arg: T): T {
  return arg;
}

// 사용할 때 타입 지정
identity<string>("hello");  // 반환: string
identity<number>(123);      // 반환: number

// 타입 추론도 가능
identity("hello");  // T가 string으로 자동 추론
```

#### 실제 활용 예시

```typescript
// API 응답 타입
interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

// 사용
interface User {
  id: number;
  name: string;
}

const response: ApiResponse<User> = {
  code: 200,
  message: "성공",
  data: { id: 1, name: "철수" }
};

// 배열 응답
const listResponse: ApiResponse<User[]> = {
  code: 200,
  message: "성공",
  data: [
    { id: 1, name: "철수" },
    { id: 2, name: "영희" }
  ]
};
```

---

### 4.8 React에서 자주 쓰는 타입

#### 컴포넌트 타입

```typescript
// 방법 1: React.FC (Function Component)
const MyComponent: React.FC<Props> = ({ name }) => {
  return <div>{name}</div>;
};

// 방법 2: 직접 타입 지정 (요즘 더 권장됨)
const MyComponent = ({ name }: Props) => {
  return <div>{name}</div>;
};
```

#### 이벤트 타입

```typescript
// 클릭 이벤트
const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
  console.log("클릭!", e.target);
};

// 입력 이벤트
const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
  console.log("입력값:", e.target.value);
};

// 폼 제출 이벤트
const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
  e.preventDefault();
};
```

#### useState 타입

```typescript
// 타입 추론 (초기값에서 추론)
const [count, setCount] = useState(0);  // number

// 명시적 타입 (초기값이 null일 때)
const [user, setUser] = useState<User | null>(null);

// 배열
const [items, setItems] = useState<string[]>([]);
```

#### useRef 타입

```typescript
// DOM 요소 참조
const inputRef = useRef<HTMLInputElement>(null);

// 일반 값 저장 (렌더링 사이에 유지)
const countRef = useRef<number>(0);
```

---

### 4.9 프로젝트 코드 실제 예시

#### authStore.ts에서의 타입

```typescript
// 상태와 액션 타입 정의
interface AuthState {
  token: string | null;           // null 가능
  setToken: (token: string | null) => void;  // 반환값 없는 함수
  login: (id: string, password: string) => Promise<boolean>;  // 비동기 함수
  logout: () => void;
}

// Zustand 스토어에 타입 적용
export const useAuthStore = create<AuthState>()(
  // ...
);
```

#### LoginDialog.tsx에서의 타입

```typescript
// Props 인터페이스
interface LoginDialogProps {
  onClose: () => void;  // 닫기 콜백
}

// 컴포넌트에 타입 적용
const LoginDialog: React.FC<LoginDialogProps> = ({ onClose }) => {
  // useState - 문자열 타입 (자동 추론)
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');

  // 이벤트 핸들러 타입
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    // ...
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    // ...
  };

  return (
    // JSX
  );
};
```

#### 컴포넌트 Props 예시

```typescript
// ConferenceCard 컴포넌트 Props
interface ConferenceCardProps {
  id: number;
  title: string;
  participants: number;
  isActive?: boolean;  // 선택적
  onJoin: (conferenceId: number) => void;
}

const ConferenceCard: React.FC<ConferenceCardProps> = ({
  id,
  title,
  participants,
  isActive = false,  // 기본값
  onJoin
}) => {
  return (
    <div className={`card ${isActive ? 'active' : ''}`}>
      <h3>{title}</h3>
      <p>{participants}명 참여 중</p>
      <button onClick={() => onJoin(id)}>참여</button>
    </div>
  );
};
```

---

### 4.10 자주 하는 실수 & 해결법

#### 1. `any` 남용

```typescript
// ❌ 나쁜 예 - any는 타입 체크 무력화
let data: any = fetchData();

// ✅ 좋은 예 - 구체적인 타입 정의
interface ApiData {
  id: number;
  name: string;
}
let data: ApiData = fetchData();
```

#### 2. null/undefined 체크

```typescript
// ❌ 에러: user가 null일 수 있음
const user: User | null = null;
console.log(user.name);

// ✅ 옵셔널 체이닝
console.log(user?.name);  // undefined 반환

// ✅ null 체크
if (user) {
  console.log(user.name);
}

// ✅ 널 병합 연산자
console.log(user?.name ?? "이름 없음");
```

#### 3. 이벤트 타입 모르겠을 때

```typescript
// 마우스를 올려보면 타입이 나옴!
<input onChange={(e) => {
  // e 위에 마우스 올리면: React.ChangeEvent<HTMLInputElement>
}} />

// 또는 타입을 먼저 쓰고 자동완성
const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
  // e.target.value 자동완성됨!
};
```

---

### 4.11 TypeScript 설정 파일 (tsconfig.json)

프로젝트의 TypeScript 설정입니다:

```json
{
  "compilerOptions": {
    "target": "ES2020",          // 변환 대상 JS 버전
    "module": "ESNext",          // 모듈 시스템
    "strict": true,              // 엄격 모드 (권장!)
    "jsx": "react-jsx",          // JSX 처리 방식
    "baseUrl": ".",              // 기본 경로
    "paths": {
      "@/*": ["./src/*"]         // @ = src 경로 별칭
    }
  }
}
```

**`strict: true`**가 중요합니다. 이게 켜져 있으면:
- `null`/`undefined` 체크 강제
- 암시적 `any` 금지
- 더 안전한 코드 작성 가능

---

## 5. 핵심 React 개념

### 5.1 JSX (JavaScript XML)

HTML처럼 보이지만 JavaScript인 문법입니다.

```tsx
// JSX 예시 (MainHeader.tsx)
return (
  <header className="main-header">
    <div className="logo">ModuStudy</div>
    <button onClick={handleLogin}>로그인</button>
  </header>
);
```

**핵심 규칙**:
- `class` → `className` (예약어 회피)
- 모든 태그는 닫아야 함 (`<img />`, `<br />`)
- JavaScript 표현식은 `{}` 안에

---

### 5.2 컴포넌트 (Component)

UI를 독립적인 조각으로 나눈 것. **함수형 컴포넌트**를 사용합니다.

```tsx
// 함수형 컴포넌트 기본 구조
const MainHeader: React.FC = () => {
  return (
    <header>
      {/* 컴포넌트 내용 */}
    </header>
  );
};

export default MainHeader;
```

**Props**: 부모 → 자식으로 데이터 전달

```tsx
// 부모 컴포넌트
<ConferenceCard title="스터디 모임" participants={5} />

// 자식 컴포넌트
interface ConferenceCardProps {
  title: string;
  participants: number;
}

const ConferenceCard: React.FC<ConferenceCardProps> = ({ title, participants }) => {
  return <div>{title} - {participants}명</div>;
};
```

---

### 5.3 Hooks (훅)

함수형 컴포넌트에서 상태와 생명주기를 다루는 함수들입니다.

#### `useState` - 컴포넌트 내부 상태

```tsx
const [isOpen, setIsOpen] = useState(false);  // 초기값: false

// 상태 변경 → 컴포넌트 리렌더링
setIsOpen(true);
```

#### `useEffect` - 사이드 이펙트 처리

```tsx
// 컴포넌트 마운트 시 실행 (빈 배열)
useEffect(() => {
  console.log("컴포넌트가 마운트됨");

  // 클린업 함수 (언마운트 시 실행)
  return () => {
    console.log("컴포넌트가 언마운트됨");
  };
}, []);

// 의존성 변경 시 실행
useEffect(() => {
  console.log("token이 변경됨:", token);
}, [token]);
```

#### `useNavigate` - 프로그래밍 방식 페이지 이동

```tsx
import { useNavigate } from 'react-router-dom';

const navigate = useNavigate();

// 페이지 이동
navigate('/home');
navigate('/conferences/123');
```

#### `useParams` - URL 파라미터 추출

```tsx
// URL: /conferences/123
import { useParams } from 'react-router-dom';

const { conferenceId } = useParams();
console.log(conferenceId);  // "123"
```

---

### 5.4 조건부 렌더링

```tsx
// 삼항 연산자
{isLoggedIn ? <LogoutButton /> : <LoginButton />}

// && 연산자 (조건이 true일 때만 렌더링)
{isLoading && <Spinner />}

// 논리 OR (falsy일 때 대체값)
{username || "Guest"}
```

---

### 5.5 리스트 렌더링

```tsx
const items = ["React", "Vue", "Angular"];

return (
  <ul>
    {items.map((item, index) => (
      <li key={index}>{item}</li>  // key 필수!
    ))}
  </ul>
);
```

**`key` 속성**: React가 어떤 항목이 변경/추가/삭제되었는지 식별하는 데 사용. 고유한 값 필요 (가능하면 id 사용, index는 비권장).

---

## 6. 상태 관리 (Zustand)

### 6.1 Zustand란?

Redux보다 **간단한** 전역 상태 관리 라이브러리입니다.

- 보일러플레이트 최소화
- Redux DevTools 지원
- TypeScript 친화적

### 6.2 authStore.ts 분석

```tsx
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import axios from 'axios';

// 상태 타입 정의
interface AuthState {
  token: string | null;
  setToken: (token: string | null) => void;
  login: (id: string, password: string) => Promise<boolean>;
  logout: () => void;
}

// 스토어 생성
export const useAuthStore = create<AuthState>()(
  persist(                              // localStorage 자동 저장
    (set) => ({
      // 상태
      token: null,

      // 액션: 토큰 설정
      setToken: (token) => {
        set({ token });
        if (token) {
          axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        } else {
          delete axios.defaults.headers.common['Authorization'];
        }
      },

      // 액션: 로그인
      login: async (id, password) => {
        try {
          const response = await axios.post('/api/v1/auth/login', { id, password });
          const { accessToken } = response.data;
          set({ token: accessToken });
          axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
          return true;
        } catch (error) {
          return false;
        }
      },

      // 액션: 로그아웃
      logout: () => {
        set({ token: null });
        delete axios.defaults.headers.common['Authorization'];
      },
    }),
    {
      name: 'auth-storage',  // localStorage 키 이름
    }
  )
);
```

### 6.3 컴포넌트에서 사용

```tsx
import { useAuthStore } from '@/store/authStore';

const LoginDialog: React.FC = () => {
  // 스토어에서 필요한 것만 가져오기
  const { login, token } = useAuthStore();

  const handleSubmit = async () => {
    const success = await login(id, password);
    if (success) {
      alert("로그인 성공!");
    }
  };

  // token 상태에 따라 UI 변경
  if (token) {
    return <div>이미 로그인됨</div>;
  }

  return (
    <form onSubmit={handleSubmit}>
      {/* 폼 내용 */}
    </form>
  );
};
```

### 6.4 menuStore.ts 분석

```tsx
interface MenuItem {
  icon: string;
  label: string;
  path: string;
}

interface MenuState {
  menuItems: MenuItem[];
  activeIndex: number;
  setActiveIndex: (index: number) => void;
}

export const useMenuStore = create<MenuState>((set) => ({
  menuItems: [
    { icon: 'mdi-home', label: '홈', path: '/' },
    { icon: 'mdi-history', label: '히스토리', path: '/history' },
    { icon: 'mdi-logout', label: '로그아웃', path: '/logout' },
  ],
  activeIndex: 0,
  setActiveIndex: (index) => set({ activeIndex: index }),
}));
```

---

## 7. 라우팅 (React Router v6)

### 7.1 라우팅 설정 (App.tsx)

```tsx
import { Routes, Route } from 'react-router-dom';
import MainLayout from './views/main/MainLayout';
import Home from './views/home/Home';
import History from './views/history/History';
import ConferenceDetail from './views/conferences/ConferenceDetail';

function App() {
  return (
    <Routes>
      {/* 중첩 라우팅: MainLayout이 공통 레이아웃 */}
      <Route path="/" element={<MainLayout />}>
        <Route index element={<Home />} />                    {/* / */}
        <Route path="history" element={<History />} />        {/* /history */}
        <Route path="conferences/:conferenceId" element={<ConferenceDetail />} />
      </Route>
    </Routes>
  );
}
```

### 7.2 중첩 라우팅과 Outlet

```tsx
// MainLayout.tsx
import { Outlet } from 'react-router-dom';

const MainLayout: React.FC = () => {
  return (
    <div className="main-layout">
      <MainHeader />
      <MainSidebar />

      {/* 자식 라우트가 여기에 렌더링됨 */}
      <main className="content">
        <Outlet />
      </main>

      <MainFooter />
    </div>
  );
};
```

### 7.3 라우트 구조도

```
URL: /
┌─────────────────────────────────────────────┐
│  MainLayout                                 │
│  ┌─────────────────────────────────────┐   │
│  │ MainHeader                           │   │
│  └─────────────────────────────────────┘   │
│  ┌──────┐  ┌──────────────────────────┐   │
│  │Side  │  │ <Outlet />               │   │
│  │bar   │  │                          │   │
│  │      │  │  / → Home.tsx            │   │
│  │      │  │  /history → History.tsx  │   │
│  │      │  │  /conferences/:id →      │   │
│  │      │  │       ConferenceDetail   │   │
│  └──────┘  └──────────────────────────┘   │
│  ┌─────────────────────────────────────┐   │
│  │ MainFooter                           │   │
│  └─────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
```

---

## 8. API 통신 (Axios)

### 8.1 Axios 기본 설정

```tsx
// vite.config.ts - 개발 서버 프록시 설정
export default defineConfig({
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 백엔드 서버
        changeOrigin: true,
      },
    },
  },
});
```

### 8.2 API 호출 패턴

```tsx
import axios from 'axios';

// GET 요청
const fetchUserInfo = async () => {
  const response = await axios.get('/api/v1/users/me');
  return response.data;
};

// POST 요청
const login = async (id: string, password: string) => {
  const response = await axios.post('/api/v1/auth/login', {
    id,
    password,
  });
  return response.data;  // { code, message, accessToken }
};

// 헤더에 토큰 포함 (전역 설정)
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
```

### 8.3 API 엔드포인트

| 메서드 | 엔드포인트 | 인증 | 설명 |
|--------|-----------|------|------|
| POST | `/api/v1/auth/login` | X | 로그인 |
| POST | `/api/v1/users` | X | 회원가입 |
| GET | `/api/v1/users/me` | O (Bearer) | 내 정보 조회 |

---

## 9. 컴포넌트 흐름도

### 9.1 전체 컴포넌트 계층 구조

```
main.tsx
  └── BrowserRouter
        └── App.tsx
              └── Routes
                    └── Route (MainLayout)
                          ├── MainHeader
                          │     ├── Logo
                          │     ├── SearchBar
                          │     ├── LoginButton → LoginDialog
                          │     └── RegisterButton → RegisterDialog
                          │
                          ├── MainSidebar
                          │     └── MenuItem[] (map)
                          │
                          ├── Outlet (페이지 컨텐츠)
                          │     ├── Home
                          │     │     └── ConferenceCard[] (map)
                          │     ├── History
                          │     └── ConferenceDetail
                          │
                          └── MainFooter
```

### 9.2 앱 초기화 흐름

```
[브라우저 시작]
       │
       ▼
index.html 로드
       │
       ▼
main.tsx 실행
       │
       ├── BrowserRouter 초기화
       ├── App 컴포넌트 마운트
       │
       ▼
authStore 초기화 (persist)
       │
       ├── localStorage에서 토큰 복원
       ├── 토큰 있으면 axios 헤더 설정
       │
       ▼
MainLayout 렌더링
       │
       ├── Header, Sidebar, Footer 렌더링
       ├── 현재 URL에 맞는 페이지 (Outlet)
       │
       ▼
[앱 준비 완료]
```

---

## 10. 데이터 흐름 상세

### 10.1 로그인 흐름

```
[사용자]
   │
   │ 1. 로그인 버튼 클릭
   ▼
┌─────────────────────────────────────────────────┐
│ MainHeader.tsx                                  │
│                                                 │
│ const [showLogin, setShowLogin] = useState()    │
│ setShowLogin(true)  ───────────────────────┐   │
│                                             │   │
│ {showLogin && <LoginDialog onClose={...} />}│   │
└─────────────────────────────────────────────│───┘
                                              │
                                              ▼
┌─────────────────────────────────────────────────┐
│ LoginDialog.tsx                                 │
│                                                 │
│ const [id, setId] = useState('')                │
│ const [password, setPassword] = useState('')    │
│ const { login } = useAuthStore()                │
│                                                 │
│ 2. 폼 입력 & 제출                               │
│                                                 │
│ const handleSubmit = async () => {              │
│   const success = await login(id, password)     │
│   if (success) onClose()                        │
│ }                                               │
└────────────────────│────────────────────────────┘
                     │
                     │ 3. login 액션 호출
                     ▼
┌─────────────────────────────────────────────────┐
│ authStore.ts (Zustand)                          │
│                                                 │
│ login: async (id, password) => {                │
│   const res = await axios.post(                 │
│     '/api/v1/auth/login',  ─────────────────┐  │
│     { id, password }                         │  │
│   )                                          │  │
│   set({ token: res.data.accessToken }) ←─────┤  │
│   return true                                │  │
│ }                                            │  │
└──────────────────────────────────────────────│──┘
                                               │
                     4. API 요청               │
                                               ▼
┌─────────────────────────────────────────────────┐
│ Backend (Spring Boot)                           │
│                                                 │
│ AuthController.login()                          │
│   ├── UserService.getUserByCredentials()        │
│   ├── PasswordEncoder.matches()                 │
│   └── JwtTokenUtil.createToken()                │
│                                                 │
│ Response: { accessToken: "eyJ..." }             │
└────────────────────│────────────────────────────┘
                     │
                     │ 5. 토큰 반환
                     ▼
┌─────────────────────────────────────────────────┐
│ authStore.ts                                    │
│                                                 │
│ set({ token: accessToken })                     │
│   │                                             │
│   ├── Zustand 상태 업데이트                      │
│   ├── localStorage 자동 저장 (persist)          │
│   └── axios 헤더 설정                           │
│       axios.defaults.headers.Authorization      │
│         = 'Bearer eyJ...'                       │
└────────────────────│────────────────────────────┘
                     │
                     │ 6. 상태 변경 → 리렌더링
                     ▼
┌─────────────────────────────────────────────────┐
│ MainHeader.tsx (리렌더링)                        │
│                                                 │
│ const { token } = useAuthStore()                │
│                                                 │
│ // token이 있으므로 로그아웃 버튼 표시           │
│ {token ? <LogoutButton /> : <LoginButton />}    │
└─────────────────────────────────────────────────┘
```

### 10.2 회원가입 흐름

```
[RegisterDialog]
       │
       │ axios.post('/api/v1/users', {id, password, name})
       ▼
[UserController.register()]
       │
       │ UserService.createUser()
       │   ├── 중복 체크
       │   └── PasswordEncoder.encode()
       ▼
[UserRepository.save(user)]
       │
       ▼
[MySQL INSERT]
       │
       ▼
[Response: 200 OK]
       │
       ▼
[다이얼로그 닫기 & 로그인 유도]
```

### 10.3 인증된 API 요청 흐름

```
[컴포넌트]
    │
    │ axios.get('/api/v1/users/me')
    │ (Authorization: Bearer eyJ... 자동 포함)
    ▼
[Vite Proxy]
    │
    │ localhost:8080/api/v1/users/me
    ▼
[Spring Security FilterChain]
    │
    ├── JwtAuthenticationFilter
    │     ├── 토큰 추출
    │     ├── JwtTokenUtil.validateToken()
    │     └── SecurityContext에 인증 정보 설정
    ▼
[UserController.getMe()]
    │
    │ @AuthenticationPrincipal
    ▼
[UserService.getUserById()]
    │
    ▼
[Response: UserRes]
```

---

## 11. 주요 파일별 역할

### 11.1 진입점

| 파일 | 역할 |
|------|------|
| `index.html` | HTML 템플릿, `<div id="root">` 포함 |
| `main.tsx` | React 앱 마운트, BrowserRouter 래핑 |
| `App.tsx` | 라우팅 설정 |

### 11.2 상태 관리

| 파일 | 역할 |
|------|------|
| `store/authStore.ts` | 인증 상태 (토큰, 로그인/로그아웃) |
| `store/menuStore.ts` | 사이드바 메뉴 상태 |

### 11.3 레이아웃 & 공통 컴포넌트

| 파일 | 역할 |
|------|------|
| `MainLayout.tsx` | 전체 레이아웃 (헤더+사이드바+Outlet+푸터) |
| `MainHeader.tsx` | 상단 헤더 (로고, 검색, 인증 버튼) |
| `MainSidebar.tsx` | 좌측 네비게이션 |
| `MainFooter.tsx` | 하단 푸터 |
| `LoginDialog.tsx` | 로그인 모달 |
| `RegisterDialog.tsx` | 회원가입 모달 |

### 11.4 페이지 컴포넌트

| 파일 | 라우트 | 역할 |
|------|--------|------|
| `Home.tsx` | `/` | 메인 홈 (회의 목록) |
| `History.tsx` | `/history` | 히스토리 |
| `ConferenceDetail.tsx` | `/conferences/:id` | 회의 상세 |

### 11.5 설정 파일

| 파일 | 역할 |
|------|------|
| `vite.config.ts` | Vite 설정 (프록시, 빌드 경로) |
| `tsconfig.json` | TypeScript 설정 |
| `package.json` | 의존성 관리 |

---

## 부록: 자주 사용하는 패턴

### A. 조건부 렌더링 패턴

```tsx
// 로그인 상태에 따른 버튼 표시
{token ? (
  <button onClick={logout}>로그아웃</button>
) : (
  <>
    <button onClick={() => setShowLogin(true)}>로그인</button>
    <button onClick={() => setShowRegister(true)}>회원가입</button>
  </>
)}
```

### B. 폼 핸들링 패턴

```tsx
const [formData, setFormData] = useState({ id: '', password: '' });

const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
  const { name, value } = e.target;
  setFormData(prev => ({ ...prev, [name]: value }));
};

const handleSubmit = async (e: React.FormEvent) => {
  e.preventDefault();
  // API 호출
};

return (
  <form onSubmit={handleSubmit}>
    <input name="id" value={formData.id} onChange={handleChange} />
    <input name="password" value={formData.password} onChange={handleChange} />
    <button type="submit">제출</button>
  </form>
);
```

### C. 모달 (다이얼로그) 패턴

```tsx
// 부모 컴포넌트
const [showModal, setShowModal] = useState(false);

return (
  <>
    <button onClick={() => setShowModal(true)}>모달 열기</button>
    {showModal && (
      <Modal onClose={() => setShowModal(false)}>
        <p>모달 내용</p>
      </Modal>
    )}
  </>
);

// Modal 컴포넌트
interface ModalProps {
  onClose: () => void;
  children: React.ReactNode;
}

const Modal: React.FC<ModalProps> = ({ onClose, children }) => {
  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <button className="close-btn" onClick={onClose}>×</button>
        {children}
      </div>
    </div>
  );
};
```

### D. API 에러 핸들링 패턴

```tsx
const [loading, setLoading] = useState(false);
const [error, setError] = useState<string | null>(null);

const fetchData = async () => {
  setLoading(true);
  setError(null);

  try {
    const response = await axios.get('/api/data');
    // 성공 처리
  } catch (err) {
    if (axios.isAxiosError(err)) {
      setError(err.response?.data?.message || '요청 실패');
    } else {
      setError('알 수 없는 오류');
    }
  } finally {
    setLoading(false);
  }
};
```

---

## 참고 자료

- [React 공식 문서](https://react.dev/)
- [React Router v6 문서](https://reactrouter.com/)
- [Zustand GitHub](https://github.com/pmndrs/zustand)
- [Vite 공식 문서](https://vitejs.dev/)
- [TypeScript 핸드북](https://www.typescriptlang.org/docs/)

---
