# SQUIZ

> ## 🧩 흩어진 공부를 모으고, 잊혀질 지식을 퀴즈로 묶다 — AI 올인원 스터디 플랫폼

- **서비스명**: SQUIZ (Study + Quiz)
- **개발 기간**: 2026.01.06 ~ 2026.02.13 (6주)
- **개발 인원**: 6명 (AI · Infra · Mobile · Backend · Frontend)

<br>

# 목차

- [👤 담당 역할 및 기여](#-담당-역할-및-기여)
- [💡 기획 배경](#-기획-배경)
- [✨ 서비스 주요 기능](#-서비스-주요-기능)
- [🛠️ 프로젝트 핵심 기술](#core-tech)
- [🗂️ ERD](#erd)
- [👥 팀원 소개](#-팀원-소개)
- [⚙️ 기술 스택](#tech-stack)

<br>

# 👤 담당 역할 및 기여

### 🤖 AI 서버

| 기능 | 구현 |
|:---|:---|
| **실시간 음성 자막** | 화상 회의 중 말이 끝날 때마다 음성을 글자로 바꿔 실시간 자막으로 제공 |
| **AI 요약 2단계 처리** | 원본 경량 LLM(4bit 양자화)으로 먼저 초벌 요약·압축한 뒤 고성능 AI(Claude)로 다듬어, 외부 AI 입력 토큰을 약 60% 절감 |
| **경량 모델 파인튜닝 실험** | 회의 요약 특화를 위해 학습 데이터 합성 + QLoRA 파인튜닝을 시도했으나, 데이터 품질·과적합으로 원본보다 품질이 낮아 원본 양자화 모델을 채택 |
| **커리큘럼 추천** | 학습 주제·기간만 입력하면 주차별 커리큘럼을 AI가 실시간으로 생성 |
| **복습 퀴즈 생성** | 회의 요약을 바탕으로 복습 퀴즈를 자동 생성 |

### ⚙️ 백엔드 (화상회의 · AI · 메시지 · 일정)

| 기능 | 구현 |
|:---|:---|
| **회의 · AI 결과 관리** | 화상 회의 생성·참가자·상태 관리, AI가 만든 요약·퀴즈 저장/조회 |
| **AI 폴링 스케줄러** | AI 처리 시작·결과 폴링·파일 동기화 주기를 나눠 운영(@Scheduled) |
| **실시간 자막 전송** | 음성 인식 결과를 회의 참가자 모두에게 실시간으로 전송 |
| **1:1 실시간 메시지** | 실시간 채팅과 친구 접속 상태 표시 (서버가 여러 대로 늘어나도 메시지가 빠짐없이 전달되도록 처리) |
| **구글 캘린더 연동** | 스터디 일정을 구글 캘린더와 자동 동기화 |
| **서버 간 연동** | 화상회의 서버·백엔드·AI 서버를 잇는 통신 처리 |
| **출석 검증** | 회의 시간·권한·중복을 확인해 출석/지각/결석 판정 |

### 📱 안드로이드 앱

| 기능 | 구현 |
|:---|:---|
| **블루투스 자동 출석** | 스터디장 근처에 가면 블루투스 신호로 자동 출석 처리 (오프라인 스터디) |
| **회의 녹음** | 전화가 오면 자동으로 멈췄다 다시 녹음하고, 다른 앱을 써도 백그라운드에서 유지 |
| **푸시 알림** | 복습·일정·채팅 등 알림 수신, 복습 알림을 누르면 오늘의 퀴즈로 바로 연결 |
| **화면 구성** | 로그인·스터디·출석·복습 퀴즈·알림 등 70여 개 화면 구현 |
| **구글 캘린더 연동** | 앱 안에서 스터디 일정을 구글 캘린더와 통합 |

### 🚀 인프라 · 팀장

| 기능 | 구현 |
|:---|:---|
| **무중단 배포** | 새 버전을 띄워 정상 확인 후 트래픽을 옮기고, 문제가 생기면 즉시 이전 버전으로 되돌려 서비스 중단 없이 배포 |
| **이미지 경량화 빌드** | 서비스를 담는 컨테이너 이미지를 작게 만들어 배포 속도·용량을 최적화 |
| **컨테이너 운영** | 백엔드·프론트·화상회의·AI 등 여러 서비스를 컨테이너로 묶어 클라우드(AWS)에서 운영 |
| **자동 빌드·배포** | 코드를 올리면 자동으로 빌드·배포되도록 구성 |
| **서버 모니터링** | CPU·메모리·디스크·네트워크 사용량과 컨테이너별 상태·로그를 실시간으로 확인하고, 문제가 생긴 컨테이너를 화면에서 바로 재시작할 수 있는 모니터링 대시보드를 직접 구축 |
| **데이터 구조 설계** | 117개 테이블을 회원·인증 / 스터디 / 세션·출석 / 미팅 AI / 퀴즈 / 복습 / 게이미피케이션 / 채팅 / 알림·일정 / AI 추천 등 도메인으로 설계 |
| **팀 리딩** | 6명 팀의 일정 관리, 4개 서버가 맞물리는 흐름 등 프로젝트 문서 작성 |

### 🎤 발표
- 서비스 개요 · 주요 기능 · 시연 · 기술 설명 발표

<br>

# 💡 기획 배경

### 스터디가 5주를 못 넘기는 이유

<table>
  <tr>
    <td align="center" width="25%">☁️<br/><b>흩어진 도구</b><br/><sub>일정은 카톡, 모임은 Zoom, 기록은 Notion — 도구 전환으로 집중력이 분산</sub></td>
    <td align="center" width="25%">🏫<br/><b>운영 부담 집중</b><br/><sub>출석은 일일이 확인하고 수동 기록, 회의록은 누군가의 희생 — 스터디장 피로 누적</sub></td>
    <td align="center" width="25%">🔁<br/><b>복습의 부재</b><br/><sub>다시 정리·복습할 엄두가 안 남 — 배운 내용의 70%가 24시간 안에 휘발</sub></td>
    <td align="center" width="25%">📉<br/><b>휘발되는 기록</b><br/><sub>열심히 해도 남는 데이터가 없어 성장이 보이지 않고 동기가 사라짐</sub></td>
  </tr>
</table>

### **🧩 SQUIZ 🧩**

> **만들고 — 만나고 — 복습하고, 전부 한곳에서**

- 🤖 **AI 자동 정리** — 화상 모임이 끝나면 요약·오개념 교정·액션아이템·복습 퀴즈를 AI가 한 번에 생성
- 🧠 **맞춤 복습** — 망각 곡선 이론 기반으로 잊어버리기 직전에 다시 출제, 약점 분석으로 취약 분야 추천
- 📦 **올인원** — 모집·진행·관리·복습을 웹과 앱 하나로 통합
- 📱 **웹 + 앱 연속성** — PC에서는 웹으로, 오프라인에서는 BLE 출석·녹음까지 모바일로 끊김 없이

<br>

# ✨ 서비스 주요 기능

### 👥 스터디 모집 · 관리 · 출석

- 일반/번개 스터디 생성, 팀원 모집 공고 → 신청·승인 → 멤버·역할 관리
- 출석 관리 (출석/지각/결석 기록·통계, 소명 제출·승인)

### 🗓 AI 학습 계획

- 주제만 입력하면 AI가 주차별 커리큘럼을 자동 생성 (SSE 스트리밍 실시간 생성)
- 스터디 로드맵으로 현재 진행 상황 시각화

### 🎥 화상 모임 + AI 자동 정리

- WebRTC 기반 화상 회의, 실시간 STT 자막 표시, 회의 중 자동 출석
- 종료 후 AI가 요약·오개념 교정·심화 학습 추천·액션아이템·복습 퀴즈를 자동 생성
- 요약 결과를 PDF·마크다운으로 저장

### 🧩 퀴즈 복습 시스템

- **오늘의 복습** — 망각 곡선(FSRS) 기반으로 잊어버리기 직전에 자동 재출제
- **코스 퀴즈** — 운영체제·네트워크·DB 등 CS 분야별 단계 학습
- **약점 분석 · 오답 노트** — 분야별 정답률로 취약점 파악, 틀린 문제 모아 재학습
- **단어 추론 게임(꼬멘틀, 3D 시각화)** · **실시간 퀴즈 대결**

### 💬 워크스페이스 + 캘린더

- 스터디별 실시간 채팅·자료실·메시지 핀 고정
- 1:1 다이렉트 메시지(DM)와 친구 접속 상태 실시간 표시
- 개인 일정 + 스터디 일정 통합 캘린더 (Google Calendar 연동)

### 🔔 알림

- 스터디 일정·복습 리마인드·새 메시지·출석 시작 등 실시간 푸시 알림
- 알림 종류별 분류·읽음 처리

### 📱 모바일 앱 (Android)

- BLE 비콘 기반 자동 출석 (오프라인 스터디)
- FCM 푸시 알림 (복습 리마인드·일정 알림)
- 오프라인 모임 녹음 → AI 자동 정리 연동

### 🏆 게이미피케이션

- 학습 활동으로 경험치 획득, 레벨업·뱃지 시스템
- GitHub 잔디 스타일 활동 히트맵

### 📰 뉴스 큐레이션

- 개발·CS·IT 뉴스를 모아 제공, 북마크

### 📋 기록 · 커뮤니티

- 게시판(자유·정보 공유), 스터디 회고, 데일리 학습 리포트

### 🔐 소셜 로그인 · 마이페이지

- Kakao · Naver · Google 소셜 로그인(OAuth2)
- 프로필·레벨·뱃지·활동 기록 관리

<br>

<a name="core-tech"></a>

# 🛠️ 프로젝트 핵심 기술

### 🎥 화상 회의 → 실시간 자막 → AI 정리

- **WebRTC(mediasoup SFU)** 로 다자 화상 회의를 제공하고, 발화가 끝날 때마다 음성을 인식해 **WebSocket(STOMP)** 으로 참가자 모두에게 자막을 실시간 전송
- 회의가 끝나면 AI가 요약·복습 퀴즈까지 자동 생성 — 화상회의(SFU)·백엔드·AI 서버 3개를 하나의 흐름으로 연결

### ⚡ 회의 후 대기 시간 단축

- **문제** — 기존에는 회의가 끝난 뒤 전체 녹음을 한꺼번에 음성 인식(STT)해 대기 시간이 길었음
- **해결** — 회의 중 발화 단위로 STT를 미리 처리·누적하는 **스트리밍 구조**로 바꿔, 회의가 끝나면 곧바로 요약 단계로 넘어가 체감 대기 시간을 크게 단축

### 🧠 가벼운 AI + 고성능 AI 2단계 요약

- **원본 Qwen3-8B를 4bit 양자화한 경량 LLM**으로 먼저 초벌 요약해 핵심을 압축하고, 고성능 LLM(Claude)으로 다듬는 2단계 방식으로 외부 AI 입력 토큰을 약 60% 절감
  - *(회의 요약 특화 파인튜닝도 시도했으나, 학습 데이터 품질·과적합으로 원본보다 품질이 낮아 원본 양자화 모델을 채택)*
- **보안** — 회의 원문을 외부 AI에 그대로 보내지 않고, 내부(온프레미스 GPU)에서 먼저 요약한 결과만 전송해 민감한 원문 노출을 줄임

### 📡 블루투스 자동 출석

- 스터디장 앱이 **BLE 비콘(iBeacon)** 신호를 송출하면, 근처 멤버 앱이 이를 감지해 출석 API를 호출 — 서버가 세션 시간·권한·중복을 검증해 출석/지각/결석 판정

### 🚀 무중단 배포

- **Blue-Green 배포**로 새 버전을 띄워 헬스체크 후 **Nginx의 트래픽 전환**으로 넘기고, 문제가 생기면 즉시 이전 버전으로 롤백 — 화상 회의 서비스 특성상 잠깐의 멈춤도 없게 처리

<br>

<a name="erd"></a>

# 🗂️ ERD

> 회원·인증 · 스터디 · 세션·출석 · 미팅 AI · 퀴즈 · 복습 · 게이미피케이션 · 채팅 · 알림·일정 · AI 추천

<div align="center">
  <img src="career/SQUIZ_개발중.png" width="900" alt="SQUIZ ERD"/>
</div>

<br>

# 👥 팀원 소개

<table>
  <tr>
    <td align="center">
      <img src="https://img.shields.io/badge/팀장%20%7C%20Backend%20%7C%20AI%20%7C%20Mobile-4285F4?style=for-the-badge&logoColor=white"/>
    </td>
    <td align="center">
      <img src="https://img.shields.io/badge/Fullstack%20Developer-6DB33F?style=for-the-badge&logoColor=white"/>
    </td>
    <td align="center">
      <img src="https://img.shields.io/badge/Fullstack%20Developer-6DB33F?style=for-the-badge&logoColor=white"/>
    </td>
  </tr>
  <tr>
    <td align="center"><b>윤상훈</b><br/><sub>tkdgns1998@gmail.com</sub></td>
    <td align="center"><b>조문희</b><br/><sub>chachaasdfgm@gmail.com</sub></td>
    <td align="center"><b>김민재</b><br/><sub>minjae000715@gmail.com</sub></td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://img.shields.io/badge/Fullstack%20Developer-6DB33F?style=for-the-badge&logoColor=white"/>
    </td>
    <td align="center">
      <img src="https://img.shields.io/badge/Frontend%20Developer-61DAFB?style=for-the-badge&logoColor=black"/>
    </td>
    <td align="center">
      <img src="https://img.shields.io/badge/Frontend%20Developer-61DAFB?style=for-the-badge&logoColor=black"/>
    </td>
  </tr>
  <tr>
    <td align="center"><b>박지원</b><br/><sub>p01046108755@gmail.com</sub></td>
    <td align="center"><b>성경훈</b><br/><sub>skh3268793@gmail.com</sub></td>
    <td align="center"><b>신재혁</b><br/><sub>tlswogur3210@gmail.com</sub></td>
  </tr>
</table>

<br>

<a name="tech-stack"></a>

# ⚙️ 기술 스택

### Backend

<div>
  <img src="https://img.shields.io/badge/Java%2021-007396?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Boot%203.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"/>
  <img src="https://img.shields.io/badge/OAuth2%20(Kakao·Naver·Google)-EB5424?style=for-the-badge&logo=auth0&logoColor=white"/>
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white"/>
  <img src="https://img.shields.io/badge/JPA%20%2F%20QueryDSL-59666C?style=for-the-badge&logo=hibernate&logoColor=white"/>
  <img src="https://img.shields.io/badge/MyBatis-2C4F7C?style=for-the-badge&logoColor=white"/>
  <img src="https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white"/>
  <img src="https://img.shields.io/badge/Quartz-FF6F00?style=for-the-badge&logoColor=white"/>
  <img src="https://img.shields.io/badge/Jsoup-1F8ACB?style=for-the-badge&logoColor=white"/>
  <img src="https://img.shields.io/badge/OpenPDF-EC1C24?style=for-the-badge&logoColor=white"/>
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"/>
</div>

### AI Server

<div>
  <img src="https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white"/>
  <img src="https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white"/>
  <img src="https://img.shields.io/badge/faster--whisper-412991?style=for-the-badge&logo=openai&logoColor=white"/>
  <img src="https://img.shields.io/badge/Qwen3%20(QLoRA)-FF6A00?style=for-the-badge&logoColor=white"/>
  <img src="https://img.shields.io/badge/Claude%20Sonnet%204-D97757?style=for-the-badge&logo=anthropic&logoColor=white"/>
  <img src="https://img.shields.io/badge/GPT--4o--mini-412991?style=for-the-badge&logo=openai&logoColor=white"/>
  <img src="https://img.shields.io/badge/llama--cpp-000000?style=for-the-badge&logoColor=white"/>
</div>

### Frontend

<div>
  <img src="https://img.shields.io/badge/React%2018-61DAFB?style=for-the-badge&logo=react&logoColor=black"/>
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white"/>
  <img src="https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white"/>
  <img src="https://img.shields.io/badge/Zustand-443E38?style=for-the-badge&logoColor=white"/>
  <img src="https://img.shields.io/badge/TanStack%20Query-FF4154?style=for-the-badge&logo=reactquery&logoColor=white"/>
  <img src="https://img.shields.io/badge/Tailwind%20CSS-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white"/>
  <img src="https://img.shields.io/badge/Recharts-22B5BF?style=for-the-badge&logoColor=white"/>
  <img src="https://img.shields.io/badge/Three.js-000000?style=for-the-badge&logo=threedotjs&logoColor=white"/>
  <img src="https://img.shields.io/badge/FullCalendar-4285F4?style=for-the-badge&logoColor=white"/>
  <img src="https://img.shields.io/badge/Framer%20Motion-0055FF?style=for-the-badge&logo=framer&logoColor=white"/>
  <img src="https://img.shields.io/badge/TensorFlow.js-FF6F00?style=for-the-badge&logo=tensorflow&logoColor=white"/>
</div>

### Mobile

<div>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>
  <img src="https://img.shields.io/badge/BLE%20(Nordic)-0082FC?style=for-the-badge&logo=bluetooth&logoColor=white"/>
  <img src="https://img.shields.io/badge/Room-4285F4?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/WorkManager-4285F4?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Retrofit-48B983?style=for-the-badge&logoColor=white"/>
  <img src="https://img.shields.io/badge/Coil-FF6F61?style=for-the-badge&logoColor=white"/>
  <img src="https://img.shields.io/badge/FCM-FFCA28?style=for-the-badge&logo=firebase&logoColor=black"/>
</div>

### 실시간 통신

<div>
  <img src="https://img.shields.io/badge/WebSocket%20(STOMP)-010101?style=for-the-badge&logo=socketdotio&logoColor=white"/>
  <img src="https://img.shields.io/badge/SSE-FF6C37?style=for-the-badge&logoColor=white"/>
  <img src="https://img.shields.io/badge/WebRTC-333333?style=for-the-badge&logo=webrtc&logoColor=white"/>
  <img src="https://img.shields.io/badge/mediasoup-353535?style=for-the-badge&logoColor=white"/>
</div>

### Database

<div>
  <img src="https://img.shields.io/badge/MySQL%208.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"/>
</div>

### Infra

<div>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/>
  <img src="https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white"/>
  <img src="https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"/>
  <img src="https://img.shields.io/badge/GitLab%20CI-FC6D26?style=for-the-badge&logo=gitlab&logoColor=white"/>
  <img src="https://img.shields.io/badge/Blue--Green-2496ED?style=for-the-badge&logoColor=white"/>
</div>

### Cooperation

<div>
  <a href="https://lab.ssafy.com/s14-webmobile1-sub1/S14P11D106"><img src="https://img.shields.io/badge/GitLab-FC6D26?style=for-the-badge&logo=gitlab&logoColor=white"/></a>
</div>

<br>
