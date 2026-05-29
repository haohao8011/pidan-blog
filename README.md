# pidan-blog

类 Halo 的开源博客系统，前后端分离架构。

## 技术栈

- **后端：** Spring Boot 3.2 + Java 17 + PostgreSQL
- **前端：** Vue 3 + TypeScript + Vite + Tailwind CSS
- **编辑器：** Tiptap（富文本 + Markdown）
- **认证：** Spring Security + JWT（HTTP-only Cookie）
- **部署：** Docker Compose

## 快速开始

### Docker Compose 部署

```bash
docker compose up -d --build
```

访问 http://localhost:8080 按引导页完成初始化。

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
blog/
├── blog-server/        # Spring Boot 后端
│   ├── src/main/java/  # Java 源码
│   ├── Dockerfile
│   └── pom.xml
├── blog-web/           # Vue 3 前端
│   ├── src/            # Vue 源码
│   └── package.json
├── docs/               # 项目文档
├── scripts/            # 开发脚本
├── docker-compose.yml
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
