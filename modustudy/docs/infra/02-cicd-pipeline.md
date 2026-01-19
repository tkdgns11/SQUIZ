# CI/CD 파이프라인 가이드

## 1. 개요

Jenkins를 사용한 CI/CD 파이프라인입니다.

```
GitLab Push → Webhook → Jenkins → Test → Build → Deploy
```

## 2. Jenkins 정보

| 항목 | 값 |
|-----|-----|
| URL | `https://jenkins-1.ssafy.com` |
| 프로젝트 | `s14-webmobile1-sub2/S14P12D106` |

## 3. 파이프라인 단계

```
┌──────────┐   ┌──────────────┐   ┌────────────────┐   ┌──────────┐
│ Checkout │ → │ Backend Test │ → │ Frontend Build │ → │  Deploy  │
└──────────┘   └──────────────┘   └────────────────┘   └──────────┘
```

### Stage 1: Checkout

- GitLab에서 소스 코드 가져오기

### Stage 2: Backend Test

- Gradle을 사용한 단위 테스트 실행
- 테스트 실패 시 빌드 중단
- 테스트 결과 JUnit 리포트 생성

### Stage 3: Frontend Build

- npm 의존성 설치
- Vite 빌드 실행
- 빌드 실패 시 배포 중단

### Stage 4: Deploy

- rsync로 EC2에 파일 전송
- Docker Compose로 컨테이너 재시작

## 4. Jenkinsfile

```groovy
pipeline {
    agent any

    environment {
        EC2_HOST = 'i14d106.p.ssafy.io'
        EC2_USER = 'ubuntu'
        DEPLOY_PATH = '/home/ubuntu/squiz'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Backend Test') {
            steps {
                dir('modustudy/backend') {
                    sh '''
                        chmod +x gradlew
                        ./gradlew test --no-daemon
                    '''
                }
            }
        }

        stage('Frontend Build') {
            steps {
                dir('modustudy/frontend') {
                    sh '''
                        npm ci
                        npm run build
                    '''
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                sshagent(credentials: ['ec2-ssh-key']) {
                    // 배포 스크립트
                }
            }
        }
    }
}
```

## 5. 배포 트리거

### 자동 배포 (Webhook)

GitLab에서 `master` 브랜치에 Push하면 자동으로 배포됩니다.

### 수동 배포

Jenkins 대시보드에서 "Build Now" 클릭

## 6. Jenkins Credentials

| ID | 종류 | 용도 |
|----|------|------|
| `ec2-ssh-key` | SSH Username with private key | EC2 SSH 접속 |

### Credentials 등록 방법

1. Jenkins → Manage Jenkins → Credentials
2. Add Credentials
3. Kind: SSH Username with private key
4. ID: `ec2-ssh-key`
5. Username: `ubuntu`
6. Private Key: PEM 파일 내용 붙여넣기

## 7. GitLab Webhook 설정

| 항목 | 값 |
|-----|-----|
| URL | `https://jenkins-1.ssafy.com/project/s14-webmobile1-sub2/S14P12D106` |
| Trigger | Push events |
| SSL | Enable |

## 8. 빌드 실패 시 대응

### 테스트 실패

1. Jenkins 빌드 로그 확인
2. 실패한 테스트 케이스 확인
3. 로컬에서 테스트 실행하여 수정

```bash
cd modustudy/backend
./gradlew test
```

### 빌드 실패

1. Jenkins 콘솔 로그 확인
2. 로컬에서 빌드 테스트

```bash
cd modustudy/frontend
npm ci
npm run build
```

### 배포 실패

1. EC2 SSH 접속하여 로그 확인

```bash
ssh squiz
cd /home/ubuntu/squiz
docker-compose logs -f
```

## 9. 브랜치 전략

| 브랜치 | 용도 | 자동 배포 |
|-------|------|----------|
| master | 운영 배포 | ✅ |
| develop | 개발 통합 | ❌ (선택) |
| feature/* | 기능 개발 | ❌ |

### 권장 워크플로우

1. `feature/기능명` 브랜치 생성
2. 개발 및 테스트 코드 작성
3. 로컬에서 테스트 통과 확인
4. `master`로 Merge Request
5. 코드 리뷰 후 Merge
6. 자동 배포 실행
