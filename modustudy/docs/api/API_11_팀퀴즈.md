# 팀 내 퀴즈 API (Study Quiz)

## 기본 정보
- Base URL: `/api/v1/studies/{studyId}/quizzes`
- 인증: JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/` | 퀴즈 목록 조회 | O |
| GET | `/{quizId}` | 퀴즈 상세 조회 | O |
| POST | `/{quizId}/attempt` | 퀴즈 시작 | O |
| POST | `/{quizId}/attempt/{attemptId}/answer` | 답안 제출 | O |
| GET | `/{quizId}/attempt/{attemptId}/result` | 퀴즈 결과 | O |
| GET | `/wrong-notes` | 오답 노트 목록 | O |
| POST | `/wrong-notes/review` | 오답 복습 시작 | O |
| PUT | `/wrong-notes/{noteId}/mastered` | 오답 숙지 완료 | O |
| GET | `/today` | 오늘의 퀴즈 | O |

---

## API 상세

### 1. 퀴즈 목록 조회

**Request**
```
GET /api/v1/studies/{studyId}/quizzes?page=0&size=20
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
        "title": "1회차 미팅 복습 퀴즈",
        "sourceType": "MEETING",
        "sourceName": "1회차 스터디 미팅",
        "questionCount": 5,
        "status": "ACTIVE",
        "myAttempts": 1,
        "myBestScore": 80,
        "createdAt": "2025-01-16T00:00:00Z"
      },
      {
        "id": 2,
        "title": "DP 자료 복습 퀴즈",
        "sourceType": "MATERIAL",
        "sourceName": "DP 개념 정리",
        "questionCount": 10,
        "status": "ACTIVE",
        "myAttempts": 0,
        "myBestScore": null,
        "createdAt": "2025-01-17T00:00:00Z"
      }
    ],
    "page": 0,
    "totalElements": 5
  }
}
```

---

### 2. 퀴즈 상세 조회

**Request**
```
GET /api/v1/studies/{studyId}/quizzes/{quizId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "1회차 미팅 복습 퀴즈",
    "sourceType": "MEETING",
    "source": {
      "id": 1,
      "name": "1회차 스터디 미팅",
      "date": "2025-01-15T19:00:00Z"
    },
    "questionCount": 5,
    "status": "ACTIVE",
    "myAttempts": [
      {
        "attemptId": 1,
        "score": 80,
        "correctCount": 4,
        "completedAt": "2025-01-16T10:00:00Z"
      }
    ],
    "createdAt": "2025-01-16T00:00:00Z"
  }
}
```

---

### 3. 퀴즈 시작

**Request**
```
POST /api/v1/studies/{studyId}/quizzes/{quizId}/attempt
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "attemptId": 2,
    "questions": [
      {
        "id": 1,
        "questionNumber": 1,
        "questionText": "DP에서 메모이제이션이란?",
        "questionType": "MULTIPLE_CHOICE",
        "options": [
          {"id": "A", "text": "반복문을 사용하는 기법"},
          {"id": "B", "text": "이미 계산한 값을 저장하는 기법"},
          {"id": "C", "text": "재귀를 사용하지 않는 기법"},
          {"id": "D", "text": "분할 정복 기법"}
        ]
      },
      {
        "id": 2,
        "questionNumber": 2,
        "questionText": "피보나치 수열의 시간복잡도를 DP로 줄이면?",
        "questionType": "SHORT_ANSWER",
        "options": null
      }
    ],
    "totalQuestions": 5
  }
}
```

---

### 4. 답안 제출

**Request**
```
POST /api/v1/studies/{studyId}/quizzes/{quizId}/attempt/{attemptId}/answer
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "answers": [
    {"questionId": 1, "answer": "B"},
    {"questionId": 2, "answer": "O(n)"},
    {"questionId": 3, "answer": "A"},
    {"questionId": 4, "answer": "C"},
    {"questionId": 5, "answer": "D"}
  ]
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "attemptId": 2,
    "score": 80,
    "totalScore": 100,
    "correctCount": 4,
    "totalQuestions": 5,
    "wrongQuestionIds": [3]
  }
}
```

---

### 5. 퀴즈 결과

**Request**
```
GET /api/v1/studies/{studyId}/quizzes/{quizId}/attempt/{attemptId}/result
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "attemptId": 2,
    "score": 80,
    "correctCount": 4,
    "totalQuestions": 5,
    "results": [
      {
        "questionId": 1,
        "questionText": "DP에서 메모이제이션이란?",
        "myAnswer": "B",
        "correctAnswer": "B",
        "isCorrect": true,
        "explanation": "메모이제이션은 이미 계산한 값을 저장해두고 재사용하는 기법입니다."
      },
      {
        "questionId": 3,
        "questionText": "Top-down 방식의 특징은?",
        "myAnswer": "A",
        "correctAnswer": "C",
        "isCorrect": false,
        "explanation": "Top-down은 재귀를 사용하고, Bottom-up은 반복문을 사용합니다.",
        "addedToWrongNote": true
      }
    ]
  }
}
```

---

### 6. 오답 노트 목록

**Request**
```
GET /api/v1/studies/{studyId}/quizzes/wrong-notes?page=0&size=20
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
        "questionId": 3,
        "questionText": "Top-down 방식의 특징은?",
        "quizTitle": "1회차 미팅 복습 퀴즈",
        "myAnswer": "A",
        "correctAnswer": "C",
        "reviewCount": 2,
        "lastReviewedAt": "2025-01-18T10:00:00Z",
        "isMastered": false,
        "createdAt": "2025-01-16T10:00:00Z"
      }
    ],
    "totalWrongNotes": 5,
    "masteredCount": 2
  }
}
```

---

### 7. 오답 복습 시작

**Request**
```
POST /api/v1/studies/{studyId}/quizzes/wrong-notes/review
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "limit": 5
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "reviewSessionId": "review_123",
    "questions": [
      {
        "noteId": 1,
        "questionId": 3,
        "questionText": "Top-down 방식의 특징은?",
        "questionType": "MULTIPLE_CHOICE",
        "options": [
          {"id": "A", "text": "반복문 사용"},
          {"id": "B", "text": "배열 사용"},
          {"id": "C", "text": "재귀 사용"},
          {"id": "D", "text": "스택 사용"}
        ],
        "reviewCount": 2
      }
    ],
    "totalQuestions": 3
  }
}
```

---

### 8. 오답 숙지 완료

**Request**
```
PUT /api/v1/studies/{studyId}/quizzes/wrong-notes/{noteId}/mastered
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "오답을 숙지 완료 처리했습니다.",
  "data": {
    "noteId": 1,
    "isMastered": true,
    "totalReviewCount": 3
  }
}
```

---

### 9. 오늘의 퀴즈

**Request**
```
GET /api/v1/studies/{studyId}/quizzes/today
Authorization: Bearer {accessToken}
```

**Response - 퀴즈 있음**
```json
{
  "success": true,
  "data": {
    "available": true,
    "quiz": {
      "id": 3,
      "title": "오늘의 복습 퀴즈",
      "questionCount": 5,
      "source": "최근 미팅 및 자료 기반",
      "hasCompleted": false
    },
    "wrongNoteCount": 3,
    "suggestion": "오답 노트에 3개의 문제가 있습니다. 복습해보세요!"
  }
}
```

**Response - 퀴즈 없음**
```json
{
  "success": true,
  "data": {
    "available": false,
    "message": "아직 생성된 퀴즈가 없습니다. 미팅이나 자료가 추가되면 AI가 퀴즈를 생성합니다.",
    "wrongNoteCount": 3
  }
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| QUIZ_NOT_FOUND | 퀴즈를 찾을 수 없음 |
| ATTEMPT_NOT_FOUND | 시도를 찾을 수 없음 |
| QUIZ_DISABLED | 비활성화된 퀴즈 |
| ALREADY_COMPLETED | 이미 완료된 시도 |
| WRONG_NOTE_NOT_FOUND | 오답 노트를 찾을 수 없음 |
