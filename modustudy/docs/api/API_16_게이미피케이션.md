# 게이미피케이션 API (Gamification)

## 기본 정보
- Base URL: `/api/v1/gamification`
- 인증: JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/contributions` | 잔디 그래프 조회 | O |
| GET | `/contributions/{date}` | 특정 날짜 활동 상세 | O |
| GET | `/stats` | 내 활동 통계 | O |
| GET | `/badges` | 뱃지 목록 (긍정/영구) | O |
| GET | `/penalties` | 패널티 목록 (부정/일시) | O |
| GET | `/studies/{studyId}/ranking` | 팀 내 랭킹 | O |

---

## API 상세

### 1. 잔디 그래프 조회

**Request**
```
GET /api/v1/gamification/contributions?year=2025&month=1
Authorization: Bearer {accessToken}
```

| Parameter | Type | 필수 | 설명 |
|-----------|------|------|------|
| year | int | O | 연도 |
| month | int | X | 월 (없으면 연간 조회) |

**Response - 월간 조회**
```json
{
  "success": true,
  "data": {
    "year": 2025,
    "month": 1,
    "contributions": [
      {"date": "2025-01-01", "hasActivity": true},
      {"date": "2025-01-02", "hasActivity": true},
      {"date": "2025-01-03", "hasActivity": false},
      {"date": "2025-01-04", "hasActivity": true}
    ],
    "summary": {
      "totalDays": 31,
      "activeDays": 15,
      "currentStreak": 3,
      "maxStreak": 7
    }
  }
}
```

**Response - 연간 조회**
```json
{
  "success": true,
  "data": {
    "year": 2025,
    "contributions": [
      {"date": "2025-01-01", "hasActivity": true},
      {"date": "2025-01-02", "hasActivity": true}
    ],
    "summary": {
      "totalDays": 365,
      "activeDays": 45,
      "currentStreak": 3,
      "maxStreak": 15
    },
    "monthlyStats": [
      {"month": 1, "activeDays": 15},
      {"month": 2, "activeDays": 0}
    ]
  }
}
```

---

### 2. 특정 날짜 활동 상세

**Request**
```
GET /api/v1/gamification/contributions/2025-01-15
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "date": "2025-01-15",
    "hasActivity": true,
    "activities": [
      {
        "type": "STUDY_ATTENDANCE",
        "referenceId": 1,
        "referenceName": "알고리즘 스터디 1회차",
        "createdAt": "2025-01-15T19:00:00Z"
      },
      {
        "type": "QUIZ_CONTEST",
        "referenceId": 5,
        "referenceName": "CS 기초 퀴즈",
        "createdAt": "2025-01-15T14:00:00Z"
      }
    ]
  }
}
```

---

### 3. 내 활동 통계

**Request**
```
GET /api/v1/gamification/stats
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "level": 5,
    "levelName": "성실러",
    "levelProgress": {
      "current": 45,
      "required": 60,
      "percentage": 75.0
    },
    "nextLevel": {
      "level": 6,
      "name": "열공러"
    },
    "totalActivityDays": 45,
    "currentStreak": 7,
    "maxStreak": 15,
    "lastActivityDate": "2025-01-17",
    "totalStudiesJoined": 3,
    "totalStudiesLed": 1,
    "totalAttendance": 20,
    "totalChatCount": 150,
    "totalQuizCount": 12,
    "totalMaterialsUploaded": 8,
    "totalRetrospectives": 5,
    "joinedAt": "2025-01-01T00:00:00Z"
  }
}
```

---

### 4. 뱃지 목록 (긍정/영구)

> 한번 획득하면 영구 유지되는 긍정적 보상

**Request**
```
GET /api/v1/gamification/badges
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "categories": [
      {
        "category": "ACTIVITY",
        "categoryName": "활동",
        "badges": [
          {
            "id": 1,
            "code": "FIRST_ACTIVITY",
            "name": "첫 발걸음",
            "description": "첫 활동 기록",
            "icon": "👣",
            "isEarned": true,
            "earnedAt": "2025-01-01T00:00:00Z",
            "progress": null
          },
          {
            "id": 2,
            "code": "ACTIVITY_30",
            "name": "꾸준함의 시작",
            "description": "30일 활동 달성",
            "icon": "📅",
            "isEarned": false,
            "earnedAt": null,
            "progress": {
              "current": 15,
              "required": 30,
              "percentage": 50.0
            }
          }
        ]
      },
      {
        "category": "STREAK",
        "categoryName": "스트릭",
        "badges": [
          {
            "id": 3,
            "code": "STREAK_7",
            "name": "일주일 연속",
            "description": "7일 연속 활동",
            "icon": "🔥",
            "isEarned": true,
            "earnedAt": "2025-01-10T00:00:00Z"
          }
        ]
      }
    ],
    "totalBadges": 20,
    "earnedCount": 5
  }
}
```

---

### 5. 패널티 목록 (부정/일시)

> 일시적으로 부여되며, 조건 충족 시 해소 가능

**Request**
```
GET /api/v1/gamification/penalties
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "activePenalties": [
      {
        "id": 1,
        "penaltyCode": "THREE_DAY_QUIT",
        "name": "작심삼일",
        "description": "스터디 3회 연속 불참",
        "icon": "😅",
        "grantedAt": "2025-01-10T00:00:00Z",
        "studyId": 1,
        "studyName": "알고리즘 스터디",
        "isActive": true,
        "removalCondition": "3일 연속 출석 시 해소",
        "removalProgress": 1,
        "removalRequired": 3
      },
      {
        "id": 2,
        "penaltyCode": "GHOST_MEMBER",
        "name": "유령회원",
        "description": "7일 이상 미접속",
        "icon": "👻",
        "grantedAt": "2025-01-08T00:00:00Z",
        "studyId": null,
        "studyName": null,
        "isActive": true,
        "removalCondition": "3일 연속 로그인 시 해소",
        "removalProgress": 0,
        "removalRequired": 3
      }
    ],
    "removedPenalties": [
      {
        "id": 3,
        "penaltyCode": "LATE_KING",
        "name": "지각왕",
        "description": "3회 연속 지각",
        "icon": "⏰",
        "grantedAt": "2025-01-05T00:00:00Z",
        "removedAt": "2025-01-12T00:00:00Z",
        "studyId": 1,
        "studyName": "알고리즘 스터디"
      }
    ],
    "totalActive": 2,
    "totalRemoved": 1
  }
}
```

---

### 6. 팀 내 랭킹

**Request**
```
GET /api/v1/gamification/studies/{studyId}/ranking
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "rankings": [
      {
        "rank": 1,
        "user": {
          "id": 1,
          "nickname": "홍길동",
          "profileImage": "https://...",
          "level": 5,
          "levelName": "성실러"
        },
        "activityDays": 15,
        "attendanceRate": 100.0,
        "isMe": false
      },
      {
        "rank": 2,
        "user": {
          "id": 2,
          "nickname": "김싸피",
          "profileImage": "https://...",
          "level": 4,
          "levelName": "열공러"
        },
        "activityDays": 12,
        "attendanceRate": 90.0,
        "isMe": true
      }
    ],
    "myRank": 2,
    "totalMembers": 6
  }
}
```

---

## 레벨 시스템

| 레벨 | 이름 | 필요 활동일 |
|------|------|------------|
| 1 | 새싹 | 0 |
| 2 | 학습자 | 7 |
| 3 | 열공러 | 15 |
| 4 | 성실러 | 30 |
| 5 | 마스터 | 60 |
| 6 | 그랜드마스터 | 100 |

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| INVALID_DATE | 유효하지 않은 날짜 |
| STUDY_NOT_FOUND | 스터디를 찾을 수 없음 |
| NOT_STUDY_MEMBER | 스터디 멤버가 아님 |
