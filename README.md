# 🚀 UNIWork - Hệ Thống Quản Lý Dự Án Doanh Nghiệp (Enterprise Project Management Platform)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.0-red.svg)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**UNIWork** là nền tảng quản lý dự án quy mô doanh nghiệp hiện đại, được xây dựng với mục tiêu tối ưu hiệu năng xử lý, khả năng mở rộng (scalability) và trải nghiệm cộng tác thời gian thực cho các nhóm kỹ thuật và quản lý.

---

## ⚡ Điểm Nhấn Kiến Trúc & Tối Ưu Hóa Hiệu Năng (Engineering Highlights)

Dự án áp dụng các nguyên lý kiến trúc tiêu chuẩn Middle/Senior Engineer nhằm giải quyết triệt để các bài toán hiệu năng thực tế:

1. **Decoupled Aggregate Entity Architecture (Kiến trúc Entity độc lập)**:
   - Các Entity không lạm dụng `@ManyToOne`/`@JoinColumn` cascade phức tạp, giữ schema độc lập theo hướng domain-driven.
   - Toàn bộ các trường khóa ngoại (`projectId`, `assignedTo`, `stageId`,...) được đánh chỉ mục **B-Tree Index** ở tầng Database (`@Table(indexes = { ... })`), giúp truy vấn $O(\log N)$ ngay cả khi dữ liệu tăng trưởng quy mô lớn.
   - Ràng buộc toàn vẹn dữ liệu được đảm bảo bằng Unique Constraints (`uk_project_member(projectId, userId)`).

2. **Triệt tiêu bài toán N+1 Query (Zero N+1 Query Architecture)**:
   - Thay thế việc loop query trong tầng service bằng các câu lệnh JPQL Subquery và Projection có đánh index.
   - Tối ưu hóa thống kê thành viên dự án bằng câu lệnh batch aggregation `GROUP BY projectId` trong `ProjectMemberRepository`, giảm từ $(2N + 1)$ queries xuống chính xác **2 SQL queries**.

3. **Cơ chế Caching 2 lớp với Redis 7**:
   - Tích hợp Redis Caching với cấu hình serializer an toàn (`StringRedisSerializer` cho Key, `GenericJackson2JsonRedisSerializer` cho Value).
   - Áp dụng `@Cacheable` (TTL 10 phút) cho các truy vấn lặp lại như thông tin người dùng (`uniwork:user:profile`), danh sách phòng ban; tự động vô hiệu hóa (`@CacheEvict`) khi có cập nhật dữ liệu.

4. **Bảo mật chuyên sâu (Security Hardening)**:
   - Mã hóa mật khẩu một chiều với `BCryptPasswordEncoder` ở mọi luồng tạo/cập nhật tài khoản.
   - Thêm `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` trên trường `password`, ngăn chặn hoàn toàn rò rỉ hash mật khẩu qua JSON response.
   - Tích hợp `WebSocketAuthInterceptor` xác thực và thẩm định chữ ký JWT ngay từ phase bắt tay STOMP CONNECT.
   - Xử lý xác thực dữ liệu đầu vào `@Valid` tập trung tại `GlobalExceptionHandler` trả về chi tiết lỗi theo từng field.

5. **Xử lý bất đồng bộ & Tác vụ định kỳ (Async & Cron Jobs)**:
   - Cấu hình bounded thread pool `ThreadPoolTaskExecutor` (Core: 5, Max: 10, Queue: 100) ngăn ngừa cạn kiệt tài nguyên hệ thống.
   - Gửi email kích hoạt tài khoản bất đồng bộ `@Async` không chặn request của người dùng.
   - Định kỳ quét và gửi thông báo nhắc nhở các task quá hạn (`OverdueTaskJob`) vào 08:00 sáng hàng ngày.

6. **DevOps & Containerization chuẩn Production**:
   - `Dockerfile` tối ưu hóa đa tầng (Multi-stage build), tận dụng cache layer của Maven, chạy trên nền **Alpine JRE 17** siêu nhẹ với người dùng phi đặc quyền `spring` (Non-root user).
   - `docker-compose.yml` khởi động 1-click trọn gói hệ thống: Spring Boot + MySQL 8.0 + Redis 7 Alpine.
   - Bộ kiểm thử tự động Unit Tests (JUnit 5, Mockito) kiểm thử logic nghiệp vụ.

---

## ✨ Tính Năng Chính

* **📊 Dashboard Thông Minh:** Tổng quan về tiến độ dự án, thống kê task và chỉ số KPI.
* **📂 Quản Lý Dự Án Linh Hoạt:** Hỗ trợ nhiều phương pháp luận quản lý: Agile (Sprints), Waterfall (Phases), và Standard Kanban.
* **📑 Quản Lý Task & Issue Chuyên Sâu:** Phân công, gắn nhãn, lọc theo trạng thái/độ ưu tiên, xuất báo cáo CSV theo chuẩn UTF-8 Excel.
* **💬 Chat Thời Gian Thực:** Giao tiếp nội bộ mượt mà qua hệ thống WebSocket/STOMP.
* **🛎️ Thông Báo Tức Thời:** Cập nhật thông báo hệ thống và sự kiện dự án real-time.
* **📎 Quản Lý Tệp Tin:** Tích hợp Cloudinary lưu trữ và xem trước tài liệu PDF/ảnh.
* **📅 Lịch & Sự Kiện:** Theo dõi các mốc thời gian quan trọng của dự án.
* **📈 Báo Cáo & Xuất Dữ Liệu:** Thống kê hiệu suất làm việc của từng thành viên và phòng ban.

---

## 🛠️ Công Nghệ Sử Dụng

### Backend
- **Core:** Java 17, Spring Boot 3.4.5
- **Security:** Spring Security 6, JWT (JSON Web Token), BCrypt
- **Database:** MySQL 8.0 (Persistence), Redis 7 (Caching)
- **Data Access:** Spring Data JPA, Hibernate 6, JPQL Projections
- **Real-time:** Spring WebSocket, STOMP Protocol
- **Documentation:** SpringDoc OpenAPI 3 (Swagger UI)
- **Cloud Storage:** Cloudinary SDK
- **Mail:** Spring Boot Starter Mail

### Infrastructure & DevOps
- **Build Tool:** Maven 3.9+
- **Containerization:** Docker, Docker Compose
- **Testing:** JUnit 5, Mockito, AssertJ
- **CI/CD:** GitHub Actions Automation

---

## 🚀 Hướng Dẫn Cài Đặt & Khởi Chạy

### 1. Yêu Cầu Hệ Thống
- JDK 17+
- Maven 3.8+ (hoặc dùng `./mvnw`)
- Docker & Docker Compose (tùy chọn nhưng khuyến nghị)
- MySQL 8.0 & Redis 7

### 2. Khởi chạy nhanh bằng Docker Compose (Khuyến nghị)

```bash
# Clone repository
git clone https://github.com/nguyen2f/UNIWork.git
cd UNIWork/UNIWork

# Khởi động toàn bộ MySQL, Redis và Spring Boot API
docker-compose up -d
```

### 3. Khởi chạy thủ công với Maven

```bash
cd UNIWork/UNIWork

# Chạy Unit Tests kiểm tra hệ thống
./mvnw clean test

# Khởi chạy ứng dụng
./mvnw spring-boot:run
```

---

## 📚 Tài Liệu API (Swagger UI)

Khi ứng dụng đang chạy, truy cập tài liệu tương tác API tại:
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## 📁 Cấu Trúc Thư Mục (Modular Architecture)

```text
UNIWork/
├── src/main/java/com/uniwork/
│   ├── common/              # Common responses, exceptions, base utilities
│   ├── config/              # Security, Redis, Async, WebSocket, Cloudinary configs
│   ├── enums/               # Domain enums (ProjectStatus, Priority, Role,...)
│   ├── exceptions/          # CoreException, ErrorCode, GlobalExceptionHandler
│   ├── job/                 # Scheduled jobs (OverdueTaskJob)
│   ├── modules/             # Domain modules
│   │   ├── auth/            # Authentication, JWT utilities & filters
│   │   ├── project/         # Project & Member controllers, services, repositories
│   │   ├── stage/           # Stage / Sprint / Phase management
│   │   ├── task/            # Task management & CSV export
│   │   ├── issue/           # Issue tracking
│   │   ├── chat/            # Real-time WebSocket messaging
│   │   ├── notification/    # In-app notifications
│   │   ├── user/            # User profile & caching
│   │   └── file/            # File attachment & Cloudinary storage
│   └── util/                # Password hashing, BeanCopy, Date utilities
├── src/test/java/           # Unit & Integration test suite
├── Dockerfile               # Multi-stage production container build
├── docker-compose.yml       # 1-click deployment orchestration
└── pom.xml                  # Maven dependencies
```

---

## 📐 Thiết Kế Hệ Thống

Các sơ đồ thiết kế chi tiết:
- [Sơ đồ Use Case](Usecase.drawio)
- [Sơ đồ Lớp (Class Diagram)](ClassDiagram.drawio)
- [Sơ đồ Chức năng Nghiệp vụ](BusinessFunctionDiagram.drawio)

---
*© 2026 UNIWork. Developed by Nguyen.*
