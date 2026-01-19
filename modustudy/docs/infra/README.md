# Squiz 인프라 문서

## 개요

Squiz 프로젝트의 인프라 및 DevOps 관련 문서입니다.

## 문서 목록

| 문서 | 설명 |
|-----|------|
| [01-infrastructure-setup.md](./01-infrastructure-setup.md) | EC2 서버 및 Docker 환경 구축 |
| [02-cicd-pipeline.md](./02-cicd-pipeline.md) | Jenkins CI/CD 파이프라인 |
| [03-testing-guide.md](./03-testing-guide.md) | 테스트 코드 작성 가이드 |
| [04-gradle-toolchain.md](./04-gradle-toolchain.md) | Gradle Toolchain (Java 21 자동 설정) |

## 인프라 구성도

```
┌─────────────────────────────────────────────────────────────┐
│                        AWS EC2                              │
│                   i14d106.p.ssafy.io                        │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                   Docker Compose                       │  │
│  │                                                        │  │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  │  │
│  │  │  Nginx  │  │ Backend │  │   SFU   │  │Recorder │  │  │
│  │  │  :80    │  │  :8080  │  │  :3000  │  │  :3001  │  │  │
│  │  │+Frontend│  │ (Spring)│  │(Mediasoup)│ │         │  │  │
│  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘  │  │
│  │                                                        │  │
│  │  ┌─────────┐  ┌─────────┐                             │  │
│  │  │  MySQL  │  │  Redis  │                             │  │
│  │  │  :3306  │  │  :6379  │                             │  │
│  │  └─────────┘  └─────────┘                             │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## CI/CD 흐름

```
GitLab Push
    ↓
GitLab Webhook 트리거
    ↓
Jenkins Pipeline 실행
    ↓
┌─────────────────────────────────┐
│ Stage 1: Checkout               │
│ - Git 저장소 클론               │
└─────────────────────────────────┘
    ↓
┌─────────────────────────────────┐
│ Stage 2: Backend Build          │
│ - Gradle Toolchain (Java 21)    │
│ - ./gradlew build -x test       │
└─────────────────────────────────┘
    ↓
┌─────────────────────────────────┐
│ Stage 3: Deploy to EC2          │
│ - rsync로 파일 전송             │
│ - docker-compose build          │
│ - docker-compose up -d          │
└─────────────────────────────────┘
    ↓
배포 완료
```

## 주요 설정 파일

| 파일 | 위치 | 설명 |
|-----|------|------|
| Jenkinsfile | `/Jenkinsfile` | Jenkins Pipeline 정의 |
| docker-compose.yml | `/modustudy/docker-compose.yml` | 컨테이너 오케스트레이션 |
| Backend Dockerfile | `/modustudy/backend/Dockerfile` | Spring Boot 빌드 |
| Frontend Dockerfile | `/modustudy/frontend/Dockerfile` | React 빌드 + Nginx |
| Nginx 설정 | `/modustudy/nginx/` | 리버스 프록시 설정 |
| build.gradle | `/modustudy/backend/build.gradle` | Gradle Toolchain 설정 |
| settings.gradle | `/modustudy/backend/settings.gradle` | Foojay Resolver 플러그인 |

## 진행 상황

### 완료

- [x] EC2 서버 초기 설정 (i14d106.p.ssafy.io)
- [x] UFW 방화벽 포트 설정 (22, 80, 443, 40000-40100/udp)
- [x] Docker 및 Docker Compose 설치
- [x] 프로젝트 디렉토리 생성 (/home/ubuntu/squiz)
- [x] SSH Config 설정
- [x] Dockerfile 작성 (Backend, Frontend, SFU, Recorder)
- [x] docker-compose.yml 작성
- [x] Nginx 설정 파일 작성
- [x] Gradle Toolchain 설정 (Java 21 자동 다운로드)
- [x] Jenkinsfile 작성 (Pipeline)
- [x] Jenkins Pipeline 프로젝트 생성
- [x] Jenkins Credentials 등록 (GitLab Token, EC2 PEM)
- [x] GitLab Webhook 연동

### 진행 중 / 해야 할 것

- [ ] **백엔드 테스트 코드 수정** (백엔드 팀원)
  - OAuth2ServiceTest: MySQL Mock 처리 필요
  - StudyRepositoryQueryDslTest: H2 호환 SQL 또는 @Disabled
  - QuizCourseControllerTest: JSON 응답 구조 확인
- [ ] **EC2 환경변수 설정** (.env 파일)
  - DB_USERNAME, DB_PASSWORD
  - DB_ROOT_PASSWORD
  - 기타 환경변수
- [ ] **첫 배포 테스트**
  - 테스트 수정 후 또는 테스트 스킵 후 배포 확인

### 예정

- [ ] SSL 인증서 발급 (Let's Encrypt)
- [ ] HTTPS 적용 및 HTTP 리다이렉트
- [ ] DB 초기화 스크립트 작성 (init.sql)
- [ ] DB 백업 스크립트 작성
- [ ] 모니터링 설정 (선택)

## 접속 정보

| 항목 | 값 |
|-----|-----|
| EC2 Host | i14d106.p.ssafy.io |
| SSH User | ubuntu |
| 배포 경로 | /home/ubuntu/squiz |
| SSH 접속 | `ssh squiz` (config 설정 시) |

## 트러블슈팅

### Jenkins에서 Java 21 없음
- **해결**: Gradle Toolchain + Foojay Resolver 플러그인 사용
- 상세: [04-gradle-toolchain.md](./04-gradle-toolchain.md)

### Jenkins에서 Node.js 16 (Vite 빌드 실패)
- **해결**: 프론트엔드 빌드를 EC2 Docker에서 수행
- Frontend Dockerfile에서 Node 20으로 빌드

### Jenkins Freestyle에서 SSH Credentials 바인딩 안됨
- **해결**: Pipeline 프로젝트로 전환
- `withCredentials([file(credentialsId: 'ec2-pem', ...)])` 사용
