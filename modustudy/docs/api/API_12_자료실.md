# 자료실 API (Material)

## 기본 정보
- Base URL: `/api/v1/studies/{studyId}/materials`
- 인증: JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/` | 자료 목록 조회 | O |
| GET | `/{materialId}` | 자료 상세 조회 | O |
| POST | `/` | 자료 업로드 | O |
| PUT | `/{materialId}` | 자료 수정 | O |
| DELETE | `/{materialId}` | 자료 삭제 | O |
| GET | `/{materialId}/comments` | 댓글 목록 | O |
| POST | `/{materialId}/comments` | 댓글 작성 | O |
| DELETE | `/{materialId}/comments/{commentId}` | 댓글 삭제 | O |

---

## API 상세

### 1. 자료 목록 조회

**Request**
```
GET /api/v1/studies/{studyId}/materials?page=0&size=20&weekNumber=1&type=FILE
Authorization: Bearer {accessToken}
```

| Parameter | Type | 필수 | 설명 |
|-----------|------|------|------|
| page | int | X | 페이지 번호 |
| size | int | X | 페이지 크기 |
| weekNumber | int | X | 주차 필터 |
| type | string | X | LINK/FILE/IMAGE/VIDEO |
| keyword | string | X | 검색어 |

**Response**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "DP 개념 정리",
        "description": "다이나믹 프로그래밍 기초 개념",
        "materialType": "FILE",
        "fileUrl": "https://storage.../dp_concept.pdf",
        "fileSize": 1024000,
        "weekNumber": 1,
        "viewCount": 15,
        "commentCount": 3,
        "uploader": {
          "id": 1,
          "nickname": "홍길동",
          "profileImage": "https://..."
        },
        "createdAt": "2025-01-15T10:00:00Z"
      },
      {
        "id": 2,
        "title": "백준 문제 링크",
        "description": "이번 주 풀어야 할 문제 목록",
        "materialType": "LINK",
        "url": "https://www.acmicpc.net/problem/1000",
        "weekNumber": 1,
        "viewCount": 20,
        "commentCount": 1,
        "uploader": {
          "id": 2,
          "nickname": "김싸피"
        },
        "createdAt": "2025-01-15T11:00:00Z"
      }
    ],
    "page": 0,
    "totalElements": 10
  }
}
```

---

### 2. 자료 상세 조회

**Request**
```
GET /api/v1/studies/{studyId}/materials/{materialId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "DP 개념 정리",
    "description": "다이나믹 프로그래밍 기초 개념을 정리한 문서입니다.",
    "materialType": "FILE",
    "fileUrl": "https://storage.../dp_concept.pdf",
    "fileName": "dp_concept.pdf",
    "fileSize": 1024000,
    "weekNumber": 1,
    "viewCount": 16,
    "uploader": {
      "id": 1,
      "nickname": "홍길동",
      "profileImage": "https://..."
    },
    "createdAt": "2025-01-15T10:00:00Z",
    "updatedAt": "2025-01-15T10:00:00Z"
  }
}
```

---

### 3. 자료 업로드

**Request - 파일/이미지**
```
POST /api/v1/studies/{studyId}/materials
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

| Field | Type | 필수 | 설명 |
|-------|------|------|------|
| file | File | O | 파일 (max 50MB) |
| title | string | O | 제목 |
| description | string | X | 설명 |
| weekNumber | int | X | 주차 |

**Request - 링크**
```
POST /api/v1/studies/{studyId}/materials
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "title": "백준 문제 링크",
  "description": "이번 주 풀어야 할 문제",
  "materialType": "LINK",
  "url": "https://www.acmicpc.net/problem/1000",
  "weekNumber": 1
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 3,
    "title": "백준 문제 링크",
    "materialType": "LINK",
    "createdAt": "2025-01-16T10:00:00Z"
  }
}
```

---

### 4. 자료 수정

**Request**
```
PUT /api/v1/studies/{studyId}/materials/{materialId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "title": "DP 개념 정리 (수정)",
  "description": "메모이제이션 내용 추가",
  "weekNumber": 2
}
```

**Response**
```json
{
  "success": true,
  "message": "자료가 수정되었습니다."
}
```

---

### 5. 자료 삭제

**Request**
```
DELETE /api/v1/studies/{studyId}/materials/{materialId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "자료가 삭제되었습니다."
}
```

---

### 6. 댓글 목록

**Request**
```
GET /api/v1/studies/{studyId}/materials/{materialId}/comments
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "user": {
        "id": 2,
        "nickname": "김싸피",
        "profileImage": "https://..."
      },
      "content": "정리 감사합니다!",
      "createdAt": "2025-01-15T12:00:00Z"
    },
    {
      "id": 2,
      "user": {
        "id": 3,
        "nickname": "이싸피"
      },
      "content": "5페이지에 오타가 있어요",
      "createdAt": "2025-01-15T13:00:00Z"
    }
  ]
}
```

---

### 7. 댓글 작성

**Request**
```
POST /api/v1/studies/{studyId}/materials/{materialId}/comments
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "content": "좋은 자료 감사합니다!"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 3,
    "content": "좋은 자료 감사합니다!",
    "createdAt": "2025-01-16T10:00:00Z"
  }
}
```

---

### 8. 댓글 삭제

**Request**
```
DELETE /api/v1/studies/{studyId}/materials/{materialId}/comments/{commentId}
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

## 에러 코드

| 코드 | 설명 |
|------|------|
| MATERIAL_NOT_FOUND | 자료를 찾을 수 없음 |
| NOT_MATERIAL_OWNER | 본인 자료만 수정/삭제 가능 |
| FILE_SIZE_EXCEEDED | 파일 크기 초과 |
| INVALID_FILE_TYPE | 지원하지 않는 파일 형식 |
| COMMENT_NOT_FOUND | 댓글을 찾을 수 없음 |
