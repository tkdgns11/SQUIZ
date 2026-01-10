# 인증 API (Auth)

## 기본 정보
- Base URL: `/api/v1/auth`
- 인증: 일부 엔드포인트 제외하고 JWT 필요

---

## 엔드포인트 목록

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/oauth/{provider}` | 소셜 로그인 URL 요청 | X |
| POST | `/oauth/{provider}/callback` | 소셜 로그인 콜백 처리 | X |
| POST | `/login` | 이메일+비밀번호 로그인 | X |
| POST | `/token/refresh` | Access Token 갱신 | O |
| POST | `/logout` | 로그아웃 | O |
| POST | `/password` | 비밀번호 설정 (최초) | O |
| PUT | `/password` | 비밀번호 변경 | O |
| GET | `/social/my` | 연동된 소셜 계정 목록 조회 | O |
| POST | `/social/{provider}/link` | 소셜 계정 연동 추가 | O |
| DELETE | `/social/{provider}` | 소셜 계정 연동 해제 | O |
| GET | `/org/{orgType}/verify` | 소속 인증 URL 요청 | O |
| POST | `/org/{orgType}/verify/callback` | 소속 인증 콜백 처리 | O |
| GET | `/org/my` | 내 소속 인증 목록 조회 | O |
| DELETE | `/org/{orgId}` | 소속 인증 해제 | O |

---

## API 상세

### 1. 소셜 로그인 URL 요청
소셜 로그인 인증 페이지로 리다이렉트할 URL을 반환합니다.

**Request**
```
GET /api/v1/auth/oauth/{provider}
```

| Parameter | Type | 필수 | 설명 |
|-----------|------|------|------|
| provider | string | O | google / kakao / naver |

**Response**
```json
{
  "success": true,
  "data": {
    "authUrl": "https://accounts.google.com/o/oauth2/v2/auth?client_id=...&redirect_uri=...&response_type=code&scope=..."
  }
}
```

---

### 2. 소셜 로그인 콜백 처리
소셜 로그인 인증 후 콜백을 처리하고 JWT 토큰을 발급합니다.

**Request**
```
POST /api/v1/auth/oauth/{provider}/callback
Content-Type: application/json
```
```json
{
  "code": "authorization_code_here"
}
```

| Parameter | Type | 필수 | 설명 |
|-----------|------|------|------|
| provider | string | O | google / kakao / naver |

**Response - 기존 회원**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 3600,
    "isNewUser": false,
    "user": {
      "id": 1,
      "email": "hong@gmail.com",
      "nickname": "홍길동",
      "profileImage": "https://...",
      "loginProvider": "GOOGLE"
    }
  }
}
```

**Response - 신규 회원**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 3600,
    "isNewUser": true,
    "user": {
      "id": 1,
      "email": "hong@gmail.com",
      "nickname": null,
      "profileImage": null,
      "loginProvider": "GOOGLE"
    }
  }
}
```

> 신규 회원의 경우 닉네임 설정이 필요합니다. (프로필 설정 API 사용)

---

### 3. 이메일+비밀번호 로그인
이메일과 비밀번호로 로그인합니다.

**Request**
```
POST /api/v1/auth/login
Content-Type: application/json
```
```json
{
  "email": "hong@gmail.com",
  "password": "password123!"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "email": "hong@gmail.com",
      "nickname": "홍길동",
      "profileImage": "https://...",
      "loginProvider": "EMAIL"
    }
  }
}
```

**Error Response**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "이메일 또는 비밀번호가 일치하지 않습니다."
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "PASSWORD_NOT_SET",
    "message": "비밀번호가 설정되지 않은 계정입니다. 소셜 로그인을 이용해주세요."
  }
}
```

---

### 4. Access Token 갱신
Refresh Token으로 새로운 Access Token을 발급받습니다.

**Request**
```
POST /api/v1/auth/token/refresh
Content-Type: application/json
```
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 3600
  }
}
```

**Error Response**
```json
{
  "success": false,
  "error": {
    "code": "TOKEN_EXPIRED",
    "message": "Refresh token has expired"
  }
}
```

---

### 5. 로그아웃
현재 세션을 종료하고 토큰을 무효화합니다.

**Request**
```
POST /api/v1/auth/logout
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "로그아웃되었습니다."
}
```

---

### 6. 비밀번호 설정 (최초)
소셜 로그인 사용자가 비밀번호를 설정합니다.

**Request**
```
POST /api/v1/auth/password
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "password": "newPassword123!",
  "passwordConfirm": "newPassword123!"
}
```

**Response**
```json
{
  "success": true,
  "message": "비밀번호가 설정되었습니다."
}
```

**Error Response**
```json
{
  "success": false,
  "error": {
    "code": "PASSWORD_ALREADY_SET",
    "message": "이미 비밀번호가 설정되어 있습니다. 비밀번호 변경을 이용해주세요."
  }
}
```

---

### 7. 비밀번호 변경
기존 비밀번호를 변경합니다.

**Request**
```
PUT /api/v1/auth/password
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "currentPassword": "oldPassword123!",
  "newPassword": "newPassword456!",
  "newPasswordConfirm": "newPassword456!"
}
```

**Response**
```json
{
  "success": true,
  "message": "비밀번호가 변경되었습니다."
}
```

**Error Response**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_PASSWORD",
    "message": "현재 비밀번호가 일치하지 않습니다."
  }
}
```

---

### 8. 연동된 소셜 계정 목록 조회
현재 사용자에게 연동된 소셜 계정 목록을 조회합니다.

**Request**
```
GET /api/v1/auth/social/my
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": {
    "linkedAccounts": [
      {
        "provider": "GOOGLE",
        "email": "hong@gmail.com",
        "linkedAt": "2025-01-10T10:00:00Z"
      },
      {
        "provider": "KAKAO",
        "email": "hong@kakao.com",
        "linkedAt": "2025-01-15T14:30:00Z"
      }
    ],
    "hasPassword": true
  }
}
```

---

### 9. 소셜 계정 연동 추가
새로운 소셜 계정을 연동합니다.

**Request**
```
POST /api/v1/auth/social/{provider}/link
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "code": "authorization_code_here"
}
```

| Parameter | Type | 필수 | 설명 |
|-----------|------|------|------|
| provider | string | O | google / kakao / naver |

**Response**
```json
{
  "success": true,
  "data": {
    "provider": "NAVER",
    "email": "hong@naver.com",
    "linkedAt": "2025-01-17T10:00:00Z"
  },
  "message": "네이버 계정이 연동되었습니다."
}
```

**Error Response**
```json
{
  "success": false,
  "error": {
    "code": "ALREADY_LINKED",
    "message": "이미 해당 소셜 계정이 연동되어 있습니다."
  }
}
```

```json
{
  "success": false,
  "error": {
    "code": "ACCOUNT_EXISTS",
    "message": "해당 소셜 계정은 다른 사용자에게 연동되어 있습니다."
  }
}
```

---

### 10. 소셜 계정 연동 해제
연동된 소셜 계정을 해제합니다.

**Request**
```
DELETE /api/v1/auth/social/{provider}
Authorization: Bearer {accessToken}
```

| Parameter | Type | 필수 | 설명 |
|-----------|------|------|------|
| provider | string | O | google / kakao / naver |

**Response**
```json
{
  "success": true,
  "message": "구글 계정 연동이 해제되었습니다."
}
```

**Error Response**
```json
{
  "success": false,
  "error": {
    "code": "CANNOT_UNLINK",
    "message": "최소 1개의 로그인 수단이 필요합니다. 비밀번호를 설정하거나 다른 소셜 계정을 연동해주세요."
  }
}
```

---

### 11. 소속 인증 URL 요청
소속 기관 OAuth 인증 페이지로 리다이렉트할 URL을 반환합니다.

**Request**
```
GET /api/v1/auth/org/{orgType}/verify
Authorization: Bearer {accessToken}
```

| Parameter | Type | 필수 | 설명 |
|-----------|------|------|------|
| orgType | string | O | SSAFY / NBC / WTC 등 |

**Response**
```json
{
  "success": true,
  "data": {
    "authUrl": "https://project.ssafy.com/oauth/sso-check?client_id=...&redirect_uri=..."
  }
}
```

---

### 12. 소속 인증 콜백 처리
소속 기관 OAuth 인증 완료 후 정보를 저장합니다.

**Request**
```
POST /api/v1/auth/org/{orgType}/verify/callback
Authorization: Bearer {accessToken}
Content-Type: application/json
```
```json
{
  "code": "authorization_code_here"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "orgType": "SSAFY",
    "orgData": {
      "userId": "ssafy_user_123",
      "name": "홍길동",
      "email": "hong@ssafy.com",
      "resno": "1412345",
      "generation": 14,
      "campus": "서울",
      "retireYn": "N"
    },
    "verifiedAt": "2025-01-17T10:00:00Z",
    "lastCheckedAt": "2025-01-17T10:00:00Z",
    "isActive": true
  },
  "message": "SSAFY 인증이 완료되었습니다."
}
```

---

### 13. 내 소속 인증 목록 조회
현재 사용자의 소속 인증 목록을 조회합니다.

**Request**
```
GET /api/v1/auth/org/my
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "orgType": "SSAFY",
      "orgData": {
        "generation": 14,
        "campus": "서울",
        "retireYn": "N"
      },
      "verifiedAt": "2025-01-17T10:00:00Z",
      "lastCheckedAt": "2025-01-17T12:00:00Z",
      "isActive": true
    }
  ]
}
```

---

### 14. 소속 인증 해제
소속 인증을 해제합니다.

**Request**
```
DELETE /api/v1/auth/org/{orgId}
Authorization: Bearer {accessToken}
```

**Response**
```json
{
  "success": true,
  "message": "소속 인증이 해제되었습니다."
}
```

---

## 지원 OAuth Provider

| Provider | 설명 |
|----------|------|
| google | Google OAuth 2.0 |
| kakao | Kakao OAuth 2.0 |
| naver | Naver OAuth 2.0 |

---

## 에러 코드

| 코드 | 설명 |
|------|------|
| INVALID_PROVIDER | 지원하지 않는 OAuth 제공자 |
| INVALID_AUTH_CODE | 유효하지 않은 인증 코드 |
| TOKEN_EXPIRED | 토큰 만료 |
| INVALID_TOKEN | 유효하지 않은 토큰 |
| OAUTH_FAILED | OAuth 인증 실패 |
| INVALID_CREDENTIALS | 이메일 또는 비밀번호 불일치 |
| PASSWORD_NOT_SET | 비밀번호 미설정 계정 |
| PASSWORD_ALREADY_SET | 이미 비밀번호 설정됨 |
| INVALID_PASSWORD | 현재 비밀번호 불일치 |
| ALREADY_LINKED | 이미 연동된 소셜 계정 |
| ACCOUNT_EXISTS | 다른 사용자에게 연동된 소셜 계정 |
| CANNOT_UNLINK | 최소 1개 로그인 수단 필요 |
