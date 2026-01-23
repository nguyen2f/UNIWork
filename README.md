<<<<<<< HEAD
# 🚀 UNIWork - Hệ Thống Quản Lý Dự Án Doanh Nghiệp

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-Latest-red.svg)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

**UNIWork** là một nền tảng quản lý dự án hiện đại, được thiết kế để tối ưu hóa quy trình làm việc, tăng cường sự cộng tác và theo dõi hiệu suất dự án trong doanh nghiệp.

---

## ✨ Tính Năng Chính

*   **📊 Dashboard Thông Minh:** Tổng quan về tiến độ dự án, thống kê task và hoạt động gần đây.
*   **📂 Quản Lý Dự Án:** Tạo, phân quyền và theo dõi các dự án đa cấp.
*   **📑 Quản Lý Công Việc (Task):** Hệ thống task chi tiết với trạng thái, độ ưu tiên, người thực hiện và bình luận.
*   **💬 Chat Thời Gian Thực:** Giao tiếp nội bộ mượt mà qua hệ thống WebSocket.
*   **🛎️ Thông Báo:** Cập nhật tức thời về các thay đổi trong dự án và công việc.
*   **📂 Quản Lý Tệp Tin:** Tích hợp Cloudinary để lưu trữ và quản lý tài liệu đính kèm chuyên nghiệp.
*   **📅 Lịch & Sự Kiện:** Theo dõi các mốc thời gian quan trọng của dự án.
*   **📝 Báo cáo:** Xuất báo cáo hiệu quả công việc và tiến độ dự án.

---

## 🛠️ Công Nghệ Sử Dụng

### Backend
- **Core:** Java 17, Spring Boot 3.4.5
- **Security:** Spring Security, JWT (JSON Web Token)
- **Database:** MySQL 8.0 (Persistence), Redis (Caching & Sessions)
- **Real-time:** Spring WebSocket (STOMP)
- **Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Cloud service:** Cloudinary (Lưu trữ ảnh/file)
- **Mail:** Spring Mail (Thông báo qua email)

### Infrastructure
- **Build Tool:** Maven
- **Containerization:** Docker, Docker Compose

---

## 🚀 Hướng Dẫn Cài Đặt

### 1. Yêu Cầu Hệ Thống
- JDK 17 hoặc cao hơn.
- Maven 3.8+.
- MySQL 8.0.
- Redis.

### 2. Cấu Hình Biến Môi Trường
Tạo file `.env` dựa trên file [.env.example](UNIWork/.env.example) và điền các thông tin cần thiết:

```env
SERVER_PORT=8080
DATASOURCE_URL=jdbc:mysql://localhost:3306/uniwork
DATASOURCE_USERNAME=your_username
DATASOURCE_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
REDIS_URL=redis://localhost:6379
# Cloudinary config
CLOUDINARY_CLOUD_NAME=your_name
CLOUDINARY_API_KEY=your_key
CLOUDINARY_API_SECRET=your_secret
```

### 3. Chạy Dự Án

#### Sử dụng Maven:
```bash
cd UNIWork
mvn clean install
mvn spring-boot:run
```

#### Sử dụng Docker:
```bash
docker-compose up -d
```

---

## 📚 Tài Liệu API (Swagger)

Sau khi khởi chạy ứng dụng, bạn có thể truy cập tài liệu API đầy đủ tại:
`http://localhost:8080/swagger-ui/index.html`

---

## 📁 Cấu Trúc Thư Mục

```text
UNIWork/
├── src/main/java/com/uniwork/
│   ├── config/          # Cấu hình hệ thống (Security, Redis, Cloudinary,...)
│   ├── controller/      # API Endpoints
│   ├── model/           # Entities, DTOs, Requests/Responses
│   ├── repository/      # Tầng truy xuất dữ liệu
│   ├── service/         # Xử lý logic nghiệp vụ
│   ├── filter/          # JWT Filters
│   └── util/            # Các tiện ích bổ trợ
├── src/main/resources/  # Configuration files (yml, properties)
├── Dockerfile           # Docker configuration
└── pom.xml              # Project dependencies
```

---

## 📐 Thiết Kế Hệ Thống

Các sơ đồ thiết kế chi tiết nằm tại thư mục gốc của dự án:
- [Sơ đồ Use Case](Usecase.drawio)
- [Sơ đồ Lớp (Class Diagram)](ClassDiagram.drawio)
- [Sơ đồ Chức năng Kinh doanh](BusinessFunctionDiagram.drawio)

---

## 👥 Đóng Góp
Mọi góp ý hoặc báo lỗi vui lòng tạo **Issue** hoặc gửi **Pull Request**.

---
*© 2024 UNIWork Team. All rights reserved.*
=======
📋 UNIWORK - Project and Task Management System
A comprehensive web application for managing projects, tasks, and team collaboration with role-based access control and data visualization.
🚀 Features
Core Functionality

📊 Project Management: Create, update, delete, and track projects
✅ Task Management: Organize tasks with priorities, deadlines, and assignments
👥 Team Collaboration: Manage team members and assign roles
🔐 Authentication & Authorization: Secure login with JWT and role-based access control (RBAC)
📈 Data Visualization: Interactive charts and dashboards for project analytics

Upcoming Features

💬 Real-time chat with WebSocket
📅 Gantt chart for project timeline visualization
📋 Kanban board for agile task management

🛠️ Tech Stack
Backend

Framework: Spring Boot 3.4
Language: Java 17+
Database: MySQL
Caching: Redis
Build Tool: Maven
Security: Spring Security + JWT
Documentation: Swagger/OpenAPI

Frontend

Framework: Next.js 14
Language: TypeScript
Styling: Tailwind CSS
State Management: React Context / Redux
UI Library: shadcn/ui
Charts: Recharts / Chart.js

DevOps & Deployment

Containerization: Docker
Backend Hosting: Render
Frontend Hosting: Vercel
Database Hosting: Railway
Version Control: Git & GitHub
>>>>>>> bdfa33a0e193bcedc1d0a8a410aebd9c2f8e21dc
