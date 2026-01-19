# 퀴즈 코스 API (Quiz Course)

## 기본 정보
- Base URL: `/api/v1/quiz-courses`
- 인증: 일부 JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/` | 코스 목록 조회 | X |
| GET | `/{courseId}` | 코스 상세 조회 | X |
| GET | `/{courseId}/sections` | 섹션 목록 조회 | O |
| GET | `/{courseId}/sections/{sectionNumber}` | 섹션 문제 조회 | O |
| POST | `/{courseId}/sections/{sectionNumber}/submit` | 섹션 제출 | O |
| GET | `/my/progress` | 내 코스 진행 현황 | O |
| GET | `/my/progress/{courseId}` | 특정 코스 진행 상세 | O |

---

## API 상세

### 1. 코스 목록 조회

**Request**
```
GET /api/v1/quiz-courses
```

**Response**
```json
{
  "success": true,
  "data": {
    "courses": [
      {
        "courseId": 1,
        "code": "JAVA",
        "name": "Java 마스터",
        "description": "Java 기초부터 고급까지",
        "totalSections": 5,
        "badgeCode": "JAVA_MASTER",
        "badgeName": "Java 마스터"
      },
      {
        "courseId": 2,
        "code": "PYTHON",
        "name": "Python 기초",
        "description": "Python 입문자를 위한 코스",
        "totalSections": 4,
        "badgeCode": "PYTHON_MASTER",
        "badgeName": "Python 마스터"
      },
      {
        "courseId": 3,
        "code": "CS_BASIC",
        "name": "CS 기초",
        "description": "컴퓨터 과학 기초 개념",
        "totalSections": 6,
        "badgeCode": "CS_MASTER",
        "badgeName": "CS 마스터"
      }
    ]
  }
}
```

---

### 2. 코스 상세 조회

**Request**
```
GET /api/v1/quiz-courses/{courseId}
```

**Response**
```json
{
  "success": true,
  "data": {
    "courseId": 1,
    "code": "JAVA",
    "name": "Java 마스터",
    "description": "Java 기초부터 고급까지 단계별 학습",
    "totalSections": 5,
    "badge": {
      "code": "JAVA_MASTER",
      "name": "Java 마스터",
      "description": "Java 코스 완료"
    },
    "sections": [
      {
        "sectionNumber": 1,
        "name": "기본 문법",
        "description": "변수, 자료형, 연산자",
        "totalQuestions": 10,
        "passScore": 70
      },
      {
        "sectionNumber": 2,
        "name": "객체지향",
        "description": "클래스, 상속, 다형성",
        "totalQuestions": 15,
        "passScore": 70
      },
      {
        "sectionNumber": 3,
        "name": "컬렉션",
        "description": "List, Map, Set",
        "totalQuestions": 12,
        "passScore": 70
      },
      {
        "sectionNumber": 4,
        "name": "람다 & 스트림",
        "description": "함수형 프로그래밍",
        "totalQuestions": 10,
        "passScore": 70
      },
      {
        "sectionNumber": 5,
        "name": "쓰레드",
        "description": "멀티쓰레딩, 동기화",
        "totalQuestions": 10,
        "passScore": 70
      }
    ]
  }
}
```

---

### 3. 섹션 목록 조회 (진행 상황 포함)

**Request**
```
GET /api/v1/quiz-courses/{courseId}/sections
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "courseId": 1,
    "courseName": "Java 마스터",
    "myProgress": {
      "currentSection": 3,
      "completedSections": 2,
      "isCompleted": false
    },
    "sections": [
      {
        "sectionNumber": 1,
        "name": "기본 문법",
        "totalQuestions": 10,
        "passScore": 70,
        "isUnlocked": true,
        "isPassed": true,
        "bestScore": 90,
        "attemptCount": 2
      },
      {
        "sectionNumber": 2,
        "name": "객체지향",
        "totalQuestions": 15,
        "passScore": 70,
        "isUnlocked": true,
        "isPassed": true,
        "bestScore": 80,
        "attemptCount": 1
      },
      {
        "sectionNumber": 3,
        "name": "컬렉션",
        "totalQuestions": 12,
        "passScore": 70,
        "isUnlocked": true,
        "isPassed": false,
        "bestScore": 50,
        "attemptCount": 1
      },
      {
        "sectionNumber": 4,
        "name": "람다 & 스트림",
        "totalQuestions": 10,
        "passScore": 70,
        "isUnlocked": false,
        "isPassed": false,
        "bestScore": null,
        "attemptCount": 0
      },
      {
        "sectionNumber": 5,
        "name": "쓰레드",
        "totalQuestions": 10,
        "passScore": 70,
        "isUnlocked": false,
        "isPassed": false,
        "bestScore": null,
        "attemptCount": 0
      }
    ]
  }
}
```

---

### 4. 섹션 문제 조회

**Request**
```
GET /api/v1/quiz-courses/{courseId}/sections/{sectionNumber}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "courseId": 1,
    "sectionNumber": 1,
    "sectionName": "기본 문법",
    "totalQuestions": 10,
    "passScore": 70,
    "questions": [
      {
        "questionNumber": 1,
        "questionText": "Java에서 정수형 변수를 선언할 때 사용하는 키워드는?",
        "questionType": "MULTIPLE_CHOICE",
        "options": [
          {"id": "A", "text": "integer"},
          {"id": "B", "text": "int"},
          {"id": "C", "text": "num"},
          {"id": "D", "text": "number"}
        ]
      },
      {
        "questionNumber": 2,
        "questionText": "다음 중 Java의 기본 자료형이 아닌 것은?",
        "questionType": "MULTIPLE_CHOICE",
        "options": [
          {"id": "A", "text": "int"},
          {"id": "B", "text": "String"},
          {"id": "C", "text": "boolean"},
          {"id": "D", "text": "double"}
        ]
      }
    ]
  }
}
```

**Error Response - 잠긴 섹션**
```json
{
  "success": false,
  "error": {
    "code": "SECTION_LOCKED",
    "message": "이전 섹션을 먼저 완료해주세요."
  }
}
```

---

### 5. 섹션 제출

**Request**
```
POST /api/v1/quiz-courses/{courseId}/sections/{sectionNumber}/submit
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "answers": [
    { "questionNumber": 1, "answer": ["B"] },
    { "questionNumber": 2, "answer": ["B"] }
  ]
}
```

**Response - 통과**
```json
{
  "success": true,
  "data": {
    "courseId": 1,
    "sectionNumber": 1,
    "sectionName": "기본 문법",
    "score": 80,
    "correctCount": 8,
    "totalQuestions": 10,
    "passScore": 70,
    "isPassed": true,
    "isFirstPass": true,
    "nextSection": {
      "courseId": 1,
      "sectionNumber": 2,
      "name": "객체지향"
    },
    "results": [
      {
        "questionNumber": 1,
        "isCorrect": true,
        "userAnswer": ["B"],
        "correctAnswer": ["B"],
        "explanation": "Java에서 정수형은 int 키워드를 사용합니다."
      },
      {
        "questionNumber": 2,
        "isCorrect": true,
        "userAnswer": ["B"],
        "correctAnswer": ["B"],
        "explanation": "String은 참조형입니다."
      }
    ]
  },
  "message": "축하합니다! 다음 섹션이 해금되었습니다."
}
```

**Response - 코스 완료 (마지막 섹션 통과)**
```json
{
  "success": true,
  "data": {
    "courseId": 1,
    "sectionNumber": 5,
    "sectionName": "쓰레드",
    "score": 90,
    "correctCount": 9,
    "totalQuestions": 10,
    "passScore": 70,
    "isPassed": true,
    "isFirstPass": true,
    "isCourseCompleted": true,
    "earnedBadge": {
      "code": "JAVA_MASTER",
      "name": "Java 마스터",
      "icon": "☕",
      "description": "Java 코스 완료"
    },
    "results": [...]
  },
  "message": "축하합니다! Java 코스를 완료하고 Java 마스터 뱃지를 획득했습니다!"
}
```

**Response - 불통과**
```json
{
  "success": true,
  "data": {
    "courseId": 1,
    "sectionNumber": 3,
    "sectionName": "컬렉션",
    "score": 50,
    "correctCount": 5,
    "totalQuestions": 12,
    "passScore": 70,
    "isPassed": false,
    "results": [
      {
        "questionNumber": 1,
        "isCorrect": true,
        "userAnswer": ["B"],
        "correctAnswer": ["B"],
        "explanation": "List는 순서가 있는 컬렉션입니다."
      },
      {
        "questionNumber": 2,
        "isCorrect": false,
        "userAnswer": ["A"],
        "correctAnswer": ["C"],
        "explanation": "Set은 중복을 허용하지 않습니다."
      }
    ]
  },
  "message": "아쉽습니다. 70% 이상 맞춰야 통과입니다. 다시 도전해보세요!"
}
```

---

### 6. 내 코스 진행 현황

**Request**
```
GET /api/v1/quiz-courses/my/progress
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "inProgress": [
      {
        "courseId": 1,
        "courseCode": "JAVA",
        "courseName": "Java 마스터",
        "icon": "☕",
        "totalSections": 5,
        "completedSections": 2,
        "progressPercent": 40,
        "lastActivityAt": "2025-01-10T12:00:00Z"
      }
    ],
    "completed": [
      {
        "courseId": 2,
        "courseCode": "PYTHON",
        "courseName": "Python 기초",
        "icon": "🐍",
        "totalSections": 4,
        "completedAt": "2025-01-05T15:00:00Z",
        "earnedBadge": {
          "code": "PYTHON_MASTER",
          "name": "Python 마스터",
          "icon": "🐍"
        }
      }
    ],
    "notStarted": [
      {
        "courseId": 3,
        "courseCode": "CS_BASIC",
        "courseName": "CS 기초",
        "icon": "💻",
        "totalSections": 6
      }
    ]
  }
}
```

---

### 7. 특정 코스 진행 상세

**Request**
```
GET /api/v1/quiz-courses/my/progress/{courseId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "courseId": 1,
    "courseName": "Java 마스터",
    "icon": "☕",
    "totalSections": 5,
    "completedSections": 2,
    "isCompleted": false,
    "startedAt": "2025-01-01T10:00:00Z",
    "sectionDetails": [
      {
        "sectionNumber": 1,
        "name": "기본 문법",
        "isPassed": true,
        "bestScore": 90,
        "attemptCount": 2,
        "passedAt": "2025-01-02T14:00:00Z"
      },
      {
        "sectionNumber": 2,
        "name": "객체지향",
        "isPassed": true,
        "bestScore": 80,
        "attemptCount": 1,
        "passedAt": "2025-01-05T16:00:00Z"
      },
      {
        "sectionNumber": 3,
        "name": "컬렉션",
        "isPassed": false,
        "bestScore": 50,
        "attemptCount": 1,
        "passedAt": null
      },
      {
        "sectionNumber": 4,
        "name": "람다 & 스트림",
        "isPassed": false,
        "bestScore": null,
        "attemptCount": 0,
        "passedAt": null
      },
      {
        "sectionNumber": 5,
        "name": "쓰레드",
        "isPassed": false,
        "bestScore": null,
        "attemptCount": 0,
        "passedAt": null
      }
    ]
  }
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| COURSE_NOT_FOUND | 코스를 찾을 수 없음 |
| SECTION_NOT_FOUND | 섹션을 찾을 수 없음 |
| SECTION_LOCKED | 잠긴 섹션 (이전 섹션 미완료) |
| INVALID_ANSWERS | 답변 형식 오류 |
