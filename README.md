<div align="center">

# PiDan Blog

**轻量级开源博客系统，前后端分离架构，开箱即用。**

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3-brightgreen.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5-blue.svg)](https://www.typescriptlang.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

</div>

---

## 目录

- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
  - [一键部署（推荐）](#一键部署推荐)
  - [源码构建部署](#源码构建部署)
  - [本地开发](#本地开发)
- [项目结构](#项目结构)
- [API 接口](#api-接口)
- [环境要求](#环境要求)
- [贡献指南](#贡献指南)
- [许可证](#许可证)

---

## 功能特性

**前台博客**
- 文章列表、详情页、分类筛选、标签筛选
- 归档时间线
- 评论系统（支持回复，需审核）
- 点赞
- RSS 订阅

**后台管理**
- 仪表盘（文章数、评论数、浏览量统计）
- 文章管理（Tiptap 富文本编辑器，支持 Markdown）
- 评论审核
- 分类管理、标签管理
- 站点设置（博客标题、描述等）
- 图片上传

**通用**
- 暗色模式
- 响应式设计
- JWT 认证（HTTP-only Cookie）
- Docker 一键部署

---

## 技术栈

| 层面 | 技术 | 说明 |
|------|------|------|
| 后端框架 | Spring Boot 3.2 + Java 17 | REST API |
| ORM | Spring Data JPA + Hibernate | 数据库操作 |
| 数据库 | PostgreSQL 15+ | 关系型数据库 |
| 认证 | Spring Security + JWT | HTTP-only Cookie |
| 前端框架 | Vue 3 + TypeScript + Vite | 响应式 UI |
| UI 样式 | Tailwind CSS | 暗色模式 + 响应式 |
| 富文本编辑器 | Tiptap (ProseMirror) | Markdown + 可视化 |
| 部署 | Docker Compose | 一键启动 |

---

## 快速开始

### 一键部署（推荐）

**1. 创建目录**

```bash
mkdir -p /opt/pidan-blog && cd /opt/pidan-blog
```

**2. 创建 `docker-compose.yml`**

按需修改注释标记 `←` 的部分：

```yaml
services:
  blog-server:
    image: ghcr.io/haohao8011/pidan-blog:latest
    ports:
      - "8080:8080"                    # ← 可改：左边是宿主机端口
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/pidan_blog
      - SPRING_DATASOURCE_USERNAME=pidan_blog   # ← 可改：数据库用户名
      - SPRING_DATASOURCE_PASSWORD=blog_password # ← 可改：数据库密码
    volumes:
      - uploads:/app/uploads           # 上传文件持久化
    restart: unless-stopped

  postgres:
    image: postgres:15-alpine
    volumes:
      - pgdata:/var/lib/postgresql/data # 数据库持久化
    environment:
      - POSTGRES_DB=pidan_blog          # ← 可改：数据库名
      - POSTGRES_USER=pidan_blog        # ← 可改：与上方 USERNAME 一致
      - POSTGRES_PASSWORD=blog_password # ← 可改：与上方 PASSWORD 一致
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U pidan_blog"]
      interval: 5s
      timeout: 3s
      retries: 5
    restart: unless-stopped

volumes:
  pgdata:                               # 数据库数据，勿删
  uploads:                              # 上传文件，勿删
```

**3. 启动**

```bash
docker compose up -d
```

启动后访问 `http://服务器IP:8080` 按引导页完成初始化。

**常用命令**

| 操作 | 命令 |
|------|------|
| 启动 | `docker compose up -d` |
| 停止 | `docker compose down` |
| 更新版本 | `docker compose pull && docker compose up -d` |
| 重新构建 | `docker compose up -d --build` |
| 查看日志 | `docker compose logs -f blog-server` |
| 停止并删除数据 | `docker compose down -v` |

> ⚠️ `docker compose down -v` 会删除数据库和上传文件，不可恢复。

---

### 源码构建部署

```bash
git clone https://github.com/haohao8011/pidan-blog.git
cd pidan-blog
docker compose up -d --build
```

---

### 本地开发

**环境要求：** Java 17+、Node.js 18+、PostgreSQL 15+

```bash
# 后端
cd blog-server
mvn spring-boot:run

# 前端（另开终端）
cd blog-web
pnpm install
pnpm dev
```

前端开发服务器：`http://localhost:5173`

---

## 项目结构

```
pidan-blog/
├── blog-server/                    # Spring Boot 后端
│   ├── src/
│   │   └── main/
│   │       ├── java/com/pidan/blog/
│   │       │   ├── common/         # 通用工具（BaseEntity、ApiResponse、SlugUtils）
│   │       │   ├── config/         # 应用配置（CORS、WebMvc）
│   │       │   ├── controller/
│   │       │   │   ├── admin/      # 后台接口（需认证）
│   │       │   │   └── pub/        # 公开接口
│   │       │   ├── dto/            # 请求/响应 DTO
│   │       │   ├── entity/         # JPA 实体
│   │       │   ├── exception/      # 全局异常处理
│   │       │   ├── repository/     # JPA Repository
│   │       │   ├── security/       # JWT 认证 + Spring Security
│   │       │   └── service/        # 业务逻辑
│   │       └── resources/
│   │           ├── application.yml
│   │           ├── db/migration/   # Flyway 迁移脚本
│   │           └── static/         # 前端构建产物
│   ├── src/test/
│   ├── uploads/
│   ├── Dockerfile
│   └── pom.xml
├── blog-web/                       # Vue 3 前端
│   ├── src/
│   │   ├── api/                    # 接口请求封装
│   │   ├── assets/                 # 静态资源
│   │   │   ├── fonts/
│   │   │   └── images/
│   │   ├── components/
│   │   │   ├── blog/               # 博客组件（Header、Footer、Layout、PostCard、Comment）
│   │   │   ├── console/            # 后台组件（DataTable、Modal、Sidebar）
│   │   │   └── ui/                 # 基础 UI（Button、Input、Badge、Pagination、Toast）
│   │   ├── composables/            # Vue 组合式函数
│   │   ├── router/                 # 路由配置
│   │   ├── stores/                 # Pinia 状态管理
│   │   ├── types/                  # TypeScript 类型定义
│   │   ├── utils/                  # 工具函数
│   │   └── views/
│   │       ├── blog/               # 前台页面
│   │       └── console/            # 后台页面
│   ├── public/
│   ├── index.html
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   └── tailwind.config.ts
├── docs/                           # 项目文档
│   ├── api/                        # API 接口文档
│   ├── database/                   # 数据库设计文档
│   ├── deployment/                 # 部署文档
│   ├── design/                     # 设计方案
│   ├── AUTHORS.md
│   └── CHANGELOG.md
├── scripts/                        # 开发脚本
│   ├── build-web.sh                # 前端构建脚本
│   └── db-backup.sh                # 数据库备份脚本
├── .github/
│   ├── workflows/
│   │   ├── ci.yml                  # CI 测试
│   │   └── docker.yml              # Docker 镜像构建
│   └── ISSUE_TEMPLATE/             # Issue 模板
├── docker-compose.yml
├── Dockerfile
├── .editorconfig
├── .dockerignore
├── .gitignore
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
├── SECURITY.md
├── LICENSE
└── README.md
```

---

## API 接口

### 公开接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/posts` | 文章列表（分页） |
| GET | `/api/posts/{slug}` | 文章详情 |
| POST | `/api/posts/{slug}/comments` | 提交评论 |
| POST | `/api/posts/{slug}/like` | 点赞 |
| GET | `/api/categories` | 分类列表 |
| GET | `/api/tags` | 标签列表 |
| GET | `/api/archive` | 归档时间线 |
| GET | `/api/settings` | 站点设置 |
| GET | `/api/health` | 健康检查 |

### 后台接口（需 JWT 认证）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/admin/auth/login` | 登录 |
| CRUD | `/api/admin/posts` | 文章管理 |
| CRUD | `/api/admin/categories` | 分类管理 |
| CRUD | `/api/admin/tags` | 标签管理 |
| GET/PUT/DELETE | `/api/admin/comments` | 评论管理 |
| GET/PUT | `/api/admin/settings` | 站点设置 |
| POST | `/api/admin/upload` | 文件上传 |
| GET | `/api/admin/dashboard` | 仪表盘统计 |

---

## 环境要求

| 环境 | 版本 |
|------|------|
| Java | 17+ |
| Node.js | 18+ |
| PostgreSQL | 15+ |
| Docker | 20+（可选） |

---

## 贡献指南

欢迎提交 Issue 和 Pull Request。

1. Fork 本仓库
2. 创建功能分支：`git checkout -b feature/xxx`
3. 提交更改：`git commit -m "feat: 添加 xxx"`
4. 推送分支：`git push origin feature/xxx`
5. 提交 Pull Request

详见 [CONTRIBUTING.md](CONTRIBUTING.md)

---

## 许可证

[MIT License](LICENSE)
