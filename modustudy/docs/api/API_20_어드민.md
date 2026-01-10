# 어드민 API (Admin)

## 기본 정보
- Base URL: `/api/v1/admin`
- 인증: JWT 필요 (ADMIN 권한)

---

## 엔드포인트 목록

### 대시보드
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/dashboard` | 어드민 대시보드 통계 |

### 사용자 관리
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/users` | 사용자 목록 조회 |
| GET | `/users/{userId}` | 사용자 상세 조회 |
| PUT | `/users/{userId}/ban` | 사용자 제재 |
| DELETE | `/users/{userId}/ban` | 제재 해제 |

### 신고 관리
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/reports` | 신고 목록 조회 |
| PUT | `/reports/{reportId}` | 신고 처리 |

### 공지사항 관리
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/notices` | 공지 목록 |
| POST | `/notices` | 공지 작성 |
| PUT | `/notices/{noticeId}` | 공지 수정 |
| DELETE | `/notices/{noticeId}` | 공지 삭제 |

### 퀴즈 대회 관리
| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/quiz-contests` | 대회 생성 |
| PUT | `/quiz-contests/{contestId}` | 대회 수정 |
| DELETE | `/quiz-contests/{contestId}` | 대회 삭제 |
| POST | `/quiz-contests/{contestId}/questions` | 문제 등록 |
| PUT | `/quiz-contests/{contestId}/start` | 대회 시작 |
| PUT | `/quiz-contests/{contestId}/end` | 대회 종료 |

### AI 피드백 관리
| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/ai-feedback` | 피드백 목록/통계 |

---

## API 상세

### 1. 어드민 대시보드

**Request**
```
GET /api/v1/admin/dashboard
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "users": {
      "total": 500,
      "activeToday": 150,
      "newThisWeek": 30
    },
    "studies": {
      "total": 100,
      "active": 45,
      "recruiting": 20
    },
    "quizContests": {
      "total": 15,
      "scheduled": 2,
      "totalParticipants": 1500
    },
    "reports": {
      "pending": 5,
      "processedThisWeek": 12
    }
  }
}
```

---

### 2. 사용자 목록 조회

**Request**
```
GET /api/v1/admin/users?page=0&size=20&keyword=홍길동&status=ACTIVE
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "email": "hong@gmail.com",
        "nickname": "홍길동",
        "linkedProviders": ["GOOGLE"],
        "role": "USER",
        "isActive": true,
        "banStatus": null,
        "studyCount": 3,
        "lastLoginAt": "2025-01-17T10:00:00Z",
        "createdAt": "2025-01-01T00:00:00Z"
      }
    ],
    "page": 0,
    "totalElements": 500
  }
}
```

---

### 3. 사용자 제재

**Request**
```
PUT /api/v1/admin/users/{userId}/ban
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "banType": "TEMPORARY",
  "reason": "부적절한 채팅 내용",
  "duration": 7
}
```

| banType | 설명 |
|---------|------|
| WARNING | 경고 |
| TEMPORARY | 임시 정지 (duration 일) |
| PERMANENT | 영구 정지 |

**Response**
```json
{
  "success": true,
  "message": "사용자가 7일간 정지되었습니다."
}
```

---

### 4. 신고 목록 조회

**Request**
```
GET /api/v1/admin/reports?status=PENDING&page=0&size=20
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "reporter": {
          "id": 2,
          "nickname": "김싸피"
        },
        "targetUser": {
          "id": 3,
          "nickname": "이싸피"
        },
        "targetType": "MESSAGE",
        "targetId": 100,
        "reason": "욕설 사용",
        "content": "부적절한 메시지 내용...",
        "status": "PENDING",
        "createdAt": "2025-01-17T10:00:00Z"
      }
    ],
    "page": 0,
    "totalElements": 5
  }
}
```

---

### 5. 신고 처리

**Request**
```
PUT /api/v1/admin/reports/{reportId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "status": "APPROVED",
  "action": "WARNING",
  "adminComment": "경고 조치"
}
```

| status | 설명 |
|--------|------|
| APPROVED | 신고 승인 |
| REJECTED | 신고 기각 |

**Response**
```json
{
  "success": true,
  "message": "신고가 처리되었습니다."
}
```

---

### 6. 공지사항 작성

**Request**
```
POST /api/v1/admin/notices
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "title": "서비스 점검 안내",
  "content": "1월 20일 02:00-04:00 서버 점검이 예정되어 있습니다.",
  "isPinned": true
}
```

| Field | 설명 |
|-------|------|
| isPinned | 상단 고정 여부 |

**Response**
```json
{
  "success": true,
  "data": {
    "id": 10,
    "title": "서비스 점검 안내"
  }
}
```

---

### 7. 퀴즈 대회 생성

**Request**
```
POST /api/v1/admin/quiz-contests
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "title": "알고리즘 퀴즈 대회",
  "description": "알고리즘 기초 문제",
  "scheduledAt": "2025-01-20T14:00:00Z",
  "timeLimitSeconds": 30
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 5,
    "title": "알고리즘 퀴즈 대회",
    "status": "DRAFT"
  }
}
```

---

### 8. 퀴즈 문제 등록

**Request**
```
POST /api/v1/admin/quiz-contests/{contestId}/questions
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "questions": [
    {
      "questionNumber": 1,
      "questionText": "시간복잡도 O(n log n)인 정렬은?",
      "questionType": "MULTIPLE_CHOICE",
      "options": [
        {"id": "A", "text": "버블 정렬"},
        {"id": "B", "text": "퀵 정렬"},
        {"id": "C", "text": "삽입 정렬"},
        {"id": "D", "text": "선택 정렬"}
      ],
      "correctAnswer": "B",
      "explanation": "퀵 정렬의 평균 시간복잡도는 O(n log n)입니다.",
      "points": 10
    }
  ]
}
```

**Response**
```json
{
  "success": true,
  "message": "1개의 문제가 등록되었습니다."
}
```

---

### 9. AI 피드백 통계

**Request**
```
GET /api/v1/admin/ai-feedback?featureType=MEETING_SUMMARY
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "summary": {
      "totalFeedback": 500,
      "positiveRate": 78.5,
      "negativeRate": 21.5
    },
    "byFeature": [
      {
        "featureType": "MEETING_SUMMARY",
        "featureName": "미팅 요약",
        "totalCount": 200,
        "positiveCount": 160,
        "negativeCount": 40,
        "positiveRate": 80.0
      },
      {
        "featureType": "QUIZ_GENERATION",
        "featureName": "퀴즈 생성",
        "totalCount": 150,
        "positiveCount": 120,
        "negativeCount": 30,
        "positiveRate": 80.0
      }
    ],
    "recentNegative": [
      {
        "id": 100,
        "featureType": "MEETING_SUMMARY",
        "referenceId": 50,
        "comment": "요약이 너무 짧아요",
        "createdAt": "2025-01-17T10:00:00Z"
      }
    ]
  }
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| FORBIDDEN | 관리자 권한 필요 |
| USER_NOT_FOUND | 사용자를 찾을 수 없음 |
| REPORT_NOT_FOUND | 신고를 찾을 수 없음 |
| CONTEST_NOT_FOUND | 대회를 찾을 수 없음 |
| CANNOT_DELETE_STARTED | 시작된 대회는 삭제 불가 |
