# 커리큘럼/진도 API (Curriculum)

## 기본 정보
- Base URL: `/api/v1/studies/{studyId}/curriculum`
- 인증: JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/` | 커리큘럼 목록 조회 | O |
| POST | `/` | 커리큘럼 추가 (스터디장) | O |
| PUT | `/{curriculumId}` | 커리큘럼 수정 (스터디장) | O |
| DELETE | `/{curriculumId}` | 커리큘럼 삭제 (스터디장) | O |
| GET | `/progress` | 진도 현황 조회 | O |
| POST | `/{curriculumId}/progress` | 진도 체크 | O |
| DELETE | `/{curriculumId}/progress` | 진도 체크 해제 | O |

---

## API 상세

### 1. 커리큘럼 목록 조회

**Request**
```
GET /api/v1/studies/{studyId}/curriculum
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "curriculum": [
      {
        "id": 1,
        "weekNumber": 1,
        "title": "알고리즘 기초",
        "description": "시간복잡도, 공간복잡도, 빅오 표기법",
        "materials": [
          {
            "id": 1,
            "title": "시간복잡도 정리",
            "materialType": "FILE"
          }
        ],
        "myProgress": {
          "isCompleted": true,
          "completedAt": "2025-01-16T10:00:00Z"
        }
      },
      {
        "id": 2,
        "weekNumber": 2,
        "title": "정렬 알고리즘",
        "description": "버블, 선택, 삽입, 퀵, 병합 정렬",
        "materials": [],
        "myProgress": {
          "isCompleted": false,
          "completedAt": null
        }
      }
    ],
    "totalWeeks": 8,
    "myCompletedWeeks": 1,
    "myProgressRate": 12.5
  }
}
```

---

### 2. 커리큘럼 추가 (스터디장)

**Request**
```
POST /api/v1/studies/{studyId}/curriculum
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "weekNumber": 3,
  "title": "탐색 알고리즘",
  "description": "이분탐색, DFS, BFS"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 3,
    "weekNumber": 3,
    "title": "탐색 알고리즘"
  }
}
```

---

### 3. 커리큘럼 수정 (스터디장)

**Request**
```
PUT /api/v1/studies/{studyId}/curriculum/{curriculumId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "title": "탐색 알고리즘 (수정)",
  "description": "이분탐색, DFS, BFS, 다익스트라"
}
```

**Response**
```json
{
  "success": true,
  "message": "커리큘럼이 수정되었습니다."
}
```

---

### 4. 커리큘럼 삭제 (스터디장)

**Request**
```
DELETE /api/v1/studies/{studyId}/curriculum/{curriculumId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "커리큘럼이 삭제되었습니다."
}
```

---

### 5. 진도 현황 조회

**Request**
```
GET /api/v1/studies/{studyId}/curriculum/progress
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "members": [
      {
        "userId": 1,
        "nickname": "홍길동",
        "profileImage": "https://...",
        "completedWeeks": [1, 2],
        "progressRate": 25.0
      },
      {
        "userId": 2,
        "nickname": "김싸피",
        "profileImage": "https://...",
        "completedWeeks": [1],
        "progressRate": 12.5
      },
      {
        "userId": 3,
        "nickname": "이싸피",
        "profileImage": "https://...",
        "completedWeeks": [1, 2, 3],
        "progressRate": 37.5
      }
    ],
    "weekProgress": [
      {
        "weekNumber": 1,
        "title": "알고리즘 기초",
        "completedCount": 3,
        "totalMembers": 3,
        "completionRate": 100.0
      },
      {
        "weekNumber": 2,
        "title": "정렬 알고리즘",
        "completedCount": 2,
        "totalMembers": 3,
        "completionRate": 66.7
      }
    ],
    "totalWeeks": 8,
    "averageProgressRate": 25.0
  }
}
```

---

### 6. 진도 체크

**Request**
```
POST /api/v1/studies/{studyId}/curriculum/{curriculumId}/progress
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "curriculumId": 2,
    "weekNumber": 2,
    "isCompleted": true,
    "completedAt": "2025-01-17T10:00:00Z",
    "myProgressRate": 25.0
  }
}
```

---

### 7. 진도 체크 해제

**Request**
```
DELETE /api/v1/studies/{studyId}/curriculum/{curriculumId}/progress
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "curriculumId": 2,
    "isCompleted": false,
    "myProgressRate": 12.5
  }
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| CURRICULUM_NOT_FOUND | 커리큘럼을 찾을 수 없음 |
| DUPLICATE_WEEK_NUMBER | 중복된 주차 번호 |
| NOT_STUDY_LEADER | 스터디장 권한 필요 |
