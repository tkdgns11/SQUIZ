# DM API (Direct Message)

## 기본 정보
- Base URL: `/api/v1/dm`
- 인증: JWT 필요
- 실시간 메시지: WebSocket `/ws/dm`

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/conversations` | DM 대화 목록 조회 | O |
| GET | `/conversations/{conversationId}` | 대화 메시지 조회 | O |
| POST | `/conversations` | 새 대화 시작 | O |
| POST | `/conversations/{conversationId}/messages` | 메시지 전송 | O |
| PUT | `/conversations/{conversationId}/read` | 메시지 읽음 처리 | O |
| DELETE | `/conversations/{conversationId}` | 대화 삭제 (본인만) | O |
| DELETE | `/messages/{messageId}` | 메시지 삭제 | O |

---

## API 상세

### 1. DM 대화 목록 조회

**Request**
```
GET /api/v1/dm/conversations
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": [
    {
      "conversationId": "conv_123abc",
      "partner": {
        "id": 2,
        "nickname": "김친구",
        "profileImage": "https://...",
        "isOnline": true
      },
      "lastMessage": {
        "id": 100,
        "content": "내일 스터디에서 봐요!",
        "senderId": 2,
        "createdAt": "2025-01-10T15:30:00Z"
      },
      "unreadCount": 2,
      "updatedAt": "2025-01-10T15:30:00Z"
    },
    {
      "conversationId": "conv_456def",
      "partner": {
        "id": 3,
        "nickname": "이친구",
        "profileImage": "https://...",
        "isOnline": false,
        "lastSeenAt": "2025-01-10T12:00:00Z"
      },
      "lastMessage": {
        "id": 80,
        "content": "알겠습니다~",
        "senderId": 1,
        "createdAt": "2025-01-09T20:00:00Z"
      },
      "unreadCount": 0,
      "updatedAt": "2025-01-09T20:00:00Z"
    }
  ]
}
```

---

### 2. 대화 메시지 조회

**Request**
```
GET /api/v1/dm/conversations/{conversationId}?cursor=100&limit=50
Authorization: Bearer {accessToken}
```

| Parameter | Type | 필수 | 설명 |
|-----------|------|------|------|
| cursor | int | X | 마지막 메시지 ID (이전 메시지 조회) |
| limit | int | X | 조회 개수 (기본: 50, 최대: 100) |

**Response**
```json
{
  "success": true,
  "data": {
    "conversationId": "conv_123abc",
    "partner": {
      "id": 2,
      "nickname": "김친구",
      "profileImage": "https://...",
      "isOnline": true
    },
    "messages": [
      {
        "id": 99,
        "content": "오늘 스터디 어땠어요?",
        "senderId": 1,
        "isRead": true,
        "createdAt": "2025-01-10T15:00:00Z"
      },
      {
        "id": 100,
        "content": "내일 스터디에서 봐요!",
        "senderId": 2,
        "isRead": false,
        "createdAt": "2025-01-10T15:30:00Z"
      }
    ],
    "hasMore": true,
    "nextCursor": 98
  }
}
```

---

### 3. 새 대화 시작

친구에게만 DM 전송 가능

**Request**
```
POST /api/v1/dm/conversations
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "partnerId": 2,
  "message": "안녕하세요! 스터디 관련해서 질문이 있어요."
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "conversationId": "conv_789ghi",
    "messageId": 101,
    "createdAt": "2025-01-10T16:00:00Z"
  }
}
```

---

### 4. 메시지 전송

**Request**
```
POST /api/v1/dm/conversations/{conversationId}/messages
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "content": "네, 알겠습니다!"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 102,
    "content": "네, 알겠습니다!",
    "senderId": 1,
    "isRead": false,
    "createdAt": "2025-01-10T16:05:00Z"
  }
}
```

---

### 5. 메시지 읽음 처리

대화방 진입 시 호출

**Request**
```
PUT /api/v1/dm/conversations/{conversationId}/read
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "읽음 처리되었습니다.",
  "data": {
    "readCount": 3,
    "lastReadMessageId": 102
  }
}
```

---

### 6. 대화 삭제

본인 기준으로만 삭제 (상대방에게는 남아있음)

**Request**
```
DELETE /api/v1/dm/conversations/{conversationId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "대화가 삭제되었습니다."
}
```

---

### 7. 메시지 삭제

본인이 보낸 메시지만 삭제 가능

**Request**
```
DELETE /api/v1/dm/messages/{messageId}
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

## WebSocket 이벤트

### 연결
```
ws://api.modustudy.com/ws/dm?token={accessToken}
```

### 수신 이벤트

**새 메시지 수신**
```json
{
  "type": "NEW_MESSAGE",
  "data": {
    "conversationId": "conv_123abc",
    "message": {
      "id": 103,
      "content": "새 메시지입니다.",
      "senderId": 2,
      "createdAt": "2025-01-10T16:10:00Z"
    }
  }
}
```

**메시지 읽음**
```json
{
  "type": "MESSAGE_READ",
  "data": {
    "conversationId": "conv_123abc",
    "readerId": 2,
    "lastReadMessageId": 102
  }
}
```

**상대방 타이핑**
```json
{
  "type": "TYPING",
  "data": {
    "conversationId": "conv_123abc",
    "userId": 2,
    "isTyping": true
  }
}
```

### 발신 이벤트

**타이핑 상태 전송**
```json
{
  "type": "TYPING",
  "conversationId": "conv_123abc",
  "isTyping": true
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| CONVERSATION_NOT_FOUND | 대화를 찾을 수 없음 |
| MESSAGE_NOT_FOUND | 메시지를 찾을 수 없음 |
| NOT_FRIEND | 친구가 아닌 사용자에게 DM 불가 |
| USER_BLOCKED | 차단된 사용자 |
| BLOCKED_BY_USER | 상대방이 나를 차단함 |
| CANNOT_DELETE_OTHERS_MESSAGE | 다른 사람의 메시지 삭제 불가 |
