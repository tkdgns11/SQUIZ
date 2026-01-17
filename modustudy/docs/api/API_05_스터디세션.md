# 스터디 세션 API (Study Session)

## 기본 정보
- Base URL: `/api/v1/study/{studyId}/sessions`
- 인증: JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/` | 세션(회차) 목록 조회 | O |
| GET | `/{sessionId}` | 세션 상세 조회 | O |
| POST | `/` | 세션 생성 (스터디장) | O |
| PUT | `/{sessionId}` | 세션 수정 (스터디장) | O |
| DELETE | `/{sessionId}` | 세션 삭제 (스터디장) | O |
| PUT | `/{sessionId}/status` | 세션 상태 변경 (스터디장) | O |

---

## API 상세

### 1. 세션 목록 조회

**Request**
```
GET /api/v1/study/{studyId}/sessions
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "sessions": [
      {
        "id": 1,
        "sessionNumber": 1,
        "title": "OT 및 환경설정",
        "description": "스터디 소개 및 개발 환경 설정",
        "scheduledAt": "2025-01-15T19:00:00Z",
        "durationMinutes": 60,
        "location": "Discord",
        "isOnline": true,
        "status": "COMPLETED",
        "completedAt": "2025-01-15T20:10:00Z",
        "attendanceCount": 5,
        "totalMembers": 6
      },
      {
        "id": 2,
        "sessionNumber": 2,
        "title": "배열과 문자열",
        "description": "배열/문자열 관련 문제 풀이",
        "scheduledAt": "2025-01-22T19:00:00Z",
        "durationMinutes": 90,
        "location": "Discord",
        "isOnline": true,
        "status": "SCHEDULED",
        "completedAt": null,
        "attendanceCount": 0,
        "totalMembers": 6
      }
    ],
    "totalSessions": 8,
    "completedSessions": 1
  }
}
```

---

### 2. 세션 상세 조회

**Request**
```
GET /api/v1/study/{studyId}/sessions/{sessionId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "sessionNumber": 1,
    "title": "OT 및 환경설정",
    "description": "스터디 소개 및 개발 환경 설정",
    "scheduledAt": "2025-01-15T19:00:00Z",
    "durationMinutes": 60,
    "location": "Discord",
    "isOnline": true,
    "status": "COMPLETED",
    "completedAt": "2025-01-15T20:10:00Z",
    "attendance": [
      {
        "userId": 1,
        "nickname": "홍길동",
        "status": "PRESENT",
        "checkedAt": "2025-01-15T19:02:00Z"
      },
      {
        "userId": 2,
        "nickname": "김싸피",
        "status": "LATE",
        "checkedAt": "2025-01-15T19:15:00Z"
      }
    ],
    "meeting": {
      "id": 1,
      "hasSummary": true,
      "hasTranscript": true
    },
    "materials": [
      {
        "id": 1,
        "title": "OT 자료",
        "materialType": "FILE"
      }
    ]
  }
}
```

---

### 3. 세션 생성 (스터디장)

**Request**
```
POST /api/v1/study/{studyId}/sessions
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "sessionNumber": 3,
  "title": "스택과 큐",
  "description": "스택/큐 자료구조 및 관련 문제 풀이",
  "scheduledAt": "2025-01-29T19:00:00Z",
  "durationMinutes": 90,
  "location": "Discord",
  "isOnline": true
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 3,
    "sessionNumber": 3,
    "title": "스택과 큐",
    "scheduledAt": "2025-01-29T19:00:00Z"
  }
}
```

---

### 4. 세션 수정 (스터디장)

**Request**
```
PUT /api/v1/study/{studyId}/sessions/{sessionId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "title": "스택과 큐 (수정)",
  "scheduledAt": "2025-01-30T19:00:00Z",
  "location": "Zoom"
}
```

**Response**
```json
{
  "success": true,
  "message": "세션이 수정되었습니다."
}
```

---

### 5. 세션 삭제 (스터디장)

**Request**
```
DELETE /api/v1/study/{studyId}/sessions/{sessionId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "세션이 삭제되었습니다."
}
```

---

### 6. 세션 상태 변경 (스터디장)

**Request**
```
PUT /api/v1/study/{studyId}/sessions/{sessionId}/status
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "status": "IN_PROGRESS"
}
```

| status | 설명 |
|--------|------|
| SCHEDULED | 예정됨 |
| IN_PROGRESS | 진행중 |
| COMPLETED | 완료 |
| CANCELLED | 취소 |

**Response**
```json
{
  "success": true,
  "message": "세션 상태가 변경되었습니다."
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| SESSION_NOT_FOUND | 세션을 찾을 수 없음 |
| DUPLICATE_SESSION_NUMBER | 중복된 회차 번호 |
| INVALID_SESSION_STATUS | 유효하지 않은 상태 변경 |
| CANNOT_DELETE_COMPLETED | 완료된 세션은 삭제 불가 |
