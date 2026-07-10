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
- [💡 핵심 문제 해결](#core-problem)
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
| **1:1 실시간 메시지** | 실시간 채팅과 친구 접속 상태 표시. DM은 DB 저장 후 커밋 이후 Redis pub/sub으로 접속 중인 사용자에게 전달하고, 놓친 메시지는 DB 이력과 미읽음 기준으로 확인 |
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
| **무중단 배포** | 새 버전을 띄워 정상 확인 후 Nginx 트래픽을 전환하고, 문제 시 이전 버전으로 되돌릴 수 있도록 Blue-Green 배포 구성 |
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
- 📱 **웹 + 앱 연속성** — PC에서는 웹으로, 오프라인에서는 BLE 출석·녹음까지 모바일로 이어서 사용

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

- **Blue-Green 배포**로 새 버전을 띄워 헬스체크 후 **Nginx 트래픽을 전환**하고, 문제 시 이전 버전으로 롤백 — HTTP 요청은 graceful shutdown으로 마무리하고, WebSocket·SFU 장기 연결은 재접속 전제로 처리

<br>

<a name="core-problem"></a>

# 💡 핵심 문제 해결

### ① 무중단 배포 — 멀티스테이지 + Blue-Green + graceful shutdown

- **문제** — 화상 회의 서비스라 배포 중 잠깐이라도 끊기면 진행 중인 회의가 중단됨
- **원인** — 배포 시 인스턴스 교체 순간의 다운타임
- **대안** — ① 단순 재시작 — 다운타임 발생 ② 롤링 — 교체 중 구·신 버전 공존 ③ **Blue-Green** — 환경 전환·즉시 롤백
- **선택 이유** — 회의 중 끊김을 최소화하고 문제 시 트래픽만 되돌려 즉시 롤백하기 위해 Blue-Green 채택
- **해결** — 멀티스테이지 빌드로 런타임 이미지를 JRE-alpine만 담아 경량화하고, **Blue-Green**으로 비활성 환경에 새 버전을 배포 → 헬스체크(backend·sfu·ai·nginx) 통과 시 nginx `upstream.conf` 전환(`nginx -s reload`) → 실패 시 즉시 롤백. 종료되는 기존 인스턴스는 `server.shutdown=graceful`로 진행 중 HTTP 요청을 마친 뒤 종료
- **결과** — 배포 중에도 기존 버전이 요청을 받아 HTTP 요청 기준 무중단(WebSocket·SFU 장기 연결은 전환 시 재접속)

```dockerfile
# ① 멀티스테이지 — 런타임은 JRE-alpine 만 담아 이미지 경량화
FROM eclipse-temurin:21-jre-alpine
COPY --from=build .../*.jar app.jar
```
```bash
# ② Blue-Green 무중단 배포 — deploy-blue-green.sh
deploy_inactive                         # 비활성(blue|green)에 새 버전 배포
health_check_all "$inactive" || exit 1  # backend·sfu·ai·nginx 헬스체크 (실패=중단)
switch_traffic "$inactive"              # upstream.conf 재작성 + nginx -s reload
rollback: switch_traffic "$active"      # 실패 시 트래픽만 되돌려 즉시 롤백
```
```properties
# 종료 시 진행 중 HTTP 요청을 마친 뒤 종료 (WebSocket은 재접속)
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
```

### ② 다중 서버 실시간 DM — Redis pub/sub + afterCommit

- **문제** — 서버를 여러 대로 늘리면 발신자·수신자가 다른 인스턴스에 붙어 실시간 메시지가 전달되지 않고, 커밋 전에 발행하면 롤백 시 저장되지 않은 메시지가 푸시됨
- **원인** — WebSocket 세션은 특정 인스턴스에만 존재
- **대안** — ① 단일 서버 고정 — 확장 불가 ② 스티키 세션 — 불균형·장애 취약 ③ **Redis pub/sub 중계** + 발행 시점은 커밋 전 대신 **afterCommit**
- **선택 이유** — 수평 확장을 위해 pub/sub, 롤백 유령 메시지 방지를 위해 커밋 후 발행. 원본이 DB라 실시간을 놓쳐도 복구 가능
- **해결** — DM을 DB에 저장하고 `@TransactionalEventListener(AFTER_COMMIT)`로 **커밋 확정 후에만** 수신자 채널로 Redis publish → 모든 인스턴스가 구독하다 자신이 가진 세션으로만 전달. 놓친 푸시는 미읽음(`id > lastReadId`)으로 남아 대화 열람 시 DB 이력에서 복구
- **결과** — 서버 대수와 무관하게 전달, 롤백 시 유령 메시지 차단

```java
// ① DM을 DB에 저장하고, 커밋 후 보낼 이벤트를 등록
@Transactional
public DmResponse sendMessage(...) {
    directMessageMapper.insert(message);
    eventPublisher.publishEvent(new DmMessageSentEvent(receiverId, event));
}
// ② 커밋 확정 후에만 Redis publish — 롤백 시 '미저장 메시지 푸시' 방지
@TransactionalEventListener(phase = AFTER_COMMIT)
void onSent(DmMessageSentEvent e) {
    redisPublisher.publishToUser(e.receiverId(), e.payload());
}
// ③ 놓친 푸시는 미읽음(id > lastReadId)으로 남고, 대화 열람 시 DB로 복구
```

### ③ 스키마 변경 관리 — Flyway + JPA validate

- **문제** — 팀원마다 스키마가 갈라지고, `ddl-auto=update`는 어떤 DDL이 실행될지 예측·이력 관리가 어려움
- **대안** — ① `ddl-auto=update` — 자동이지만 실행될 DDL 예측·이력 불가 ② **Flyway 버전 SQL** — 명시·검증·이력화
- **선택 이유** — 운영 스키마는 무엇이 언제 어떻게 바뀌는지 추적돼야 안전 → JPA는 `validate`만, 변경은 Flyway로 관리
- **해결** — JPA는 `validate`만 수행하고 스키마는 **Flyway 버전 SQL**로 관리(`validate-on-migrate`로 적용된 파일의 무단 변경 감지). 컬럼 타입 변경 등은 마이그레이션(V14)으로 명시
- **결과** — 모든 환경이 동일한 스키마와 변경 이력을 공유

```properties
# JPA는 검증만, 스키마는 Flyway가 버전 관리 (ddl-auto 충돌 방지)
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.flyway.validate-on-migrate=true
```
```sql
-- V14: 컬럼 타입 변경을 마이그레이션으로 명시 (JPA가 못 잡던 변경)
ALTER TABLE contribution_detail MODIFY COLUMN reference_name VARCHAR(500) NOT NULL;
```

### ④ 여러 명 동시 개발 안정성 — 계층별 테스트 + MR 게이트

- **문제** — 여러 명이 같은 코드를 동시에 수정하면 변경 충돌·회귀가 머지 이후에야 드러남
- **대안** — ① 코드 리뷰만 ② 주요 경로 수동 점검 ③ **MR마다 전체 테스트 자동 실행**
- **선택 이유** — 사람 점검은 누락이 생기므로, 회귀를 머지 전에 기계적으로 차단하려면 자동 게이트가 확실
- **해결** — `service`=Mockito 단위 / `controller`=@WebMvcTest / `repository`=@SpringBootTest로 계층을 분리해 테스트(93개 클래스)하고, MR마다 전체 테스트를 자동 실행해 통과해야만 머지
- **결과** — 회귀가 공유 브랜치(dev)에 들어오기 전에 차단

```java
// 계층 분리 — service=Mockito 단위 · controller=@WebMvcTest · repository=@SpringBootTest
@ExtendWith(MockitoExtension.class)
@InjectMocks MeetingSummaryService service;   // 단위 예
assertThatThrownBy(() -> service.create(1L)).isInstanceOf(SttNotFoundException.class);
```
```yaml
# .gitlab-ci.yml — MR마다 전체 테스트(계층별 93 클래스) 실행, 통과해야만 머지
test:
  script: ./gradlew test
  rules: [ if: $CI_PIPELINE_SOURCE == "merge_request_event" ]
```

### ⑤ 미팅 AI 요약 — 2단계 요약으로 비용·전송 절감

- **문제** — 긴 회의 원문을 그대로 외부 LLM(Claude)에 전달하면 토큰 비용·전송량이 크고 원문이 외부로 노출됨
- **대안** — ① 원문 전체를 Claude로 — 비용·전송·민감정보 노출↑ ② 로컬 모델만 — 품질↓ ③ **로컬 초벌 + Claude 정제**
- **선택 이유** — 비용·전송량·정보 노출을 줄이면서 요약 품질을 유지하는 절충
- **해결** — 로컬 경량 LLM(Qwen3-8B, 4bit 양자화)으로 온프레미스에서 초벌 요약·압축한 뒤, 압축본만 Claude로 보완(실패 시 로컬 결과로 폴백)
- **결과** — 외부 전송 범위와 입력 토큰을 약 60% 절감

```python
# 1) 로컬 경량 LLM(Qwen3-8B, 4bit 양자화) — 온프레미스 초벌 요약·압축
local_summary = local_llm(transcript)["choices"][0]["text"]
# 2) 압축된 요약본만 Claude로 보완 (원문 미전송 → 전송 범위↓·토큰 ~60%↓)
resp = call_claude(f"다음 회의 분석을 검토·보완: {local_summary}")
final = parse(resp).get("summary") or local_summary   # 실패 시 로컬 결과 폴백
```

### ⑥ AI 커리큘럼 실시간 생성 — SSE 스트리밍

- 주차별 커리큘럼을 LLM이 다 만들 때까지 기다리면 화면이 멈춘 것처럼 느껴져, **SSE 토큰 스트리밍**으로 생성되는 즉시 전송해 체감 대기 시간을 줄임

```python
@app.post("/api/recommend-template/stream")        # SSE 토큰 스트리밍
async def recommend_stream(req):
    async def gen():
        for chunk in recommend_llm(prompt, stream=True):        # 토큰 단위
            yield f"event: token\ndata: {json.dumps(chunk)}\n\n"  # 즉시 전송
    return StreamingResponse(gen(), media_type="text/event-stream")
```

### ⑦ 발화 단위 STT 누적 저장

- 회의 종료 후 전체 녹음을 다시 STT하면 대기 시간이 회의 길이만큼 늘어나므로, 회의 중 실시간 STT 결과를 **발화 단위로 누적 저장**하고 종료 시 재STT 없이 누적 transcript로 곧바로 요약해 대기 시간을 단축

```java
// 실시간 STT 결과가 있으면 종료 후 재STT를 건너뛴다 (대기 시간 단축)
List<String> lines = speechSegmentService.getTranscriptByMeetingId(id); // 발화 단위 누적
String transcript = String.join("\n", lines);
String jobId = aiService.summarizeTranscriptAsync(transcript, speakerIds, true);
```

### ⑧ 경량 모델 파인튜닝 시도

- 회의 요약용 경량 모델을 도메인에 맞추기 위해 데이터를 직접 합성(유튜브 자막·GitHub 이슈 수집 + AI 생성)해 **QLoRA**로 파인튜닝을 시도했으나, 데이터 양·품질 한계로 원본 대비 개선이 뚜렷하지 않아 **원본 모델을 채택**

```python
# 도메인 데이터가 없어 직접 합성 (유튜브 자막·GitHub 이슈 + AI 생성)
data = collect_subtitles() + collect_github_issues() + ai_generate_meetings()
# Qwen3을 4bit 로드 + QLoRA 어댑터로 파인튜닝
model = FastLanguageModel.get_peft_model(model, r=8, target_modules=[...])
SFTTrainer(model, train_dataset=data, args=TrainingArguments(...)).train()
# → 검증 결과 개선이 뚜렷하지 않아 운영엔 원본 채택 (무리한 적용 대신 검증 후 판단)
```

### ⑨ 블루투스 자동 출석 (오프라인)

- 오프라인 스터디 출석을 위해 멤버 앱이 주변 **iBeacon**을 스캔해 자기 스터디·세션 신호일 때만 출석을 호출하고, 서버가 시간·권한·중복을 검증

```kotlin
// 멤버 앱: 주변 iBeacon 스캔 → 내 세션이면 출석 호출
override fun onScanResult(type: Int, result: ScanResult) {
    val b = parseIBeacon(result) ?: return
    if (b.uuid != SQUIZ_UUID) return
    if (b.major == studyId && b.minor == sessionId)   // 내 스터디·세션만
        attendanceApi.checkIn(studyId, sessionId)      // 서버가 시간·권한·중복 검증
}
```

<br>

<a name="erd"></a>

# 🗂️ ERD

> 회원·인증 · 스터디 · 세션·출석 · 미팅 AI · 퀴즈 · 복습 · 게이미피케이션 · 채팅 · 알림·일정 · AI 추천

<div align="center">
  <img src="ERD/SQUIZ_ERD.png" width="900" alt="SQUIZ ERD"/>
  <br/>
  <a href="https://www.erdcloud.com/d/XPFCDQve26qSEvr6A">🔗 ERDCloud에서 전체 보기</a>
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
