# 스터디 댓글 API (Study Comment)

## 기본 정보
- Base URL: `/api/v1/studies/{studyId}/comments`
- 인증: JWT 필요 (조회 제외)
- 담당: D

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/` | 댓글 목록 조회 | X |
| POST | `/` | 댓글 작성 | O |
| PUT | `/{commentId}` | 댓글 수정 | O |
| DELETE | `/{commentId}` | 댓글 삭제 | O |
| POST | `/{commentId}/replies` | 대댓글 작성 | O |
| POST | `/images` | 댓글 이미지 업로드 | O |

---

## API 상세

### 1. 댓글 목록 조회

**Request**
```
GET /api/v1/studies/{studyId}/comments?page=0&size=20
```

| Parameter | Type | 필수 | 설명 |
|-----------|------|------|------|
| page | int | X | 페이지 번호 (기본: 0) |
| size | int | X | 페이지 크기 (기본: 20) |

**Response**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "content": "이 스터디 진행 방식이 궁금합니다!",
        "imageUrl": null,
        "author": {
          "id": 3,
          "nickname": "김싸피",
          "profileImage": "https://..."
        },
        "replies": [
          {
            "id": 2,
            "content": "매주 월/수/금 저녁에 진행해요!",
            "imageUrl": "https://s3.../comments/2.png",
            "author": {
              "id": 1,
              "nickname": "스터디장",
              "profileImage": "https://..."
            },
            "isLeader": true,
            "createdAt": "2025-01-10T10:30:00Z",
            "updatedAt": null
          }
        ],
        "replyCount": 1,
        "isLeader": false,
        "createdAt": "2025-01-10T10:00:00Z",
        "updatedAt": null
      },
      {
        "id": 3,
        "content": "지원하고 싶은데 아직 모집 중인가요?",
        "author": {
          "id": 5,
          "nickname": "이싸피",
          "profileImage": "https://..."
        },
        "replies": [],
        "replyCount": 0,
        "isLeader": false,
        "createdAt": "2025-01-11T09:00:00Z",
        "updatedAt": null
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

---

### 2. 댓글 작성

**Request**
```
POST /api/v1/studies/{studyId}/comments
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "content": "스터디 지원하고 싶습니다. 아직 자리 있나요?",
  "imageUrl": "https://s3.../comments/temp/abc123.png"
}
```

> `imageUrl`은 선택 필드. 이미지 업로드 API로 먼저 업로드 후 URL 전달.

**Response**
```json
{
  "success": true,
  "data": {
    "id": 4,
    "content": "스터디 지원하고 싶습니다. 아직 자리 있나요?",
    "imageUrl": "https://s3.../comments/4.png",
    "author": {
      "id": 6,
      "nickname": "박싸피",
      "profileImage": "https://..."
    },
    "isLeader": false,
    "createdAt": "2025-01-11T14:00:00Z"
  }
}
```

---

### 3. 댓글 수정

본인이 작성한 댓글만 수정 가능

**Request**
```
PUT /api/v1/studies/{studyId}/comments/{commentId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "content": "스터디 지원하고 싶습니다. 아직 자리 있나요? (수정)"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 4,
    "content": "스터디 지원하고 싶습니다. 아직 자리 있나요? (수정)",
    "updatedAt": "2025-01-11T14:30:00Z"
  }
}
```

---

### 4. 댓글 삭제

본인이 작성한 댓글 또는 스터디장이 삭제 가능

**Request**
```
DELETE /api/v1/studies/{studyId}/comments/{commentId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "댓글이 삭제되었습니다."
}
```

---

### 5. 대댓글 작성

**Request**
```
POST /api/v1/studies/{studyId}/comments/{commentId}/replies
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "content": "네, 아직 2자리 남았습니다!"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 5,
    "parentId": 4,
    "content": "네, 아직 2자리 남았습니다!",
    "author": {
      "id": 1,
      "nickname": "스터디장",
      "profileImage": "https://..."
    },
    "isLeader": true,
    "createdAt": "2025-01-11T15:00:00Z"
  }
}
```

---

### 6. 댓글 이미지 업로드

댓글 작성 전 이미지를 먼저 업로드

**Request**
```
POST /api/v1/studies/{studyId}/comments/images
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

| Field | Type | 필수 | 설명 |
|-------|------|------|------|
| image | file | O | 이미지 파일 (jpg, png, gif, webp) |

> 최대 파일 크기: 5MB

**Response**
```json
{
  "success": true,
  "data": {
    "imageUrl": "https://s3.../comments/temp/abc123.png",
    "expiresAt": "2025-01-11T15:00:00Z"
  }
}
```

> 임시 URL은 1시간 후 만료. 댓글 작성 시 영구 URL로 변환됨.

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| STUDY_NOT_FOUND | 스터디를 찾을 수 없음 |
| COMMENT_NOT_FOUND | 댓글을 찾을 수 없음 |
| NOT_COMMENT_AUTHOR | 댓글 작성자가 아님 |
| COMMENT_TOO_LONG | 댓글이 너무 김 (최대 500자) |
| CANNOT_REPLY_TO_REPLY | 대댓글에는 답글 불가 |
| IMAGE_TOO_LARGE | 이미지 크기 초과 (최대 5MB) |
| INVALID_IMAGE_FORMAT | 지원하지 않는 이미지 형식 |
