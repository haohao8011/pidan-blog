#!/bin/bash
# 数据库备份脚本（Docker 环境）

set -e

BACKUP_DIR="$(dirname "$0")/../backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
CONTAINER_NAME="blog-postgres-1"

mkdir -p "$BACKUP_DIR"

docker exec "$CONTAINER_NAME" pg_dump -U pidan_blog pidan_blog > "$BACKUP_DIR/pidan_blog_${TIMESTAMP}.sql"

echo "备份完成: $BACKUP_DIR/pidan_blog_${TIMESTAMP}.sql"
