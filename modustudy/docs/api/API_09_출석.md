# 출석 API (Attendance)

## 기본 정보
- Base URL: `/api/v1/studies/{studyId}/attendance`
- 인증: JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/sessions/{sessionId}` | 세션별 출석 현황 | O |
| GET | `/calendar` | 출석 캘린더 조회 | O |
| POST | `/sessions/{sessionId}/beacon/start` | BLE 비콘 시작 (스터디장) | O |
| POST | `/sessions/{sessionId}/beacon/stop` | BLE 비콘 종료 (스터디장) | O |
| POST | `/sessions/{sessionId}/check` | BLE 출석 체크 (비콘 감지) | O |
| PUT | `/sessions/{sessionId}/members/{userId}/status` | 출석 상태 수동 변경 (스터디장) | O |
| POST | `/sessions/{sessionId}/self-check` | 셀프 출석 | O |
| GET | `/sessions/{sessionId}/self-check/status` | 셀프 출석 가능 여부 | O |
| POST | `/sessions/{sessionId}/excuse` | 결석 소명 작성 | O |
| PUT | `/sessions/{sessionId}/excuse/{excuseId}` | 소명 승인/거절 (스터디장) | O |
| GET | `/my` | 내 출석 현황 | O |
| GET | `/sessions/{sessionId}/memos` | 세션 메모 목록 조회 | O |
| POST | `/sessions/{sessionId}/memos` | 세션 메모 작성 | O |
| PUT | `/sessions/{sessionId}/memos` | 내 세션 메모 수정 | O |

---

## API 상세

### 1. 세션별 출석 현황

**Request**
```
GET /api/v1/studies/{studyId}/attendance/sessions/{sessionId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "sessionId": 1,
    "sessionNumber": 1,
    "scheduledAt": "2025-01-15T19:00:00Z",
    "attendanceList": [
      {
        "userId": 1,
        "nickname": "홍길동",
        "profileImage": "https://...",
        "status": "PRESENT",
        "checkType": "BLE",
        "checkedAt": "2025-01-15T19:02:00Z"
      },
      {
        "userId": 2,
        "nickname": "김싸피",
        "profileImage": "https://...",
        "status": "LATE",
        "checkType": "BLE",
        "checkedAt": "2025-01-15T19:20:00Z"
      },
      {
        "userId": 3,
        "nickname": "이싸피",
        "profileImage": "https://...",
        "status": "ABSENT",
        "checkType": null,
        "checkedAt": null,
        "excuseStatus": "PENDING"
      }
    ],
    "summary": {
      "total": 6,
      "present": 4,
      "late": 1,
      "absent": 1,
      "excused": 0,
      "attendanceRate": 83.3
    }
  }
}
```

---

### 2. 출석 캘린더 조회

**Request**
```
GET /api/v1/studies/{studyId}/attendance/calendar?year=2025&month=1
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "year": 2025,
    "month": 1,
    "myAttendance": [
      {
        "date": "2025-01-15",
        "sessionNumber": 1,
        "status": "PRESENT"
      },
      {
        "date": "2025-01-22",
        "sessionNumber": 2,
        "status": "ABSENT"
      }
    ],
    "summary": {
      "totalSessions": 2,
      "attended": 1,
      "attendanceRate": 50.0,
      "warningThreshold": 50.0,
      "isWarning": true
    }
  }
}
```

---

### 3. BLE 비콘 시작 (스터디장)

스터디장이 출석 체크를 시작하면 비콘 신호 발신에 필요한 정보를 반환합니다.

**Request**
```
POST /api/v1/studies/{studyId}/attendance/sessions/{sessionId}/beacon/start
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "sessionId": 5,
    "beaconUUID": "550e8400-e29b-41d4-a716-446655440000",
    "major": 1,
    "minor": 5,
    "signature": "encrypted_signature_for_verification",
    "expiresAt": "2025-01-15T20:00:00Z"
  },
  "message": "BLE 비콘을 시작합니다. 1시간 후 자동 종료됩니다."
}
```

**Error Response**
```json
{
  "success": false,
  "error": {
    "code": "BEACON_ALREADY_ACTIVE",
    "message": "이미 비콘이 활성화되어 있습니다."
  }
}
```

---

### 4. BLE 비콘 종료 (스터디장)

스터디장이 출석 체크를 종료합니다.

**Request**
```
POST /api/v1/studies/{studyId}/attendance/sessions/{sessionId}/beacon/stop
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "sessionId": 5,
    "totalChecked": 8,
    "duration": "00:45:30"
  },
  "message": "BLE 비콘이 종료되었습니다."
}
```

---

### 5. BLE 출석 체크 (스터디원 - 모바일)

스터디원의 앱이 스터디장의 BLE 비콘 신호를 감지하면 호출됩니다.

**Request**
```
POST /api/v1/studies/{studyId}/attendance/sessions/{sessionId}/check
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "beaconData": "encrypted_beacon_signature",
  "checkType": "BLE"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "userId": 2,
    "nickname": "김싸피",
    "profileImage": "https://...",
    "status": "PRESENT",
    "checkedAt": "2025-01-15T19:02:00Z",
    "message": "출석 완료!"
  }
}
```

**Response - 지각**
```json
{
  "success": true,
  "data": {
    "userId": 2,
    "nickname": "김싸피",
    "status": "LATE",
    "checkedAt": "2025-01-15T19:20:00Z",
    "message": "지각 처리되었습니다. (20분 초과)"
  }
}
```

---

### 6. 출석 상태 수동 변경 (스터디장)

스터디장이 멤버의 출석 상태를 수동으로 변경합니다.

**Request**
```
PUT /api/v1/studies/{studyId}/attendance/sessions/{sessionId}/members/{userId}/status
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "status": "PRESENT",
  "reason": "BLE 인식 오류로 수동 출석 처리"
}
```

| status | 설명 |
|--------|------|
| PRESENT | 출석 |
| LATE | 지각 |
| ABSENT | 결석 |
| EXCUSED | 공결 |

**Response**
```json
{
  "success": true,
  "data": {
    "userId": 3,
    "nickname": "이싸피",
    "previousStatus": "ABSENT",
    "newStatus": "PRESENT",
    "modifiedBy": "홍길동",
    "modifiedAt": "2025-01-15T19:30:00Z",
    "reason": "BLE 인식 오류로 수동 출석 처리"
  },
  "message": "출석 상태가 변경되었습니다."
}
```

**Error Response**
```json
{
  "success": false,
  "error": {
    "code": "NOT_STUDY_LEADER",
    "message": "스터디장만 출석 상태를 변경할 수 있습니다."
  }
}
```

---

### 7. 셀프 출석 가능 여부

**Request**
```
GET /api/v1/studies/{studyId}/attendance/sessions/{sessionId}/self-check/status
Authorization: Bearer {accessToken}
```

**Response - 셀프 출석 불가**
```json
{
  "success": true,
  "data": {
    "available": false,
    "reason": "스터디장이 출석 체크 중입니다.",
    "leaderLastActive": "2025-01-15T19:05:00Z"
  }
}
```

**Response - 셀프 출석 가능**
```json
{
  "success": true,
  "data": {
    "available": true,
    "reason": "스터디장 미접속 15분 경과로 셀프 출석이 활성화되었습니다.",
    "activatedAt": "2025-01-15T19:15:00Z"
  }
}
```

---

### 8. 셀프 출석

**Request**
```
POST /api/v1/studies/{studyId}/attendance/sessions/{sessionId}/self-check
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "status": "PRESENT",
    "checkType": "SELF",
    "checkedAt": "2025-01-15T19:16:00Z",
    "message": "셀프 출석 완료!"
  }
}
```

---

### 9. 결석 소명 작성

**Request**
```
POST /api/v1/studies/{studyId}/attendance/sessions/{sessionId}/excuse
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "reason": "갑작스러운 가족 행사로 참석하지 못했습니다."
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "excuseId": 1,
    "status": "PENDING",
    "createdAt": "2025-01-16T10:00:00Z"
  }
}
```

---

### 10. 소명 승인/거절 (스터디장)

**Request**
```
PUT /api/v1/studies/{studyId}/attendance/sessions/{sessionId}/excuse/{excuseId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "status": "APPROVED"
}
```

| status | 설명 |
|--------|------|
| APPROVED | 승인 (EXCUSED 처리) |
| REJECTED | 거절 (ABSENT 유지) |

**Response**
```json
{
  "success": true,
  "message": "소명이 승인되었습니다."
}
```

---

### 11. 내 출석 현황

**Request**
```
GET /api/v1/studies/{studyId}/attendance/my
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "totalSessions": 5,
    "attended": 4,
    "late": 1,
    "absent": 0,
    "excused": 0,
    "attendanceRate": 100.0,
    "history": [
      {
        "sessionNumber": 1,
        "scheduledAt": "2025-01-15T19:00:00Z",
        "status": "PRESENT",
        "checkedAt": "2025-01-15T19:02:00Z"
      },
      {
        "sessionNumber": 2,
        "scheduledAt": "2025-01-22T19:00:00Z",
        "status": "LATE",
        "checkedAt": "2025-01-22T19:15:00Z"
      }
    ]
  }
}
```

---

### 12. 세션 메모 목록 조회

오프라인 스터디에서 참석자들이 작성한 메모를 조회합니다. AI가 스터디 내용 파악에 활용합니다.

**Request**
```
GET /api/v1/studies/{studyId}/attendance/sessions/{sessionId}/memos
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "sessionId": 5,
    "sessionNumber": 3,
    "isOnline": false,
    "memos": [
      {
        "userId": 1,
        "nickname": "홍길동",
        "profileImage": "https://...",
        "content": "DP 문제 3개 풀이, 메모이제이션 개념 정리",
        "createdAt": "2025-01-15T21:00:00Z"
      },
      {
        "userId": 2,
        "nickname": "김싸피",
        "profileImage": "https://...",
        "content": "백준 1463번 풀이 공유, 점화식 세우는 법 학습",
        "createdAt": "2025-01-15T21:05:00Z"
      }
    ],
    "totalCount": 2
  }
}
```

---

### 13. 세션 메모 작성

**Request**
```
POST /api/v1/studies/{studyId}/attendance/sessions/{sessionId}/memos
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "content": "DP 문제 3개 풀이, 메모이제이션 개념 정리"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "memoId": 1,
    "content": "DP 문제 3개 풀이, 메모이제이션 개념 정리",
    "createdAt": "2025-01-15T21:00:00Z"
  },
  "message": "메모가 저장되었습니다."
}
```

---

### 14. 내 세션 메모 수정

**Request**
```
PUT /api/v1/studies/{studyId}/attendance/sessions/{sessionId}/memos
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "content": "DP 문제 3개 풀이 + 그리디 1문제 추가"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "memoId": 1,
    "content": "DP 문제 3개 풀이 + 그리디 1문제 추가",
    "updatedAt": "2025-01-15T21:30:00Z"
  },
  "message": "메모가 수정되었습니다."
}
```

---

## 모바일 BLE 관련

### BLE 비콘 방식
스터디장의 폰이 BLE 비콘 신호를 발신하고, 스터디원의 앱이 이를 감지하여 출석 체크합니다.

### BLE 출석 체크 흐름
1. 스터디장: "출석 시작" 버튼 → BLE 비콘 신호 발신 시작
2. 스터디원: 앱 실행 → BLE 스캔 모드 활성화
3. 스터디원 앱이 스터디장의 비콘 신호 감지
4. 자동으로 출석 체크 API 호출
5. 출석 기록 저장 → 양쪽에 결과 알림

### 비콘 데이터 구조
```json
{
  "studyId": 1,
  "sessionId": 5,
  "leaderId": 1,
  "timestamp": 1705341600,
  "signature": "encrypted_signature"
}
```

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| SESSION_NOT_FOUND | 세션을 찾을 수 없음 |
| ALREADY_CHECKED | 이미 출석 체크됨 |
| SELF_CHECK_NOT_AVAILABLE | 셀프 출석 불가능 |
| INVALID_BLE_TOKEN | 유효하지 않은 BLE 토큰 |
| BEACON_ALREADY_ACTIVE | 이미 비콘이 활성화됨 |
| BEACON_NOT_ACTIVE | 비콘이 활성화되지 않음 |
| NOT_STUDY_LEADER | 스터디장 권한 필요 |
| EXCUSE_ALREADY_EXISTS | 이미 소명 작성됨 |
| EXCUSE_NOT_FOUND | 소명을 찾을 수 없음 |
| MEMO_ALREADY_EXISTS | 이미 메모 작성됨 |
| MEMO_NOT_FOUND | 메모를 찾을 수 없음 |
| NOT_OFFLINE_SESSION | 오프라인 세션만 메모 가능 |
