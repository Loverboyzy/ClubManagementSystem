<p align="center">
  <h1 align="center">🎓 社团管理系统</h1>
  <p align="center">Club Management System — 前后端分离的全栈 Web 应用</p>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring_Boot-2.3.4-6DB33F?logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Vue-2.x-4FC08D?logo=vuedotjs&logoColor=white" />
  <img src="https://img.shields.io/badge/MyBatis_Plus-3.4.2-1E90FF" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/Element_UI-2.x-409EFF?logo=element&logoColor=white" />
  <img src="https://img.shields.io/badge/license-MIT-green" />
</p>

---

## ✨ 功能特性

| 模块 | 说明 |
|------|------|
| 🔐 用户认证 | 登录/退出/密码修改，Token 认证 + 频率限制 |
| 👥 用户管理 | 三级角色（系统管理员/社团管理员/普通成员）增删改查 |
| 📂 社团管理 | 创建/编辑/删除社团，按类型分类 |
| 👤 成员管理 | 成员列表、入团/退出、团长不可移除 |
| 📝 入团申请 | 提交申请 → 审批（通过/拒绝）→ 自动加入 |
| 🎯 活动管理 | 发布/编辑/删除活动，查看报名情况 |
| 📋 活动报名 | 报名活动、防重复报名、人数统计 |
| 📢 通知公告 | 系统通知 + 社团通知，按角色推送 |
| 💰 缴费管理 | 记录/查询缴费信息 |

### v2.0 增强

| 特性 | 说明 |
|------|------|
| 🛡️ 登录频率限制 | 同 IP 5 次/15 分钟，防暴力破解 |
| 🎨 UI 美化 | 渐变色登录页 + 表单校验增强 |
| 🔐 HTTP 拦截器 | 自动附带 Token + 401 自动跳转 |
| 🚦 路由守卫 | 未登录自动跳转，已登录免重复 |

---

## 🚀 快速开始

### 环境要求

| 软件 | 版本 |
|------|------|
| JDK | 1.8+ |
| MySQL | 8.0+ |
| Maven | 3.3+ |
| Node.js | 12+ |

### 1. 数据库

```sql
CREATE DATABASE association_manager CHARACTER SET utf8 COLLATE utf8_general_ci;
```

导入建表脚本：
```bash
mysql -u root -p association_manager < AssociationManagerApi/数据库脚本.sql
```

### 2. 后端

```bash
cd AssociationManagerApi
mvn spring-boot:run
# 启动在 http://localhost:9211/association
```

### 3. 前端

```bash
cd AssociationManagerVue
npm install
npm run serve
# 启动在 http://localhost:9212
```

### 默认账户

| 账号 | 密码 | 角色 |
|------|------|------|
| `superadmin` | `123456` | 系统管理员 |
| `admin` | `123456` | 社团管理员 |
| `user` | `123456` | 普通成员 |

---

## 📁 项目结构

```
社团管理系统/
├── AssociationManagerApi/    # Spring Boot 后端
│   ├── pom.xml
│   ├── 数据库脚本.sql
│   └── src/main/java/com/rabbiter/association/
│       ├── controller/       # 10 个 Controller
│       ├── service/          # 业务逻辑层
│       ├── dao/              # MyBatis-Plus DAO
│       ├── entity/           # 数据实体
│       └── utils/            # 工具类
├── AssociationManagerVue/    # Vue 2 前端
│   └── src/
│       ├── views/            # 页面组件
│       ├── router/           # 路由 + 守卫
│       ├── utils/            # HTTP 工具
│       └── api/              # API 封装
├── README.md
└── 用户使用文档.md
```

---

## 🏗️ 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 2.3.4 |
| ORM | MyBatis-Plus 3.4.2 |
| 数据库 | MySQL 8.0 |
| 缓存 | Ehcache 3.8 |
| 前端框架 | Vue 2 + Element UI |
| HTTP | Axios |

---

## 📄 许可证

[MIT License](./LICENSE)
