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

## ⚠️ 环境要求

- JDK 8+（本机实测 JDK 19 可运行）
- Maven 3.6+（IDEA 自带 Maven 也可）
- MySQL 5.7+ / 8.0
- Node.js 16+、npm 8+
