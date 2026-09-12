# 宿舍管理系统（Dormitory Management System）

基于 **Spring Boot + MyBatis-Plus + MySQL + Vue 3 + Element Plus** 的前后端分离宿舍管理系统，适合课程设计 / 毕业设计二次开发，可直接用 **IntelliJ IDEA** 打开运行。

## ✨ 功能一览

| 模块 | 说明 |
| ---- | ---- |
| 首页看板 | 统计卡片（学生/楼栋/房间/床位/入住率）+ ECharts 图表（楼栋入住、性别分布、报修状态）+ 近期入住动态 |
| 学生管理 | 增删改查、学号/姓名搜索、性别筛选、分页 |
| 宿舍楼栋 | 楼栋增删改查、入住率进度条 |
| 房间管理 | 房间新增/删除、启用/停用、按楼栋筛选 |
| 入住管理 | 办理入住 / 办理退宿，自动更新床位占用并留痕 |
| 报修管理 | 报修提交、状态流转（待处理 → 处理中 → 已完成） |

## 🛠 技术栈

| 端 | 技术 |
| --- | ---- |
| 后端 | Spring Boot 2.7.18、MyBatis-Plus 3.5.3.1、MySQL 8.0、Lombok |
| 前端 | Vue 3、Vite 5、Vue Router 4、Element Plus、ECharts、Axios |
| 工具 | IntelliJ IDEA、Navicat / MySQL Workbench |

## 📁 目录结构

```
dormitory-system
├── sql/
│   └── dorm.sql                  # 数据库初始化脚本（建库建表 + 示例数据）
├── backend/                      # Spring Boot 后端（IDEA 直接打开）
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/dorm/
│       │   ├── controller/       # 接口层（REST API）
│       │   ├── service/          # 业务层
│       │   ├── mapper/           # 数据访问层（MyBatis-Plus）
│       │   ├── entity/           # 实体类
│       │   ├── vo/               # 视图对象
│       │   ├── dto/              # 请求参数对象
│       │   ├── common/           # 统一响应与异常处理
│       │   └── config/           # 跨域 / 分页插件配置
│       └── resources/
│           └── application.yml   # 数据库等配置
└── frontend/                     # Vue 3 前端
    └── src/
        ├── views/                # 页面（登录/看板/学生/楼栋/房间/入住/报修）
        ├── router/               # 路由与登录守卫
        └── utils/request.js      # Axios 封装
```

## 🚀 快速启动

### 1. 初始化数据库

用 Navicat / MySQL Workbench / 命令行执行 `sql/dorm.sql`：

```bash
mysql -uroot -p < sql/dorm.sql
```

> 脚本会删除并重建 `dorm_manage` 数据库，内置 3 栋楼、108 个房间、260 名学生等示例数据。
> 默认账号：**admin**，密码：**123456**（MD5 加密存储）。

### 2. 启动后端（IDEA）

1. `File → Open` 选择 `backend` 目录（或直接打开整个 `dormitory-system` 项目）
2. 等待 Maven 自动下载依赖（首次较慢）
3. 修改 `backend/src/main/resources/application.yml` 中的数据库用户名 / 密码
4. 运行 `com.dorm.DormApplication`，控制台输出「宿舍管理系统后端启动成功」即可

### 3. 启动前端

```bash
cd frontend
npm install        # 安装依赖
npm run dev        # 启动开发服务器
```

浏览器访问 **http://localhost:5173**，使用 admin / 123456 登录。

> 前端开发服务器已配置代理：`/api` 自动转发到 `http://localhost:8080`，无需额外跨域处理。

## 🔌 主要接口

| 方法 | 路径 | 说明 |
| ---- | ---- | ---- |
| POST | /api/login | 登录 |
| GET | /api/dashboard/stats | 首页看板统计 |
| GET/POST/PUT/DELETE | /api/students | 学生管理 |
| GET/POST/PUT/DELETE | /api/dormitories | 楼栋管理 |
| GET/POST/PUT/DELETE | /api/rooms | 房间管理 |
| GET | /api/rooms/options | 可入住房间 |
| POST | /api/checkins | 办理入住 |
| POST | /api/checkins/checkout | 办理退宿 |
| GET/POST/PUT/DELETE | /api/repairs | 报修管理 |

## 📝 二次开发建议

- 登录目前为轻量实现（前端存储用户信息），如需更严谨可引入 JWT + 拦截器
- 学生入住逻辑在 `CheckInService` 中以事务保证数据一致性，可作为扩展点
- 首页图表数据来自 `/api/dashboard/stats`，新增统计项只需在 `DashboardService` 中扩展

## ⚠️ 环境要求

- JDK 8+（本机实测 JDK 19 可运行）
- Maven 3.6+（IDEA 自带 Maven 也可）
- MySQL 5.7+ / 8.0
- Node.js 16+、npm 8+
