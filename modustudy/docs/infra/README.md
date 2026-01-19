# Squiz 인프라 문서

## 개요

Squiz 프로젝트의 인프라 및 DevOps 관련 문서입니다.

## 문서 목록

| 문서 | 설명 |
|-----|------|
| [01-infrastructure-setup.md](./01-infrastructure-setup.md) | EC2 서버 및 Docker 환경 구축 |
| [02-cicd-pipeline.md](./02-cicd-pipeline.md) | Jenkins CI/CD 파이프라인 |
| [03-testing-guide.md](./03-testing-guide.md) | 테스트 코드 작성 가이드 |

## 인프라 구성도

```
┌─────────────────────────────────────────────────────────────┐
│                        AWS EC2                              │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                   Docker Compose                       │  │
│  │                                                        │  │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  │  │
│  │  │  Nginx  │  │ Backend │  │   SFU   │  │Recorder │  │  │
│  │  │  :80    │  │  :8080  │  │  :3000  │  │  :3001  │  │  │
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
GitLab Push → Webhook → Jenkins → Test → Build → Deploy to EC2
```

## 진행 상황

### 완료

- [x] EC2 서버 초기 설정
- [x] UFW 방화벽 포트 설정
- [x] Docker 및 Docker Compose 설치
- [x] Dockerfile 작성 (Backend, SFU, Recorder)
- [x] docker-compose.yml 작성
- [x] Nginx 설정 파일 작성
- [x] Jenkinsfile 작성
- [x] Jenkins Credentials 등록
- [x] GitLab Webhook 연동

### 진행 중

- [ ] .env 환경변수 설정
- [ ] 테스트 코드 작성
- [ ] 첫 배포 테스트

### 예정

- [ ] SSL 인증서 발급 (Let's Encrypt)
- [ ] DB 초기화 스크립트 작성
- [ ] 모니터링 설정
