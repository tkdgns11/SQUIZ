# 인프라 구축 가이드

## 1. EC2 서버 정보

| 항목 | 값 |
|-----|-----|
| 도메인 | `i14d106.p.ssafy.io` |
| 사용자 | `ubuntu` |
| SSH 접속 | `ssh -i I14D106T.pem ubuntu@i14d106.p.ssafy.io` |

## 2. SSH 접속 방법

### Windows

```powershell
# PEM 파일 위치: C:\Users\{사용자명}\.ssh\I14D106T.pem
ssh -i C:\Users\{사용자명}\.ssh\I14D106T.pem ubuntu@i14d106.p.ssafy.io
```

### SSH Config 설정 (선택)

`~/.ssh/config` 파일:

```
Host squiz
    HostName i14d106.p.ssafy.io
    User ubuntu
    IdentityFile ~/.ssh/I14D106T.pem
```

이후 간단히 접속:

```bash
ssh squiz
```

## 3. UFW 방화벽 설정

### 현재 열린 포트

| 포트 | 프로토콜 | 용도 |
|-----|---------|------|
| 22 | TCP | SSH |
| 80 | TCP | HTTP |
| 443 | TCP | HTTPS |
| 40000-40100 | UDP | WebRTC (Mediasoup) |

### 포트 추가 명령어

```bash
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 40000:40100/udp
sudo ufw status
```

## 4. Docker 설치

```bash
# Docker 설치
sudo apt update
sudo apt install -y docker.io docker-compose

# Docker 권한 추가 (재접속 필요)
sudo usermod -aG docker ubuntu

# 확인
docker --version
docker-compose --version
```

## 5. 프로젝트 디렉토리 구조

```
/home/ubuntu/squiz/
├── docker-compose.yml
├── .env
├── backend/
│   └── Dockerfile
├── frontend/
├── rtc_mockup/
│   ├── sfu-server/
│   │   └── Dockerfile
│   └── recorder-server/
│       └── Dockerfile
└── nginx/
    ├── nginx.conf
    └── conf.d/
        └── default.conf
```

## 6. Docker Compose 서비스

| 서비스 | 컨테이너명 | 포트 | 설명 |
|-------|-----------|------|------|
| nginx | squiz-nginx | 80, 443 | 리버스 프록시 + 정적 파일 |
| backend | squiz-backend | 8080 | Spring Boot API |
| sfu-server | squiz-sfu | 3000 | Mediasoup SFU |
| recorder | squiz-recorder | 3001 | 녹화 서버 |
| mysql | squiz-mysql | 3306 | 데이터베이스 |
| redis | squiz-redis | 6379 | 세션/캐시 |

## 7. 환경변수 설정

`.env` 파일 생성:

```bash
cd /home/ubuntu/squiz
nano .env
```

내용:

```env
# Database
DB_ROOT_PASSWORD=your_secure_root_password
DB_USERNAME=squiz
DB_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your_jwt_secret_key_here
```

## 8. 수동 배포 방법

```bash
cd /home/ubuntu/squiz

# 컨테이너 중지
docker-compose down

# 이미지 빌드
docker-compose build --no-cache

# 컨테이너 시작
docker-compose up -d

# 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs -f
```

## 9. 문제 해결

### 컨테이너 로그 확인

```bash
# 전체 로그
docker-compose logs

# 특정 서비스 로그
docker-compose logs backend
docker-compose logs nginx

# 실시간 로그
docker-compose logs -f backend
```

### 컨테이너 재시작

```bash
docker-compose restart backend
```

### 컨테이너 쉘 접속

```bash
docker exec -it squiz-backend /bin/sh
docker exec -it squiz-mysql mysql -u root -p
```
