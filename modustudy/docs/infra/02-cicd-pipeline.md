# CI/CD 파이프라인 가이드

## 1. 개요

GitLab Runner를 사용한 CI/CD 파이프라인입니다.

```
GitLab Push (master) → GitLab Runner → Build → Test → Deploy
```

## 2. GitLab Runner 정보

| 항목 | 값 |
|-----|-----|
| 실행 환경 | EC2 (i14d106.p.ssafy.io) |
| Executor | shell |
| 등록 태그 | docker |

## 3. 파이프라인 단계

```
┌──────────┐   ┌──────────┐   ┌──────────┐
│  Build   │ → │   Test   │ → │  Deploy  │
└──────────┘   └──────────┘   └──────────┘
      ↓              ↓              ↓
 빌드 실패 시     테스트 실패 시    배포 완료
 파이프라인 중단  파이프라인 중단
```

### Stage 1: Build

- Gradle을 사용한 백엔드 빌드
- 테스트 제외 (`-x test`)
- 빌드 실패 시 파이프라인 중단

### Stage 2: Test

- Gradle 테스트 실행
- **테스트 실패 시 Deploy 단계로 진행하지 않음**
- JUnit 테스트 리포트 생성

### Stage 3: Deploy

- rsync로 파일 복사
- Docker Compose로 컨테이너 재시작
- 이전 이미지 정리

## 4. .gitlab-ci.yml

```yaml
stages:
  - build
  - test
  - deploy

variables:
  GRADLE_OPTS: "-Dorg.gradle.daemon=false"
  DEPLOY_PATH: "/home/ubuntu/squiz"

build:
  stage: build
  script:
    - cd modustudy/backend
    - chmod +x gradlew
    - ./gradlew build -x test
  only:
    - master
    - main
  tags:
    - docker

test:
  stage: test
  script:
    - cd modustudy/backend
    - ./gradlew test
  only:
    - master
    - main
  tags:
    - docker

deploy:
  stage: deploy
  script:
    - rsync -av --delete --exclude '.git' --exclude 'node_modules' --exclude '.gradle' --exclude 'build' modustudy/ $DEPLOY_PATH/
    - cd $DEPLOY_PATH
    - docker-compose down || true
    - docker-compose up -d --build
    - docker image prune -f
    - docker-compose ps
  only:
    - master
    - main
  tags:
    - docker
```

## 5. 배포 트리거

### 자동 배포

`master` 브랜치에 Push하면 자동으로 빌드/테스트/배포가 실행됩니다.

### 파이프라인 확인

GitLab → CI/CD → Pipelines 에서 확인

## 6. 빌드 실패 시 대응

### 테스트 실패

1. GitLab Pipeline 로그 확인
2. 실패한 테스트 케이스 확인
3. 로컬에서 테스트 실행하여 수정

```bash
cd modustudy/backend
./gradlew test
```

### 빌드 실패

1. GitLab Pipeline 콘솔 로그 확인
2. 로컬에서 빌드 테스트

```bash
cd modustudy/backend
./gradlew build -x test
```

### 배포 실패

1. EC2 SSH 접속하여 로그 확인

```bash
ssh -i ~/.ssh/I14D106T.pem ubuntu@i14d106.p.ssafy.io
cd /home/ubuntu/squiz
docker-compose logs -f
```

## 7. GitLab Runner 관리

### Runner 상태 확인

```bash
sudo gitlab-runner status
```

### Runner 재시작

```bash
sudo gitlab-runner restart
```

### Runner 로그 확인

```bash
sudo journalctl -u gitlab-runner -f
```

## 8. 브랜치 전략

| 브랜치 | 용도 | 자동 배포 |
|-------|------|----------|
| master | 운영 배포 | O |
| develop | 개발 통합 | X |
| feature/* | 기능 개발 | X |

### 권장 워크플로우

1. `feature/기능명` 브랜치 생성
2. 개발 및 테스트 코드 작성
3. 로컬에서 테스트 통과 확인
4. `master`로 Merge Request
5. 코드 리뷰 후 Merge
6. 자동 배포 실행
