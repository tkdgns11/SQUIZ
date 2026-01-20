#!/bin/bash
#
# Squiz MySQL 데이터베이스 복원 스크립트
# 사용법: ./restore-db.sh <백업파일.sql.gz>
#

set -e

# ===========================================
# 설정
# ===========================================
CONTAINER_NAME="squiz-mysql"
DATABASE_NAME="squiz"
BACKUP_FILE="$1"

# 환경변수 로드
if [ -f /home/ubuntu/squiz/.env ]; then
    source /home/ubuntu/squiz/.env
fi

# ===========================================
# 함수 정의
# ===========================================

log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

error_exit() {
    log "ERROR: $1"
    exit 1
}

# ===========================================
# 검증
# ===========================================

if [ -z "$BACKUP_FILE" ]; then
    echo "사용법: $0 <백업파일.sql.gz>"
    echo ""
    echo "사용 가능한 백업 파일:"
    find /home/ubuntu/squiz/backups -name "*.sql.gz" -printf "  %p (%s bytes, %Tc)\n" 2>/dev/null | head -20
    exit 1
fi

if [ ! -f "$BACKUP_FILE" ]; then
    error_exit "백업 파일을 찾을 수 없습니다: $BACKUP_FILE"
fi

# Docker 컨테이너 실행 확인
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    error_exit "MySQL 컨테이너가 실행 중이 아닙니다: $CONTAINER_NAME"
fi

# ===========================================
# 복원 실행
# ===========================================

log "====== Squiz DB 복원 시작 ======"
log "백업 파일: $BACKUP_FILE"

# 확인 프롬프트
echo ""
echo "⚠️  경고: 현재 데이터베이스가 덮어쓰기됩니다!"
echo "복원할 파일: $BACKUP_FILE"
read -p "계속하시겠습니까? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    log "복원 취소됨"
    exit 0
fi

log "복원 중..."

# 압축 해제 후 MySQL로 복원
gunzip -c "$BACKUP_FILE" | docker exec -i "$CONTAINER_NAME" mysql \
    -u"${DB_USERNAME}" \
    -p"${DB_PASSWORD}" \
    2>/dev/null

if [ $? -eq 0 ]; then
    log "====== Squiz DB 복원 완료 ======"
else
    error_exit "복원 실패"
fi
