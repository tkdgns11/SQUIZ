# 메시지 API (Message)

## 기본 정보
- Base URL: `/api/v1/studies/{studyId}/channels/{channelId}/messages`
- 인증: JWT 필요
- 실시간: WebSocket 연동

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/` | 메시지 목록 조회 | O |
| POST | `/` | 메시지 전송 | O |
| POST | `/file` | 파일/이미지 전송 | O |
| DELETE | `/{messageId}` | 메시지 삭제 | O |
| POST | `/read` | 읽음 처리 | O |

---

## API 상세

### 1. 메시지 목록 조회

**Request**
```
GET /api/v1/studies/{studyId}/channels/{channelId}/messages?cursor=100&limit=50
Authorization: Bearer {accessToken}
```

| Parameter | Type | 필수 | 설명 |
|-----------|------|------|------|
| cursor | long | X | 마지막 메시지 ID (페이징) |
| limit | int | X | 조회 개수 (기본: 50, 최대: 100) |

**Response**
```json
{
  "success": true,
  "data": {
    "messages": [
      {
        "id": 101,
        "user": {
          "id": 1,
          "nickname": "홍길동",
          "profileImage": "https://..."
        },
        "content": "안녕하세요!",
        "messageType": "TEXT",
        "fileUrl": null,
        "createdAt": "2025-01-10T12:00:00Z",
        "isDeleted": false
      },
      {
        "id": 102,
        "user": {
          "id": 2,
          "nickname": "김싸피",
          "profileImage": "https://..."
        },
        "content": "코드 공유합니다",
        "messageType": "IMAGE",
        "fileUrl": "https://storage.../images/code.png",
        "createdAt": "2025-01-10T12:01:00Z",
        "isDeleted": false
      }
    ],
    "hasMore": true,
    "nextCursor": 100
  }
}
```

---

### 2. 메시지 전송

**Request**
```
POST /api/v1/studies/{studyId}/channels/{channelId}/messages
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "content": "안녕하세요!"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 103,
    "content": "안녕하세요!",
    "messageType": "TEXT",
    "createdAt": "2025-01-10T12:05:00Z"
  }
}
```

---

### 3. 파일/이미지 전송

**Request**
```
POST /api/v1/studies/{studyId}/channels/{channelId}/messages/file
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

| Field | Type | 필수 | 설명 |
|-------|------|------|------|
| file | File | O | 파일 (이미지/문서, max 10MB) |
| content | string | X | 메시지 내용 |

**Response**
```json
{
  "success": true,
  "data": {
    "id": 104,
    "content": "파일 공유합니다",
    "messageType": "FILE",
    "fileUrl": "https://storage.../files/document.pdf",
    "fileName": "document.pdf",
    "fileSize": 1024000,
    "createdAt": "2025-01-10T12:06:00Z"
  }
}
```

---

### 4. 메시지 삭제

**Request**
```
DELETE /api/v1/studies/{studyId}/channels/{channelId}/messages/{messageId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "메시지가 삭제되었습니다."
}
```

---

### 5. 읽음 처리

**Request**
```
POST /api/v1/studies/{studyId}/channels/{channelId}/messages/read
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "lastReadMessageId": 104
}
```

**Response**
```json
{
  "success": true
}
```

---

## WebSocket 이벤트

### 연결
```
CONNECT /ws/chat
Authorization: Bearer {accessToken}
```

### 구독 (채널 입장)
```
SUBSCRIBE /topic/studies/{studyId}/channels/{channelId}
```

### 메시지 수신
```json
{
  "type": "MESSAGE",
  "data": {
    "id": 105,
    "user": {
      "id": 1,
      "nickname": "홍길동",
      "profileImage": "https://..."
    },
    "content": "새 메시지입니다",
    "messageType": "TEXT",
    "createdAt": "2025-01-10T12:10:00Z"
  }
}
```

### 메시지 전송
```
SEND /app/studies/{studyId}/channels/{channelId}/messages
```
```json
{
  "content": "메시지 내용"
}
```

### 타이핑 표시
```
SEND /app/studies/{studyId}/channels/{channelId}/typing
```

### 타이핑 수신
```json
{
  "type": "TYPING",
  "data": {
    "userId": 1,
    "nickname": "홍길동"
  }
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| CHANNEL_NOT_FOUND | 채널을 찾을 수 없음 |
| MESSAGE_NOT_FOUND | 메시지를 찾을 수 없음 |
| NOT_MESSAGE_OWNER | 본인 메시지만 삭제 가능 |
| FILE_SIZE_EXCEEDED | 파일 크기 초과 |
| INVALID_FILE_TYPE | 지원하지 않는 파일 형식 |
