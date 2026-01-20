# ===========================================
# Recorder Server
# ===========================================
FROM node:20-alpine

WORKDIR /app

# 의존성 설치 (캐시)
COPY package*.json ./
RUN npm install --omit=dev

# 소스 복사
COPY src ./src

# 녹화 파일 저장 디렉토리
RUN mkdir -p /app/recordings

# 포트 노출
EXPOSE 3001

# 실행
CMD ["node", "src/index.js"]
