#!/bin/bash
#
# Squiz MySQL 데이터베이스 백업 스크립트
# 사용법: ./backup-db.sh [daily|weekly|manual]
#

set -e

# ===========================================
# 설정
# ===========================================
BACKUP_DIR="/home/ubuntu/squiz/backups"
CONTAINER_NAME="squiz-mysql"
DATABASE_NAME="squiz"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_TYPE="${1:-manual}"

# 보관 기간 설정
DAILY_KEEP_DAYS=7      # 일간 백업 7일 보관
WEEKLY_KEEP_DAYS=30    # 주간 백업 30일 보관
MANUAL_KEEP_DAYS=90    # 수동 백업 90일 보관

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

# 백업 디렉토리 생성
create_backup_dir() {
    mkdir -p "$BACKUP_DIR/$BACKUP_TYPE"
    log "백업 디렉토리 확인: $BACKUP_DIR/$BACKUP_TYPE"
}

# MySQL 백업 실행
backup_mysql() {
    local backup_file="$BACKUP_DIR/$BACKUP_TYPE/${DATABASE_NAME}_${BACKUP_TYPE}_${DATE}.sql.gz"

    log "MySQL 백업 시작: $DATABASE_NAME"

    # Docker 컨테이너 실행 확인
    if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        error_exit "MySQL 컨테이너가 실행 중이 아닙니다: $CONTAINER_NAME"
    fi

    # mysqldump 실행 및 압축
    docker exec "$CONTAINER_NAME" mysqldump \
        -u"${DB_USERNAME}" \
        -p"${DB_PASSWORD}" \
        --single-transaction \
        --routines \
        --triggers \
        --databases "$DATABASE_NAME" \
        2>/dev/null | gzip > "$backup_file"

    if [ $? -eq 0 ] && [ -s "$backup_file" ]; then
        local size=$(du -h "$backup_file" | cut -f1)
        log "백업 완료: $backup_file ($size)"
    else
        rm -f "$backup_file"
        error_exit "백업 실패"
    fi
}

# 오래된 백업 삭제
cleanup_old_backups() {
    local keep_days

    case "$BACKUP_TYPE" in
        daily)  keep_days=$DAILY_KEEP_DAYS ;;
        weekly) keep_days=$WEEKLY_KEEP_DAYS ;;
        manual) keep_days=$MANUAL_KEEP_DAYS ;;
        *)      keep_days=$MANUAL_KEEP_DAYS ;;
    esac

    log "오래된 백업 정리 (${keep_days}일 이상)"

    local deleted_count=$(find "$BACKUP_DIR/$BACKUP_TYPE" -name "*.sql.gz" -mtime +$keep_days -delete -print | wc -l)

    if [ "$deleted_count" -gt 0 ]; then
        log "삭제된 백업 파일: ${deleted_count}개"
    fi
}

# 백업 목록 출력
list_backups() {
    log "현재 백업 목록:"
    echo "=========================================="

    for type in daily weekly manual; do
        if [ -d "$BACKUP_DIR/$type" ]; then
            local count=$(find "$BACKUP_DIR/$type" -name "*.sql.gz" 2>/dev/null | wc -l)
            local size=$(du -sh "$BACKUP_DIR/$type" 2>/dev/null | cut -f1)
            echo "[$type] ${count}개 파일, 총 ${size:-0}"
        fi
    done

    echo "=========================================="
}

# ===========================================
# 메인 실행
# ===========================================

log "====== Squiz DB 백업 시작 ======"
log "백업 타입: $BACKUP_TYPE"

create_backup_dir
backup_mysql
cleanup_old_backups
list_backups

log "====== Squiz DB 백업 완료 ======"
