# 회고 API (Retrospective)

## 기본 정보
- Base URL: `/api/v1/studies/{studyId}/retrospectives`
- 인증: JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/` | 회고 목록 조회 | O |
| GET | `/{retroId}` | 회고 상세 조회 | O |
| POST | `/` | 회고 생성 (스터디장) | O |
| DELETE | `/{retroId}` | 회고 삭제 (스터디장) | O |
| POST | `/{retroId}/items` | 회고 항목 작성 | O |
| PUT | `/{retroId}/items/{itemId}` | 회고 항목 수정 | O |
| DELETE | `/{retroId}/items/{itemId}` | 회고 항목 삭제 | O |
| GET | `/{retroId}/analysis` | AI 회고 분석 | O |

---

## API 상세

### 1. 회고 목록 조회

**Request**
```
GET /api/v1/studies/{studyId}/retrospectives?page=0&size=20
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "1회차 회고",
        "retrospectiveType": "KPT",
        "session": {
          "id": 1,
          "sessionNumber": 1
        },
        "itemCount": 15,
        "participantCount": 5,
        "hasMyItem": true,
        "createdAt": "2025-01-16T00:00:00Z"
      }
    ],
    "page": 0,
    "totalElements": 3
  }
}
```

---

### 2. 회고 상세 조회

**Request**
```
GET /api/v1/studies/{studyId}/retrospectives/{retroId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "1회차 회고",
    "retrospectiveType": "KPT",
    "session": {
      "id": 1,
      "sessionNumber": 1,
      "title": "OT 및 환경설정"
    },
    "items": {
      "KEEP": [
        {
          "id": 1,
          "user": {
            "id": 1,
            "nickname": "홍길동",
            "profileImage": "https://..."
          },
          "content": "시간 약속을 잘 지켰다",
          "createdAt": "2025-01-16T10:00:00Z"
        },
        {
          "id": 2,
          "user": {
            "id": 2,
            "nickname": "김싸피"
          },
          "content": "자료 공유가 활발했다",
          "createdAt": "2025-01-16T10:05:00Z"
        }
      ],
      "PROBLEM": [
        {
          "id": 3,
          "user": {
            "id": 1,
            "nickname": "홍길동"
          },
          "content": "회의 시간이 너무 길었다",
          "createdAt": "2025-01-16T10:10:00Z"
        }
      ],
      "TRY": [
        {
          "id": 4,
          "user": {
            "id": 2,
            "nickname": "김싸피"
          },
          "content": "다음에는 타임키퍼를 정하자",
          "createdAt": "2025-01-16T10:15:00Z"
        }
      ]
    },
    "createdAt": "2025-01-16T00:00:00Z"
  }
}
```

---

### 3. 회고 생성 (스터디장)

**Request**
```
POST /api/v1/studies/{studyId}/retrospectives
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "title": "2회차 회고",
  "retrospectiveType": "KPT",
  "sessionId": 2
}
```

| Field | Type | 필수 | 설명 |
|-------|------|------|------|
| title | string | O | 회고 제목 |
| retrospectiveType | string | O | KPT / FREE |
| sessionId | long | X | 연결할 세션 ID |

**Response**
```json
{
  "success": true,
  "data": {
    "id": 2,
    "title": "2회차 회고",
    "retrospectiveType": "KPT"
  }
}
```

---

### 4. 회고 삭제 (스터디장)

**Request**
```
DELETE /api/v1/studies/{studyId}/retrospectives/{retroId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "회고가 삭제되었습니다."
}
```

---

### 5. 회고 항목 작성

**Request**
```
POST /api/v1/studies/{studyId}/retrospectives/{retroId}/items
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "category": "KEEP",
  "content": "코드 리뷰 시간이 유익했다"
}
```

| category | 설명 |
|----------|------|
| KEEP | 잘한 점, 유지할 점 |
| PROBLEM | 문제점, 개선할 점 |
| TRY | 시도할 점, 액션 아이템 |

**Response**
```json
{
  "success": true,
  "data": {
    "id": 5,
    "category": "KEEP",
    "content": "코드 리뷰 시간이 유익했다",
    "createdAt": "2025-01-17T10:00:00Z"
  }
}
```

---

### 6. 회고 항목 수정

**Request**
```
PUT /api/v1/studies/{studyId}/retrospectives/{retroId}/items/{itemId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "content": "코드 리뷰 시간이 매우 유익했다 (수정)"
}
```

**Response**
```json
{
  "success": true,
  "message": "회고 항목이 수정되었습니다."
}
```

---

### 7. 회고 항목 삭제

**Request**
```
DELETE /api/v1/studies/{studyId}/retrospectives/{retroId}/items/{itemId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "회고 항목이 삭제되었습니다."
}
```

---

### 8. AI 회고 분석

**Request**
```
GET /api/v1/studies/{studyId}/retrospectives/{retroId}/analysis
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "analysis": {
      "summary": "이번 회고에서는 시간 관리와 자료 공유에 대한 긍정적인 피드백이 많았습니다.",
      "patterns": [
        {
          "type": "RECURRING_PROBLEM",
          "description": "시간 관리 문제가 3회 연속 언급되었습니다.",
          "frequency": 3
        }
      ],
      "recommendations": [
        "타임키퍼 역할을 지정하여 회의 시간을 관리하세요.",
        "회의 전 아젠다를 공유하면 효율적인 진행이 가능합니다."
      ],
      "sentiment": {
        "positive": 60,
        "neutral": 25,
        "negative": 15
      }
    },
    "generatedAt": "2025-01-17T12:00:00Z"
  }
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| RETROSPECTIVE_NOT_FOUND | 회고를 찾을 수 없음 |
| ITEM_NOT_FOUND | 회고 항목을 찾을 수 없음 |
| NOT_ITEM_OWNER | 본인 항목만 수정/삭제 가능 |
| INVALID_CATEGORY | 유효하지 않은 카테고리 |
