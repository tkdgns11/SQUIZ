# 데일리 API (Daily Report)

## 기본 정보
- Base URL: `/api/v1/studies/{studyId}/daily`
- 인증: JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/today` | 오늘의 데일리 조회 | O |
| GET | `/history` | 데일리 히스토리 | O |
| GET | `/{dailyId}` | 데일리 상세 조회 | O |
| POST | `/today/items` | 데일리 항목 작성 | O |
| PUT | `/today/items/{itemId}` | 데일리 항목 수정 | O |
| DELETE | `/today/items/{itemId}` | 데일리 항목 삭제 | O |

---

## API 상세

### 1. 오늘의 데일리 조회

**Request**
```
GET /api/v1/studies/{studyId}/daily/today
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 10,
    "reportDate": "2025-01-17",
    "summary": "팀원들이 알고리즘 문제 풀이에 집중하고 있습니다. 김싸피님이 DP 관련 블로커가 있습니다.",
    "items": {
      "YESTERDAY": [
        {
          "id": 1,
          "user": {
            "id": 1,
            "nickname": "홍길동",
            "profileImage": "https://..."
          },
          "content": "백준 1000번 문제 풀이 완료",
          "createdAt": "2025-01-17T09:00:00Z"
        },
        {
          "id": 2,
          "user": {
            "id": 2,
            "nickname": "김싸피"
          },
          "content": "DP 개념 정리 문서 작성",
          "createdAt": "2025-01-17T09:30:00Z"
        }
      ],
      "TODAY": [
        {
          "id": 3,
          "user": {
            "id": 1,
            "nickname": "홍길동"
          },
          "content": "백준 1001번 문제 풀이 예정",
          "createdAt": "2025-01-17T09:00:00Z"
        }
      ],
      "BLOCKER": [
        {
          "id": 4,
          "user": {
            "id": 2,
            "nickname": "김싸피"
          },
          "content": "DP 점화식 이해가 어려움",
          "createdAt": "2025-01-17T09:30:00Z"
        }
      ]
    },
    "myItems": {
      "hasYesterday": true,
      "hasToday": true,
      "hasBlocker": false
    },
    "participantCount": 4,
    "totalMembers": 6
  }
}
```

---

### 2. 데일리 히스토리

**Request**
```
GET /api/v1/studies/{studyId}/daily/history?page=0&size=20
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 10,
        "reportDate": "2025-01-17",
        "participantCount": 4,
        "blockerCount": 1,
        "hasSummary": true
      },
      {
        "id": 9,
        "reportDate": "2025-01-16",
        "participantCount": 5,
        "blockerCount": 0,
        "hasSummary": true
      }
    ],
    "page": 0,
    "totalElements": 10
  }
}
```

---

### 3. 데일리 상세 조회

**Request**
```
GET /api/v1/studies/{studyId}/daily/{dailyId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 9,
    "reportDate": "2025-01-16",
    "summary": "팀 전체가 순조롭게 학습 진행 중입니다.",
    "items": {
      "YESTERDAY": [...],
      "TODAY": [...],
      "BLOCKER": []
    },
    "participantCount": 5
  }
}
```

---

### 4. 데일리 항목 작성

**Request**
```
POST /api/v1/studies/{studyId}/daily/today/items
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "category": "YESTERDAY",
  "content": "백준 1002번 문제 풀이 완료"
}
```

| category | 설명 |
|----------|------|
| YESTERDAY | 어제 한 일 |
| TODAY | 오늘 할 일 |
| BLOCKER | 막히는 점, 도움 필요 |

**Response**
```json
{
  "success": true,
  "data": {
    "id": 5,
    "category": "YESTERDAY",
    "content": "백준 1002번 문제 풀이 완료",
    "createdAt": "2025-01-17T10:00:00Z"
  }
}
```

---

### 5. 데일리 항목 수정

**Request**
```
PUT /api/v1/studies/{studyId}/daily/today/items/{itemId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "content": "백준 1002번, 1003번 문제 풀이 완료"
}
```

**Response**
```json
{
  "success": true,
  "message": "데일리 항목이 수정되었습니다."
}
```

---

### 6. 데일리 항목 삭제

**Request**
```
DELETE /api/v1/studies/{studyId}/daily/today/items/{itemId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "데일리 항목이 삭제되었습니다."
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| DAILY_NOT_FOUND | 데일리를 찾을 수 없음 |
| ITEM_NOT_FOUND | 데일리 항목을 찾을 수 없음 |
| NOT_ITEM_OWNER | 본인 항목만 수정/삭제 가능 |
| CANNOT_EDIT_PAST_DAILY | 과거 데일리는 수정 불가 |
