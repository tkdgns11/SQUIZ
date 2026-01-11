# 스터디 API (Study)

## 기본 정보
- Base URL: `/api/v1/studies`
- 인증: JWT 필요 (일부 조회 제외)

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/` | 스터디 목록 조회 (탐색) | X |
| GET | `/{studyId}` | 스터디 상세 조회 | X |
| POST | `/` | 스터디 생성 | O |
| PUT | `/{studyId}` | 스터디 수정 | O |
| DELETE | `/{studyId}` | 스터디 삭제 | O |
| GET | `/my` | 내 스터디 목록 | O |
| GET | `/bookmarks` | 찜한 스터디 목록 | O |
| POST | `/{studyId}/bookmark` | 스터디 찜하기 | O |
| DELETE | `/{studyId}/bookmark` | 스터디 찜 해제 | O |
| GET | `/recommendations` | AI 스터디 추천 | O |
| POST | `/{studyId}/invite-link` | 초대 링크 생성 | O |
| GET | `/join/{inviteCode}` | 초대 링크로 스터디 조회 | O |
| GET | `/regions` | 지역 목록 조회 | X |

---

## API 상세

### 1. 스터디 목록 조회 (탐색)

**Request**
```
GET /api/v1/studies?page=0&size=20&topic=알고리즘&format=문제풀이&studyType=PLANNED&status=RECRUITING&sort=createdAt,desc
```

| Parameter | Type | 필수 | 설명 |
|-----------|------|------|------|
| page | int | X | 페이지 번호 (기본: 0) |
| size | int | X | 페이지 크기 (기본: 20) |
| topic | string | X | 주제 필터 |
| format | string | X | 형식 필터 |
| studyType | string | X | PLANNED/LIGHTNING |
| meetingType | string | X | ONLINE/OFFLINE/HYBRID |
| status | string | X | RECRUITING/IN_PROGRESS |
| keyword | string | X | 검색어 |
| regionId | int | X | 지역 ID (오프라인 스터디 필터) |
| scheduleDays | string | X | 요일 필터 (쉼표 구분: MON,WED,FRI) |
| scheduleTime | string | X | 시간대 필터 (MORNING/AFTERNOON/EVENING/NIGHT) |
| targetOrgType | string | X | 대상 소속 타입 (SSAFY/NBC/WTC 등) |
| sort | string | X | 정렬 기준 |

**Response**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "알고리즘 스터디",
        "topic": "알고리즘",
        "format": "문제풀이",
        "studyType": "PLANNED",
        "meetingType": "OFFLINE",
        "status": "RECRUITING",
        "maxMembers": 6,
        "currentMembers": 4,
        "region": {
          "id": 1,
          "name": "서울"
        },
        "locationDetail": "강남역 스터디카페",
        "scheduleSummary": "매주 월/수/금 19:00-21:00",
        "leader": {
          "id": 1,
          "nickname": "홍길동",
          "profileImage": "https://...",
          "leaderRating": 4.5,
          "leaderReviewCount": 12
        },
        "startDate": "2025-01-15",
        "endDate": "2025-03-15",
        "totalSessions": 8,
        "isBookmarked": false,
        "createdAt": "2025-01-01T00:00:00Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

---

### 2. 스터디 상세 조회

**Request**
```
GET /api/v1/studies/{studyId}
Authorization: Bearer {accessToken}  // 선택
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "알고리즘 스터디",
    "description": "매주 백준 문제를 풀고 코드 리뷰를 진행합니다.",
    "topic": "알고리즘",
    "format": "문제풀이",
    "studyType": "PLANNED",
    "meetingType": "OFFLINE",
    "status": "RECRUITING",
    "maxMembers": 6,
    "currentMembers": 4,
    "isPublic": true,
    "penaltyPolicy": "NORMAL",
    "region": {
      "id": 1,
      "name": "서울"
    },
    "locationDetail": "강남역 스터디카페",
    "scheduleSummary": "매주 월/수/금 19:00-21:00",
    "scheduleDays": ["MON", "WED", "FRI"],
    "scheduleTime": "19:00-21:00",
    "startDate": "2025-01-15",
    "endDate": "2025-03-15",
    "totalSessions": 8,
    "recruitStartDate": "2025-01-01",
    "recruitEndDate": "2025-01-14",
    "targetOrgType": "SSAFY",
    "targetOrgCriteria": {"generation": 14},
    "leader": {
      "id": 1,
      "nickname": "홍길동",
      "profileImage": "https://...",
      "leaderRating": 4.5,
      "leaderReviewCount": 10
    },
    "members": [
      {
        "id": 2,
        "nickname": "김싸피",
        "profileImage": "https://...",
        "role": "MEMBER",
        "joinedAt": "2025-01-05T00:00:00Z"
      }
    ],
    "sessions": [
      {
        "id": 1,
        "sessionNumber": 1,
        "title": "OT 및 환경설정",
        "scheduledAt": "2025-01-15T19:00:00Z",
        "status": "SCHEDULED"
      }
    ],
    "isBookmarked": false,
    "isApplied": false,
    "isMember": false,
    "createdAt": "2025-01-01T00:00:00Z"
  }
}
```

---

### 3. 스터디 생성

**Request**
```
POST /api/v1/studies
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "name": "알고리즘 스터디",
  "description": "매주 백준 문제를 풀고 코드 리뷰를 진행합니다.",
  "topic": "알고리즘",
  "format": "문제풀이",
  "studyType": "PLANNED",
  "meetingType": "OFFLINE",
  "maxMembers": 6,
  "isPublic": true,
  "penaltyPolicy": "NORMAL",
  "regionId": 1,
  "locationDetail": "강남역 스터디카페",
  "scheduleDays": ["MON", "WED", "FRI"],
  "scheduleTime": "19:00-21:00",
  "startDate": "2025-01-15",
  "endDate": "2025-03-15",
  "totalSessions": 8,
  "recruitStartDate": "2025-01-01",
  "recruitEndDate": "2025-01-14",
  "targetOrgType": "SSAFY",
  "targetOrgCriteria": {"generation": 14}
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "알고리즘 스터디",
    "inviteCode": "ABC123XYZ"
  }
}
```

---

### 4. 스터디 수정 (스터디장)

**Request**
```
PUT /api/v1/studies/{studyId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "name": "알고리즘 스터디 (수정)",
  "description": "설명 수정",
  "maxMembers": 8,
  "penaltyPolicy": "STRICT"
}
```

**Response**
```json
{
  "success": true,
  "message": "스터디가 수정되었습니다."
}
```

---

### 5. 내 스터디 목록

**Request**
```
GET /api/v1/studies/my
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "알고리즘 스터디",
      "topic": "알고리즘",
      "myRole": "LEADER",
      "status": "IN_PROGRESS",
      "currentMembers": 6,
      "nextSession": {
        "sessionNumber": 3,
        "scheduledAt": "2025-01-20T19:00:00Z"
      },
      "unreadCount": 5
    },
    {
      "id": 2,
      "name": "CS 스터디",
      "topic": "CS",
      "myRole": "MEMBER",
      "status": "IN_PROGRESS",
      "currentMembers": 4,
      "nextSession": null,
      "unreadCount": 0
    }
  ]
}
```

---

### 6. 찜한 스터디 목록

**Request**
```
GET /api/v1/studies/bookmarks
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": [
    {
      "id": 3,
      "name": "Java 스터디",
      "topic": "프로그래밍",
      "status": "RECRUITING",
      "currentMembers": 3,
      "maxMembers": 5,
      "bookmarkedAt": "2025-01-05T00:00:00Z"
    }
  ]
}
```

---

### 7. 스터디 찜하기/해제

**Request - 찜하기**
```
POST /api/v1/studies/{studyId}/bookmark
Authorization: Bearer {accessToken}
```

**Request - 찜 해제**
```
DELETE /api/v1/studies/{studyId}/bookmark
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "찜 목록에 추가되었습니다."
}
```

---

### 8. AI 스터디 추천

**Request**
```
GET /api/v1/studies/recommendations
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": [
    {
      "id": 5,
      "name": "저녁 알고리즘 스터디",
      "topic": "알고리즘",
      "matchingScore": 95.5,
      "matchingReasons": [
        "월/수/금 19:00-21:00 일정이 가용 시간과 100% 일치",
        "관심 주제 '알고리즘'과 일치"
      ],
      "schedules": [
        {"dayOfWeek": "MON", "startTime": "19:00", "endTime": "21:00"},
        {"dayOfWeek": "WED", "startTime": "19:00", "endTime": "21:00"},
        {"dayOfWeek": "FRI", "startTime": "19:00", "endTime": "21:00"}
      ]
    }
  ]
}
```

---

### 9. 초대 링크 생성 (스터디장)

**Request**
```
POST /api/v1/studies/{studyId}/invite-link
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "inviteCode": "ABC123XYZ",
    "inviteUrl": "https://modustudy.com/join/ABC123XYZ",
    "expiresAt": "2025-01-17T00:00:00Z"
  }
}
```

---

### 10. 지역 목록 조회

**Request**
```
GET /api/v1/studies/regions
```

**Response**
```json
{
  "success": true,
  "data": [
    {"id": 1, "code": "SEOUL", "name": "서울", "sortOrder": 1},
    {"id": 2, "code": "GYEONGGI", "name": "경기", "sortOrder": 2},
    {"id": 3, "code": "DAEJEON", "name": "대전", "sortOrder": 3},
    {"id": 4, "code": "GWANGJU", "name": "광주", "sortOrder": 4},
    {"id": 5, "code": "BUSAN", "name": "부산", "sortOrder": 5}
  ]
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| STUDY_NOT_FOUND | 스터디를 찾을 수 없음 |
| NOT_STUDY_LEADER | 스터디장 권한 필요 |
| STUDY_FULL | 스터디 정원 초과 |
| RECRUIT_CLOSED | 모집 기간 종료 |
| ALREADY_MEMBER | 이미 스터디 멤버 |
| INVALID_INVITE_CODE | 유효하지 않은 초대 코드 |
| ORG_VERIFICATION_REQUIRED | 소속 인증 필요 (해당 소속 전용 스터디) |
| ORG_CRITERIA_NOT_MET | 소속 조건 미충족 (기수 등) |
