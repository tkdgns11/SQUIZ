# ===========================================
# SFU Server (Mediasoup)
# ===========================================
FROM node:20-alpine

# Mediasoup 빌드에 필요한 패키지
RUN apk add --no-cache python3 make g++ linux-headers

WORKDIR /app

# 의존성 설치 (캐시)
COPY package*.json ./
RUN npm ci --only=production

# 소스 복사
COPY src ./src

# 포트 노출
EXPOSE 3000
EXPOSE 40000-40100/udp

# 실행
CMD ["node", "src/index.js"]
