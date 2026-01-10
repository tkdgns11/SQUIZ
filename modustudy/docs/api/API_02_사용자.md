# 사용자 API (User)

## 기본 정보
- Base URL: `/api/v1/users`
- 인증: JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/me` | 내 정보 조회 | O |
| PUT | `/me` | 내 정보 수정 | O |
| POST | `/me/profile` | 추가 정보 입력 (최초 로그인) | O |
| POST | `/me/profile-image` | 프로필 이미지 업로드 | O |
| GET | `/me/schedule` | 내 가용 일정 조회 | O |
| PUT | `/me/schedule` | 내 가용 일정 수정 | O |
| GET | `/me/stats` | 내 활동 통계 조회 | O |
| GET | `/{userId}` | 특정 사용자 조회 | O |

---

## API 상세

### 1. 내 정보 조회

**Request**
```
GET /api/v1/users/me
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "hong@gmail.com",
    "name": "홍길동",
    "nickname": "길동이",
    "profileImage": "https://...",
    "linkedProviders": ["GOOGLE"],
    "role": "USER",
    "createdAt": "2025-01-01T00:00:00Z",
    "lastLoginAt": "2025-01-10T12:00:00Z"
  }
}
```

---

### 2. 내 정보 수정

**Request**
```
PUT /api/v1/users/me
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "nickname": "새닉네임"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "nickname": "새닉네임",
    "profileImage": "https://..."
  }
}
```

---

### 3. 추가 정보 입력 (최초 로그인)

**Request**
```
POST /api/v1/users/me/profile
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "nickname": "길동이"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "nickname": "길동이"
  }
}
```

---

### 4. 프로필 이미지 업로드

**Request**
```
POST /api/v1/users/me/profile-image
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```
| Field | Type | 설명 |
|-------|------|------|
| image | File | 이미지 파일 (jpg, png, max 5MB) |

**Response**
```json
{
  "success": true,
  "data": {
    "profileImage": "https://storage.../profile/1.jpg"
  }
}
```

---

### 5. 내 가용 일정 조회

**Request**
```
GET /api/v1/users/me/schedule
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "schedules": [
      {
        "id": 1,
        "dayOfWeek": "MON",
        "startTime": "18:00",
        "endTime": "22:00",
        "isAvailable": true
      },
      {
        "id": 2,
        "dayOfWeek": "WED",
        "startTime": "18:00",
        "endTime": "22:00",
        "isAvailable": true
      }
    ]
  }
}
```

---

### 6. 내 가용 일정 수정

**Request**
```
PUT /api/v1/users/me/schedule
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "schedules": [
    {
      "dayOfWeek": "MON",
      "startTime": "18:00",
      "endTime": "22:00",
      "isAvailable": true
    },
    {
      "dayOfWeek": "TUE",
      "startTime": "19:00",
      "endTime": "21:00",
      "isAvailable": true
    }
  ]
}
```

**Response**
```json
{
  "success": true,
  "message": "일정이 저장되었습니다."
}
```

---

### 7. 내 활동 통계 조회

**Request**
```
GET /api/v1/users/me/stats
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "level": 5,
    "levelName": "성실러",
    "totalActivityDays": 45,
    "currentStreak": 7,
    "maxStreak": 15,
    "lastActivityDate": "2025-01-10",
    "totalStudiesJoined": 3,
    "totalStudiesLed": 1,
    "totalChatCount": 150,
    "totalQuizCount": 12,
    "totalMaterialsUploaded": 8,
    "totalRetrospectives": 5
  }
}
```

---

### 8. 특정 사용자 조회

**Request**
```
GET /api/v1/users/{userId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 2,
    "nickname": "김싸피",
    "profileImage": "https://...",
    "level": 3,
    "levelName": "열공러"
  }
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| USER_NOT_FOUND | 사용자를 찾을 수 없음 |
| NICKNAME_DUPLICATED | 중복된 닉네임 |
| INVALID_IMAGE_FORMAT | 지원하지 않는 이미지 형식 |
| IMAGE_SIZE_EXCEEDED | 이미지 크기 초과 |
