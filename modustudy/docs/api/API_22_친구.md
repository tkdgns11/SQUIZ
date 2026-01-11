# 친구 API (Friend)

## 기본 정보
- Base URL: `/api/v1/friends`
- 인증: JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/` | 친구 목록 조회 | O |
| GET | `/search` | 사용자 검색 | O |
| POST | `/request` | 친구 요청 보내기 | O |
| GET | `/requests/received` | 받은 친구 요청 목록 | O |
| GET | `/requests/sent` | 보낸 친구 요청 목록 | O |
| PUT | `/requests/{requestId}/accept` | 친구 요청 수락 | O |
| PUT | `/requests/{requestId}/reject` | 친구 요청 거절 | O |
| DELETE | `/{friendId}` | 친구 삭제 | O |
| POST | `/block/{userId}` | 사용자 차단 | O |
| DELETE | `/block/{userId}` | 차단 해제 | O |
| GET | `/block` | 차단 목록 조회 | O |

---

## API 상세

### 1. 친구 목록 조회

**Request**
```
GET /api/v1/friends?status=online
Authorization: Bearer {accessToken}
```

| Parameter | Type | 필수 | 설명 |
|-----------|------|------|------|
| status | string | X | 필터: online/offline/all (기본: all) |

**Response**
```json
{
  "success": true,
  "data": {
    "online": [
      {
        "friendshipId": 1,
        "user": {
          "id": 2,
          "nickname": "김친구",
          "profileImage": "https://...",
          "isOnline": true,
          "lastSeenAt": null
        },
        "createdAt": "2025-01-01T00:00:00Z"
      }
    ],
    "offline": [
      {
        "friendshipId": 2,
        "user": {
          "id": 3,
          "nickname": "이친구",
          "profileImage": "https://...",
          "isOnline": false,
          "lastSeenAt": "2025-01-10T15:30:00Z"
        },
        "createdAt": "2025-01-05T00:00:00Z"
      }
    ],
    "totalCount": 2
  }
}
```

---

### 2. 사용자 검색

검색 허용 설정한 사용자만 검색 결과에 노출

**Request**
```
GET /api/v1/friends/search?keyword=김싸피
Authorization: Bearer {accessToken}
```

| Parameter | Type | 필수 | 설명 |
|-----------|------|------|------|
| keyword | string | O | 닉네임 검색어 (2자 이상) |

**Response**
```json
{
  "success": true,
  "data": [
    {
      "id": 5,
      "nickname": "김싸피",
      "profileImage": "https://...",
      "isOnline": true,
      "friendStatus": "NONE"
    },
    {
      "id": 6,
      "nickname": "김싸피2",
      "profileImage": "https://...",
      "isOnline": false,
      "friendStatus": "PENDING_SENT"
    }
  ]
}
```

| friendStatus | 설명 |
|--------------|------|
| NONE | 관계 없음 |
| PENDING_SENT | 내가 요청 보냄 |
| PENDING_RECEIVED | 상대가 요청 보냄 |
| FRIEND | 이미 친구 |
| BLOCKED | 내가 차단함 |
| BLOCKED_BY | 상대가 나를 차단함 (검색 결과에 미노출) |

---

### 3. 친구 요청 보내기

**Request**
```
POST /api/v1/friends/request
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "userId": 5
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "requestId": 1,
    "status": "PENDING",
    "createdAt": "2025-01-10T00:00:00Z"
  }
}
```

---

### 4. 받은 친구 요청 목록

**Request**
```
GET /api/v1/friends/requests/received
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": [
    {
      "requestId": 1,
      "requester": {
        "id": 5,
        "nickname": "김싸피",
        "profileImage": "https://...",
        "isOnline": true
      },
      "status": "PENDING",
      "createdAt": "2025-01-10T00:00:00Z"
    }
  ]
}
```

---

### 5. 보낸 친구 요청 목록

**Request**
```
GET /api/v1/friends/requests/sent
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": [
    {
      "requestId": 2,
      "addressee": {
        "id": 6,
        "nickname": "이싸피",
        "profileImage": "https://..."
      },
      "status": "PENDING",
      "createdAt": "2025-01-09T00:00:00Z"
    }
  ]
}
```

---

### 6. 친구 요청 수락

**Request**
```
PUT /api/v1/friends/requests/{requestId}/accept
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "친구 요청을 수락했습니다.",
  "data": {
    "friendshipId": 1
  }
}
```

---

### 7. 친구 요청 거절

**Request**
```
PUT /api/v1/friends/requests/{requestId}/reject
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "친구 요청을 거절했습니다."
}
```

---

### 8. 친구 삭제

**Request**
```
DELETE /api/v1/friends/{friendId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "친구가 삭제되었습니다."
}
```

---

### 9. 사용자 차단

차단된 사용자는 친구 요청, DM을 보낼 수 없음

**Request**
```
POST /api/v1/friends/block/{userId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "사용자를 차단했습니다."
}
```

---

### 10. 차단 해제

**Request**
```
DELETE /api/v1/friends/block/{userId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "차단을 해제했습니다."
}
```

---

### 11. 차단 목록 조회

**Request**
```
GET /api/v1/friends/block
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": [
    {
      "id": 10,
      "nickname": "차단유저",
      "profileImage": "https://...",
      "blockedAt": "2025-01-08T00:00:00Z"
    }
  ]
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| USER_NOT_FOUND | 사용자를 찾을 수 없음 |
| ALREADY_FRIEND | 이미 친구 관계 |
| ALREADY_REQUESTED | 이미 친구 요청을 보냄 |
| REQUEST_NOT_FOUND | 친구 요청을 찾을 수 없음 |
| CANNOT_REQUEST_SELF | 자기 자신에게 요청 불가 |
| USER_BLOCKED | 차단된 사용자 |
| BLOCKED_BY_USER | 상대방이 나를 차단함 |
| USER_NOT_SEARCHABLE | 검색 비허용 사용자 |
