# 배포 가이드 (팀원용)

## 개요

master 브랜치에 push하면 GitLab Runner가 자동으로 빌드, 테스트, 배포를 수행합니다.

## 자동 배포 (권장)

### 1. 코드 변경 후 Push

```bash
git add .
git commit -m "커밋 메시지"
git push origin master
```

### 2. 파이프라인 확인

GitLab → CI/CD → Pipelines 에서 진행 상황 확인

### 3. 배포 완료

- Build → Test → Deploy 순서로 실행
- **테스트 실패 시 배포되지 않음**

---

## 수동 배포 (자동 배포 실패 시)

### 사전 준비

1. **PEM 키 파일** 필요: `I14D106T.pem`
2. PEM 파일을 `C:\Users\{사용자명}\.ssh\` 폴더에 복사

### 1단계: 파일 전송 (로컬 CMD에서)

```cmd
cd C:\SSAFY\d106\S14P11D106

scp -r -i %USERPROFILE%\.ssh\I14D106T.pem modustudy\* ubuntu@i14d106.p.ssafy.io:/home/ubuntu/squiz/
```

### 2단계: EC2 접속

```cmd
ssh -i %USERPROFILE%\.ssh\I14D106T.pem ubuntu@i14d106.p.ssafy.io
```

### 3단계: Docker 재시작

```bash
cd /home/ubuntu/squiz

# 전체 재시작
docker-compose down
docker-compose up -d --build

# 상태 확인
docker ps
```

---

## 서비스별 재배포

### Backend만 재배포

```bash
docker-compose build --no-cache backend
docker-compose up -d backend
```

### Frontend만 재배포

```bash
docker-compose build --no-cache nginx
docker-compose up -d nginx
```

### SFU만 재배포

```bash
docker-compose build --no-cache sfu-server
docker-compose up -d sfu-server
```

---

## 로그 확인

```bash
# 전체 로그
docker-compose logs -f

# 특정 서비스 로그
docker logs -f squiz-backend
docker logs -f squiz-nginx
docker logs -f squiz-sfu
```

---

## 상태 확인

```bash
# 컨테이너 상태
docker ps

# 컨테이너 리소스 사용량
docker stats
```

---

## 접속 정보

| 항목 | 값 |
|------|-----|
| 웹사이트 | http://i14d106.p.ssafy.io |
| EC2 Host | i14d106.p.ssafy.io |
| SSH User | ubuntu |
| 배포 경로 | /home/ubuntu/squiz |

---

## 주의사항

1. **master 브랜치에만 배포** - 다른 브랜치는 로컬에서 테스트
2. **배포 전 로컬 테스트 확인** - `./gradlew test`
3. **.env 파일 Git에 올리지 않기** - 비밀번호 포함
4. **PEM 파일 공유 금지** - 팀원에게만 안전하게 전달
