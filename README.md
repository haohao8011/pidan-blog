# pidan-blog

轻量级开源博客系统，前后端分离架构，开箱即用。

## 技术栈

- **后端：** Spring Boot 3.2 + Java 17 + PostgreSQL
- **前端：** Vue 3 + TypeScript + Vite + Tailwind CSS
- **编辑器：** Tiptap（富文本 + Markdown）
- **认证：** Spring Security + JWT（HTTP-only Cookie）
- **部署：** Docker Compose

## 快速开始

### 一键部署（推荐）

**1. 创建目录和配置文件**

```bash
mkdir -p /opt/pidan-blog && cd /opt/pidan-blog
```

**2. 创建 `docker-compose.yml`（按需修改注释标记的部分）**

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
      - SPRING_DATASOURCE_USERNAME=pidan_blog   # ← 可改：数据库用户名，需与下方保持一致
      - SPRING_DATASOURCE_PASSWORD=blog_password # ← 可改：数据库密码，需与下方保持一致
    volumes:
      - uploads:/app/uploads           # 上传文件持久化
    restart: unless-stopped

  postgres:
    image: postgres:15-alpine
    volumes:
      - pgdata:/var/lib/postgresql/data # 数据库持久化
    environment:
      - POSTGRES_DB=pidan_blog          # ← 可改：数据库名
      - POSTGRES_USER=pidan_blog        # ← 可改：与上方 USERNAME 保持一致
      - POSTGRES_PASSWORD=blog_password # ← 可改：与上方 PASSWORD 保持一致
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

访问 http://服务器IP:8080 按引导页完成初始化。

### 源码构建部署

```bash
git clone https://github.com/haohao8011/pidan-blog.git
cd pidan-blog
docker compose up -d --build
```

### 本地开发

```bash
# 后端（需要 PostgreSQL）
cd blog-server
mvn spring-boot:run

# 前端
cd blog-web
pnpm install
pnpm dev
```

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
│   │   ├── types/                  # TypeScript 类型（按模块拆分）
│   │   ├── utils/                  # 工具函数
│   │   └── views/
│   │       ├── blog/               # 前台页面（首页、文章详情、分类、标签、归档、关于）
│   │       └── console/            # 后台页面（仪表盘、文章管理、评论、分类、标签、设置）
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
│   ├── workflows/ci.yml            # GitHub Actions CI
│   └── ISSUE_TEMPLATE/             # Issue 模板
├── docker-compose.yml
├── .editorconfig
├── .dockerignore
├── .gitignore
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
├── SECURITY.md
├── LICENSE
└── README.md
```

## 功能

- 前台博客：文章列表、详情、分类、标签、归档、评论、点赞
- 后台管理：仪表盘、文章管理（Tiptap 编辑器）、评论审核、分类/标签管理、站点设置
- Markdown 内容同时存储原文和预渲染 HTML
- 暗色模式
- 图片上传
- RSS 订阅

## License

[MIT](LICENSE)
