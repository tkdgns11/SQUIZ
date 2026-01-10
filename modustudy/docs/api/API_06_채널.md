# 채널 API (Channel)

## 기본 정보
- Base URL: `/api/v1/studies/{studyId}/channels`
- 인증: JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/` | 채널 목록 조회 | O |
| GET | `/{channelId}` | 채널 상세 조회 | O |
| POST | `/` | 채널 생성 (스터디장) | O |
| PUT | `/{channelId}` | 채널 수정 (스터디장) | O |
| DELETE | `/{channelId}` | 채널 삭제 (스터디장) | O |

---

## API 상세

### 1. 채널 목록 조회

**Request**
```
GET /api/v1/studies/{studyId}/channels
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "textChannels": [
      {
        "id": 1,
        "name": "일반",
        "type": "TEXT",
        "description": "일반 대화 채널",
        "isDefault": true,
        "sortOrder": 0,
        "unreadCount": 5,
        "lastMessage": {
          "content": "안녕하세요!",
          "createdAt": "2025-01-10T12:00:00Z"
        }
      },
      {
        "id": 2,
        "name": "공지사항",
        "type": "TEXT",
        "description": "공지 채널",
        "isDefault": false,
        "sortOrder": 1,
        "unreadCount": 0,
        "lastMessage": null
      }
    ],
    "voiceChannels": [
      {
        "id": 3,
        "name": "스터디룸",
        "type": "VOICE",
        "description": "음성 채널",
        "isDefault": true,
        "sortOrder": 0,
        "activeParticipants": 0
      }
    ]
  }
}
```

---

### 2. 채널 상세 조회

**Request**
```
GET /api/v1/studies/{studyId}/channels/{channelId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "일반",
    "type": "TEXT",
    "description": "일반 대화 채널",
    "isDefault": true,
    "sortOrder": 0,
    "createdAt": "2025-01-01T00:00:00Z"
  }
}
```

---

### 3. 채널 생성 (스터디장)

**Request**
```
POST /api/v1/studies/{studyId}/channels
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "name": "코드리뷰",
  "type": "TEXT",
  "description": "코드 리뷰 전용 채널"
}
```

| Field | Type | 필수 | 설명 |
|-------|------|------|------|
| name | string | O | 채널명 (최대 50자) |
| type | string | O | TEXT / VOICE |
| description | string | X | 채널 설명 (최대 200자) |

**Response**
```json
{
  "success": true,
  "data": {
    "id": 4,
    "name": "코드리뷰",
    "type": "TEXT"
  }
}
```

---

### 4. 채널 수정 (스터디장)

**Request**
```
PUT /api/v1/studies/{studyId}/channels/{channelId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "name": "코드리뷰 (수정)",
  "description": "코드 리뷰 및 피드백"
}
```

**Response**
```json
{
  "success": true,
  "message": "채널이 수정되었습니다."
}
```

---

### 5. 채널 삭제 (스터디장)

**Request**
```
DELETE /api/v1/studies/{studyId}/channels/{channelId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "채널이 삭제되었습니다.",
  "data": {
    "warning": "채널 내 메시지가 모두 삭제됩니다."
  }
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| CHANNEL_NOT_FOUND | 채널을 찾을 수 없음 |
| CANNOT_DELETE_DEFAULT | 기본 채널은 삭제 불가 |
| DUPLICATE_CHANNEL_NAME | 중복된 채널명 |
| MAX_CHANNELS_EXCEEDED | 최대 채널 수 초과 |
