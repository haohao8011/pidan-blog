#!/bin/bash
# 构建前端并复制到后端 static 目录

set -e

cd "$(dirname "$0")/../blog-web"
pnpm install
pnpm build

cp -r dist/* ../blog-server/src/main/resources/static/

echo "前端构建完成，已复制到 blog-server/src/main/resources/static/"
