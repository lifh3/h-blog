# 博客项目 - 技术文档

## 项目概述

技术博客系统，包含前台展示端和独立管理后台。

---

## 技术栈

| 模块 | 技术 |
|------|------|
| 前台博客 | Vue 3 + Vite + TailwindCSS |
| 管理后台 | Vue 3 + Vite + TailwindCSS |
| 后端 API | Spring Boot 4 + MyBatis Plus |
| 数据库 | MySQL 8 |
| 认证 | JWT + BCrypt |

---

## 初始账号密码

```
用户名：admin
密码：admin123
```

```
用户名：admin2
密码：admin123
```

> ⚠️ 建议首次登录后修改密码

---

## 启动方式

### 1. 后端（必须先启动）

```bash
cd C:\Users\22071\Desktop\project\blog\backend
mvn spring-boot:run
```

- 端口：`8080`
- 地址：`http://localhost:8080`

### 2. 前台博客

```bash
cd C:\Users\22071\Desktop\project\blog\frontend
npm run dev
```

- 端口：`3001`
- 地址：`http://localhost:3001`

### 3. 管理后台

```bash
cd C:\Users\22071\Desktop\project\blog\blog-admin
npm run dev
```

- 端口：`5173`
- 地址：`http://localhost:5173`

---

## API 接口

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/api/auth/login` | POST | 登录 | 否 |
| `/api/auth/register` | POST | 注册 | 否 |
| `/api/articles` | GET | 文章列表 | 否 |
| `/api/articles/{id}` | GET | 文章详情 | 否 |
| `/api/articles/hot` | GET | 热门文章 | 否 |
| `/api/categories` | GET | 分类列表 | 否 |
| `/api/tags` | GET | 标签列表 | 否 |
| `/api/collections` | GET | 合集列表 | 否 |
| `/api/stats` | GET | 统计信息 | 否 |
| `/api/comments/article/{id}` | GET | 评论列表 | 否 |

---

## 数据库

- 地址：`localhost:3306`
- 用户名：`root`
- 密码：`230802`
- 数据库名：`blog`

### 主要表

| 表名 | 说明 |
|------|------|
| `user` | 用户表 |
| `article` | 文章表 |
| `category` | 分类表 |
| `tag` | 标签表 |
| `article_tag` | 文章-标签关联表 |
| `comment` | 评论表 |
| `collection` | 合集表 |
| `article_collection` | 文章-合集关联表 |

---

## 功能清单

### 前台
- [x] 首页文章列表
- [x] 文章详情页
- [x] 分类筛选
- [x] 标签筛选
- [x] 热门文章
- [x] 合集列表 / 详情
- [x] 文章归档
- [x] 友链

### 管理后台
- [x] 登录 / 注册
- [x] 仪表盘统计
- [x] 文章管理（CRUD）
- [x] 分类管理（CRUD）
- [x] 标签管理（CRUD）
- [x] 评论管理（审核/删除）
- [x] 合集管理（CRUD）
- [x] 个人设置

---

## 项目结构

```
C:\Users\22071\Desktop\project\
├── blog/
│   ├── frontend/        # 前台博客 Vue 3
│   ├── backend/         # 后端 Spring Boot 4
│   └── blog-admin/      # 管理后台 Vue 3
└── blog-admin/          # 独立管理后台（备用）
```
