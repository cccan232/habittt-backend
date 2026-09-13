# Habittt - 游戏化习惯养成系统

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Vue](https://img.shields.io/badge/Vue-3.x-green.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)

## 项目简介

Habittt 是一个基于“游戏化正向反馈机制”的习惯养成系统。针对传统习惯养成过程中“反馈延迟、枯燥乏味、难以坚持”的痛点，本系统通过**即时金币奖励**、**可视化成长（装扮系统）** 以及**组队打卡监督**等机制，将目标拆解为可即时获得正反馈的小任务，帮助用户更容易地进入专注、愉悦的状态，从而更好地养成良好习惯。

本项目为课程结课作业。

## 效果展示

> 点击图片可查看大图

| 模块 | 截图 |
| :---: | :---: |
| **地图** | ![地图1](screenshots\map1.png)![地图2](screenshots\map2.png) |
| **登录与注册** | ![登录注册1](screenshots\login1.png) ![登录注册2](screenshots\login2.png)|
| **任务列表与打卡** | ![任务列表](screenshots\tasklist.png) |
| **装扮商城** | ![装扮商城](screenshots\shop.png) |
| **队伍聊天** | ![队伍聊天](screenshots\team.png) |
| **管理后台** | ![管理后台](screenshots\admin.png) |


## 技术栈

本项目采用前后端分离架构，具体技术选型如下：

**前端：**
*   **框架**：Vue 3
*   **UI 组件库**：Element Plus
*   **状态管理**：Pinia
*   **HTTP 客户端**：Axios
*   **实时通信**：WebSocket (Socket.io)
*   **构建工具**：Vite

**后端：**
*   **语言/框架**：Java 21 + Spring Boot 3.2.5
*   **持久层**：MyBatis-Plus 3.5.5
*   **安全认证**：Spring Security + JWT (0.12.3)
*   **实时通信**：Spring Boot Starter WebSocket
*   **邮件服务**：Spring Boot Starter Mail
*   **工具库**：Hutool 5.8.25
*   **定时任务**：Spring @Scheduled

**数据库与缓存：**
*   **关系型数据库**：MySQL 8.0
*   **缓存/中间件**：Redis 6.x

**开发环境：**
*   OS: Windows
*   IDE: IntelliJ IDEA (后端), VS Code (前端)
*   项目管理: Maven 3.6.3
*   版本控制: Git

## 核心功能模块

系统分为**用户端**和**管理端**，主要包含以下模块：

### 用户端
1.  **用户模块**：注册、登录、密码重置、个人信息管理、新手引导。
2.  **任务模块**：创建每日任务（简单/中等/困难）、完成任务打卡获得金币、删除任务、每日早8点自动重置任务。
3.  **商城模块**：
    *   **装扮商城**：按角色分类、按风格分类购买装扮。
    *   **自定义奖励**：购买“万能卡”获取资格，添加自定义奖励（如“吃一个冰淇淋”）并兑换。
    *   **装扮抽奖**：消耗金币进行幸运箱抽奖。
4.  **队伍模块**：
    *   创建队伍、申请加入、队长审批。
    *   队伍聊天室、查看队友任务进度、提醒队友。
    *   发起/接受队伍挑战（难度挑战）。
5.  **游戏化模块**：金币系统、装扮系统、角色对话。
6.  **消息模块**：实时接收队伍申请、审批结果、聊天消息、进度提醒。

### 管理端
1.  **用户管理**：查看用户列表、重置用户密码、封禁/解封违规账号。
2.  **奖励管理**：新增装扮（设置分类、价格、绑定角色、上传图片）。

## 项目结构

### 前端结构 (`habittt-frontend/`)
```text
habittt-frontend/
├── public/                 # 静态资源（初始化图片、背景）
├── src/
│   ├── api/                # API 接口定义层 (admin.js, team.js, user.js)
│   ├── components/         # 公共组件 (CustomRewardModule.vue, GuideDialog.vue 等)
│   ├── router/             # 路由配置 (index.js，含登录拦截)
│   ├── stores/             # Pinia 状态管理 (user.js, counter.js)
│   ├── utils/              # 工具函数 (request.js, websocket.js, imageHelper.js)
│   ├── views/              # 页面视图 (admin/, HomeView.vue, Login.vue 等)
│   ├── App.vue             # 根组件
│   └── main.js             # 入口文件
├── .gitignore
├── package.json
└── vite.config.js
```

### 后端结构 (`habittt-backend/`)
```text
habittt-backend/
├── src/main/java/com/example/habittt/
│   ├── common/             # 公共模块 (FileUploadConfig, JwtUtils, Result 等)
│   ├── controller/         # 控制器层 (Admin, Costume, Reward, Task, Team, User)
│   ├── entity/             # 实体类 (Admin, Costume, Task, Team, User 等)
│   ├── mapper/             # MyBatis-Plus Mapper 层
│   ├── service/            # 业务逻辑层
│   └── task/               # 定时任务 (DailyTaskResetScheduler)
├── src/main/resources/
│   └── application.properties # 配置文件 [注意：敏感信息已脱敏]
├── .gitignore
└── pom.xml
```

## 本地运行指南

### 1. 环境准备
*   JDK 21
*   Node.js (推荐 v18+)
*   MySQL 8.0
*   Redis 6.x

### 2. 后端启动
1.  创建数据库 `habittt`，并导入数据表结构。
    *   数据库初始化脚本位于 `src/main/resources/db/init.sql`。请在 MySQL 中新建 `habittt` 数据库后，执行该 SQL 文件以创建表和初始数据。
2.  复制 `src/main/resources/application-example.properties` 为 `application.properties`。
3.  修改 `application.properties`，填入你的本地数据库密码、Redis 配置以及邮箱 SMTP 授权码。
4.  运行 `HabitttApplication.java` 启动后端服务（默认端口 8080）。

### 3. 前端启动
1.  进入 `habittt-frontend` 目录。
2.  安装依赖：`npm install`
3.  启动开发服务器：`npm run dev`
4.  浏览器访问控制台提示的地址（通常是 `http://localhost:5173`）。

## 版权声明与免责条款

1.  **项目性质**：本项目为个人课程作业，仅供学习、交流使用。
2.  **图片素材**：项目内使用的所有角色、装扮、背景等图片素材均来源于网络收集。原作者已明确声明**禁止商用，仅供自用**。
    *   **若原作者认为本仓库内容侵犯了您的权益，请联系我，我将立即删除相关素材。**
3. **代码许可**：本项目代码部分采用MIT协议开源。使用或修改代码时，请保留原作者的版权声明。