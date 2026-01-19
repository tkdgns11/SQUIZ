# ===========================================
# Stage 1: Build
# ===========================================
FROM node:20-alpine AS builder

WORKDIR /app

# 의존성 파일 복사
COPY package*.json ./

# 의존성 설치
RUN npm ci

# 소스 코드 복사
COPY . .

# 빌드
RUN npm run build

# ===========================================
# Stage 2: Production (Nginx)
# ===========================================
FROM nginx:alpine

# 빌드된 파일 복사
COPY --from=builder /app/dist /usr/share/nginx/html

# Nginx 설정 (필요시)
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
