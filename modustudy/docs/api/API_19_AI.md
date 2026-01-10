# AI API

## 기본 정보
- Base URL: `/api/v1/ai`
- 인증: JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| POST | `/chat` | AI 챗봇 대화 | O |
| GET | `/chat/history` | 대화 히스토리 | O |
| POST | `/feedback` | AI 결과물 피드백 | O |

---

## API 상세

### 1. AI 챗봇 대화

**Request**
```
POST /api/v1/ai/chat
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "studyId": 1,
  "message": "DP 점화식 세우는 방법 알려줘",
  "conversationId": "conv_123"
}
```

| Field | Type | 필수 | 설명 |
|-------|------|------|------|
| studyId | long | X | 스터디 컨텍스트 (자료 기반 답변) |
| message | string | O | 사용자 메시지 |
| conversationId | string | X | 대화 ID (연속 대화 시) |

**Response**
```json
{
  "success": true,
  "data": {
    "conversationId": "conv_123",
    "message": "DP 점화식을 세우는 방법을 설명해드릴게요.\n\n1. **문제 분석**: 먼저 문제가 최적 부분 구조를 가지는지 확인합니다...",
    "references": [
      {
        "type": "MATERIAL",
        "id": 5,
        "title": "DP 개념 정리",
        "relevance": 0.92
      },
      {
        "type": "MEETING_TRANSCRIPT",
        "id": 3,
        "title": "2회차 스터디",
        "timestamp": "15:30",
        "relevance": 0.85
      }
    ],
    "suggestedQuestions": [
      "피보나치 수열의 점화식은 어떻게 세우나요?",
      "메모이제이션과 타뷸레이션의 차이점은?"
    ]
  }
}
```

---

### 2. 대화 히스토리

**Request**
```
GET /api/v1/ai/chat/history?studyId=1&page=0&size=20
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "conversations": [
      {
        "conversationId": "conv_123",
        "studyId": 1,
        "studyName": "알고리즘 스터디",
        "lastMessage": "DP 점화식 세우는 방법 알려줘",
        "messageCount": 5,
        "createdAt": "2025-01-17T10:00:00Z",
        "updatedAt": "2025-01-17T10:30:00Z"
      }
    ],
    "page": 0,
    "totalElements": 10
  }
}
```

---

### 3. AI 결과물 피드백

**Request**
```
POST /api/v1/ai/feedback
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "featureType": "MEETING_SUMMARY",
  "referenceId": 5,
  "feedback": "POSITIVE",
  "comment": "요약이 정확하고 도움이 됐습니다."
}
```

| featureType | 설명 |
|-------------|------|
| MEETING_SUMMARY | 미팅 요약 |
| ACTION_ITEM | 액션 아이템 |
| QUIZ_GENERATION | 퀴즈 생성 |
| CHAT_RESPONSE | 챗봇 응답 |
| STUDY_RECOMMENDATION | 스터디 추천 |
| RETROSPECTIVE_ANALYSIS | 회고 분석 |

| feedback | 설명 |
|----------|------|
| POSITIVE | 👍 좋아요 |
| NEGATIVE | 👎 별로에요 |

**Response**
```json
{
  "success": true,
  "message": "피드백이 등록되었습니다. 감사합니다!"
}
```

---

## RAG (Retrieval-Augmented Generation) 컨텍스트

AI 챗봇은 다음 데이터를 참조하여 답변합니다:

1. **스터디 자료** (Material)
   - 업로드된 문서, 링크

2. **미팅 기록** (Meeting Transcript)
   - STT로 기록된 미팅 내용
   - 미팅 요약

3. **커리큘럼** (Curriculum)
   - 주차별 학습 내용

4. **회고** (Retrospective)
   - KPT 항목들

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| AI_SERVICE_UNAVAILABLE | AI 서비스 일시 불가 |
| RATE_LIMIT_EXCEEDED | 요청 한도 초과 |
| INVALID_CONVERSATION | 유효하지 않은 대화 ID |
| STUDY_CONTEXT_REQUIRED | 스터디 컨텍스트 필요 |
