# ☕ Employee Management System

Dự án học **Spring Boot** theo từng module, xây dựng hệ thống quản lý nhân viên hoàn chỉnh từ Hello World đến JWT Security và Reporting. Mỗi module tương ứng với một nhóm kiến thức, được tích hợp dần vào cùng một mini project thực tế.

---

## 📋 Mục lục

- [Tổng quan](#-tổng-quan)
- [Tech Stack](#-tech-stack)
- [Kiến trúc Layer](#-kiến-trúc-layer)
- [Lộ trình học Module 1–10](#-lộ-trình-học-module-110)
- [Cấu trúc thư mục](#-cấu-trúc-thư-mục)
- [Cài đặt & Chạy](#-cài-đặt--chạy)
- [Cấu hình môi trường](#-cấu-hình-môi-trường)
- [API Reference](#-api-reference)
- [Phân quyền](#-phân-quyền)
- [Database Schema](#-database-schema)
- [Seed Data](#-seed-data-mặc-định)
- [Logging](#-logging)
- [Troubleshooting](#-troubleshooting)
- [Các lệnh thường dùng](#-các-lệnh-thường-dùng)

---

## 🗺 Tổng quan

| Thông tin | Chi tiết                             |
|-----------|--------------------------------------|
| Ngôn ngữ | Java 17                              |
| Framework | Spring Boot 3.5.12                   |
| Database | MySQL 8+ (prod) / H2 in-memory (dev) |
| Authentication | JWT (JSON Web Token)                 |
| Web UI | Thymeleaf                            |
| Build Tool | Maven                                |
| Port mặc định | `8080`                               |

### Chức năng chính

- Quản lý nhân viên — Thêm, Sửa, Xóa, Tìm kiếm, Xem danh sách
- Quản lý phòng ban — CRUD với validate tên trùng
- Đăng ký / Đăng nhập bằng JWT
- Phân quyền ADMIN / USER
- Thống kê nhân viên theo phòng ban (số lượng, lương TB/max/min, tỷ lệ %)
- Web UI với Thymeleaf (list, form, search, statistics)
- Caching với Caffeine (TTL 60s dev / 300s prod)
- Logging với Logback + rolling file
- Health monitoring qua Spring Actuator

---

## 🛠 Tech Stack

| Thành phần | Thư viện / Công nghệ |
|------------|----------------------|
| Core | Spring Boot 3.2.0, Java 17 |
| Web | Spring Boot Starter Web, Thymeleaf |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL 8+ (prod), H2 (dev) |
| Security | Spring Security, JJWT 0.12.3 |
| Validation | Jakarta Bean Validation |
| Caching | Spring Cache + Caffeine |
| Monitoring | Spring Boot Actuator |
| Logging | SLF4J + Logback |
| Mapping | ModelMapper 3.2.0 |
| Utility | Lombok |
| Testing | Spring Boot Test |

---

## 🏗 Kiến trúc Layer

```
HTTP Request
     │
     ▼
┌──────────────────────────────────────────┐
│           Controller Layer               │  ← Nhận request, trả response
│  EmployeeController / DepartmentController   Không chứa business logic
│  AuthController / ReportController       │
└─────────────────┬────────────────────────┘
                  │
                  ▼
┌──────────────────────────────────────────┐
│            Service Layer                 │  ← Toàn bộ business logic
│  EmployeeServiceImpl / DepartmentServiceImpl  Validate, transform, orchestrate
│  AuthService / ReportService             │
└─────────────────┬────────────────────────┘
                  │
                  ▼
┌──────────────────────────────────────────┐
│          Repository Layer                │  ← Chỉ nói chuyện với DB
│  EmployeeRepository / DepartmentRepository   JpaRepository + @Query
│  UserRepository                          │
└─────────────────┬────────────────────────┘
                  │
                  ▼
          MySQL / H2 Database
```

> ⚠️ **Nguyên tắc**: Controller → Service → Repository. Controller **không được** inject Repository trực tiếp.

---

## 📚 Lộ trình học Module 1–10

### Module 1 — Getting Started with Spring Boot
> Spring Boot basics, Auto Configuration, Hello World REST API

**Lab:** Tạo project `employee-management`, viết API `/api/hello` kiểm tra server chạy thành công.

**Files:** `EmployeeManagementApplication.java`, `HelloController.java`

---

### Module 2 — Custom Bean & IoC
> Bean, IoC Container, @Component, @Service, @Bean, Dependency Injection

**Lab:** Tạo `UtilityService` (format tên, generate mã EMP-YYYY####), định nghĩa `PasswordEncoder` và `ModelMapper` bean trong `@Configuration`.

**Files:** `UtilityService.java`, `AppConfig.java`

---

### Module 3 — REST API cơ bản
> @RestController, @RequestMapping, Path variable, Request param, @RequestBody, ResponseEntity

**Lab:** API lấy danh sách nhân viên, thêm nhân viên mới với đầy đủ HTTP methods.

**Files:** `EmployeeController.java`, `DepartmentController.java`

---

### Module 4 — Spring Boot + Database (Spring Data JPA)
> Entity, @ManyToOne/@OneToMany, JpaRepository, CRUD với DB, @Query, seed data

**Lab:** Tạo bảng `employees`, `departments` với quan hệ FK, seed 3 phòng ban + 4 nhân viên, tìm kiếm theo tên/phòng ban.

**Files:** `Employee.java`, `Department.java`, `EmployeeRepository.java`, `DepartmentRepository.java`, `EmployeeServiceImpl.java`

---

### Module 5 — Validation & Exception Handling
> Bean Validation (@NotBlank, @Size, @Email, @Positive), @Valid, @RestControllerAdvice

**Lab:** Validate request, xử lý lỗi 400/404/500 với response JSON chuẩn.

**Files:** `EmployeeRequest.java`, `GlobalExceptionHandler.java`, `BusinessException.java`, `ResourceNotFoundException.java`, `ErrorResponse.java`

---

### Module 6 — Spring Boot Web (MVC + Thymeleaf)
> Thymeleaf template engine, @Controller, Model, th:each, th:field, Form Handling

**Lab:** Trang `/employees/list`, form thêm/sửa nhân viên, trang tìm kiếm.

**Files:** `EmployeeWebController.java`, `list.html`, `form.html`, `search.html`, `layout.html`

---

### Module 7 — Logging & Profiles
> SLF4J + Logback, Spring Profiles, Rolling file appender, @Slf4j

**Lab:** Log INFO/WARN khi CRUD nhân viên, tách config DB theo profile dev/prod, file log rolling 10MB/30 ngày.

**Files:** `application-dev.yml`, `application-prod.yml`, `logback-spring.xml`

| Profile | Database | Log level | Output |
|---------|----------|-----------|--------|
| `dev` | H2 in-memory | DEBUG | Console |
| `prod` | MySQL | INFO | Console + File |

---

### Module 8 — Advanced Spring Boot
> Spring Boot Actuator, @Scheduled, @EnableCaching, @Cacheable, @CacheEvict, Caffeine

**Lab:** API báo cáo tổng hợp có cache 60s, `@CacheEvict` khi CRUD, scheduled task log "System running" mỗi 30 giây.

**Files:** `SystemScheduler.java`, `ReportService.java`, `ReportController.java`

**Actuator endpoints:**

| Endpoint | Mô tả |
|----------|-------|
| `GET /actuator/health` | Trạng thái app + DB connection |
| `GET /actuator/info` | App version, description |
| `GET /actuator/metrics` | JVM memory, HTTP request count |
| `GET /actuator/loggers` | Xem/đổi log level runtime |

---

### Module 9 — Spring Security + JWT
> Spring Security Filter Chain, JWT, BCrypt, @PreAuthorize, @EnableMethodSecurity

**Lab:** User entity (ADMIN/USER roles), đăng ký/đăng nhập trả JWT token, phân quyền trên từng endpoint, 2 filter chains (API stateless + Web UI stateful).

**Files:** `User.java`, `JwtUtil.java`, `JwtAuthFilter.java`, `UserDetailsServiceImpl.java`, `AuthService.java`, `AuthController.java`, `SecurityConfig.java`

**Default users (tự động seed):**

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | ADMIN |
| `user` | `user123` | USER |

---

### Module 10 — Reporting & Analytics
> @Query nâng cao, Interface Projection, GROUP BY, thống kê trang Thymeleaf

**Lab:** Thống kê nhân viên theo phòng ban (COUNT, AVG, MAX, MIN salary, tỷ lệ %), trang web `/employees/statistics` với bar chart CSS và bảng chi tiết.

**Files:** `DeptStatProjection.java`, `StatisticsResponse.java`, `DeptStatResponse.java`, `statistics.html`

---

## 📁 Cấu trúc thư mục

```
employee-management/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/example/employeemanagement/
    │   │   ├── EmployeeManagementApplication.java   # @EnableScheduling @EnableCaching
    │   │   ├── config/
    │   │   │   ├── AppConfig.java                  # PasswordEncoder, ModelMapper
    │   │   │   └── SecurityConfig.java             # 2 filter chains: API (JWT) + Web
    │   │   ├── controller/
    │   │   │   ├── AuthController.java             # POST /api/auth/**
    │   │   │   ├── DepartmentController.java       # /api/departments
    │   │   │   ├── EmployeeController.java         # /api/employees
    │   │   │   ├── EmployeeWebController.java      # /employees/** (Thymeleaf)
    │   │   │   ├── HelloController.java            # /api/hello
    │   │   │   └── ReportController.java           # /api/reports/**
    │   │   ├── dto/
    │   │   │   ├── auth/
    │   │   │   │   ├── AuthResponse.java
    │   │   │   │   ├── LoginRequest.java
    │   │   │   │   └── RegisterRequest.java
    │   │   │   ├── request/
    │   │   │   │   ├── DepartmentRequest.java
    │   │   │   │   └── EmployeeRequest.java
    │   │   │   └── response/
    │   │   │       ├── DepartmentResponse.java
    │   │   │       ├── DeptStatResponse.java       # Module 10
    │   │   │       ├── EmployeeResponse.java
    │   │   │       ├── ErrorResponse.java
    │   │   │       └── StatisticsResponse.java     # Module 10
    │   │   ├── entity/
    │   │   │   ├── Department.java
    │   │   │   ├── Employee.java
    │   │   │   └── User.java                       # Module 9, implements UserDetails
    │   │   ├── exception/
    │   │   │   ├── BusinessException.java          # 400 Bad Request
    │   │   │   ├── GlobalExceptionHandler.java     # @RestControllerAdvice
    │   │   │   └── ResourceNotFoundException.java  # 404 Not Found
    │   │   ├── repository/
    │   │   │   ├── DepartmentRepository.java       # + findAllWithEmployees()
    │   │   │   ├── DeptStatProjection.java         # Module 10, Interface Projection
    │   │   │   ├── EmployeeRepository.java         # + query thống kê Module 10
    │   │   │   └── UserRepository.java             # Module 9
    │   │   ├── scheduler/
    │   │   │   └── SystemScheduler.java            # Module 8, @Scheduled 30s
    │   │   ├── security/
    │   │   │   ├── JwtAuthFilter.java              # Module 9, OncePerRequestFilter
    │   │   │   ├── JwtUtil.java                    # Module 9, generate + verify JWT
    │   │   │   └── UserDetailsServiceImpl.java     # Module 9
    │   │   ├── service/
    │   │   │   ├── AuthService.java                # Module 9
    │   │   │   ├── DepartmentService.java          # Interface
    │   │   │   ├── EmployeeService.java            # Interface
    │   │   │   ├── ReportService.java              # Module 8 + 10, @Cacheable
    │   │   │   ├── UtilityService.java             # Module 2
    │   │   │   └── impl/
    │   │   │       ├── DepartmentServiceImpl.java  # Refactor Module 9
    │   │   │       └── EmployeeServiceImpl.java    # @CacheEvict Module 8
    │   └── resources/
    │       ├── application.yml                     # profiles.active: prod
    │       ├── application-dev.yml                 # H2 + DEBUG log + JWT
    │       ├── application-prod.yml                # MySQL + INFO log + JWT
    │       ├── logback-spring.xml                  # Rolling file appender
    │       └── templates/
    │           ├── fragments/layout.html           # Navbar + shared CSS
    │           └── employees/
    │               ├── form.html                   # Add / Edit form
    │               ├── list.html                   # Danh sách nhân viên
    │               ├── search.html                 # Tìm kiếm
    │               └── statistics.html             # Module 10, bar chart + table
    └── test/
        └── http/
            └── employee-api.http                   # IntelliJ HTTP Client test file
```

---

## 🚀 Cài đặt & Chạy

### Yêu cầu

- Java 17+
- Maven 3.8+ (hoặc dùng `./mvnw` đi kèm project)
- MySQL 8+ (cho profile `prod`)

### Bước 1 — Clone project

```bash
git clone <repo-url>
cd employee-management
```

### Bước 2 — Tạo database MySQL

```sql
CREATE DATABASE employee_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Bước 3 — Cấu hình biến môi trường

```bash
# Thêm vào ~/.zshrc hoặc ~/.bash_profile
export DB_USERNAME=root
export DB_PASSWORD=your_password

# Reload
source ~/.zshrc

# Kiểm tra
echo $DB_PASSWORD
```

### Bước 4 — Chạy ứng dụng

```bash
# Profile prod (MySQL) — mặc định
./mvnw spring-boot:run

# Profile dev (H2 — không cần MySQL)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Bước 5 — Kiểm tra hoạt động

```bash
# Health check
curl http://localhost:8080/actuator/health
# {"status":"UP"}

# Login lấy token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Web UI:** http://localhost:8080/employees/list

**H2 Console** (chỉ profile dev): http://localhost:8080/h2-console

---

## ⚙️ Cấu hình môi trường

### `application.yml` (base — chung 2 profile)

```yaml
spring:
  profiles:
    active: prod          # Đổi thành "dev" để dùng H2
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=500,expireAfterWrite=60s

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,loggers
  endpoint:
    health:
      show-details: always
```

### `application-prod.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/employee_db?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD}        # Bắt buộc set biến môi trường
  jpa:
    hibernate:
      ddl-auto: update
  cache:
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=300s

jwt:
  secret: mySecretKey404E635266556A586E3272357538782F413F4428472B4B6250645367566B59
  expiration: 86400000              # 24 giờ (ms)
```

---

## 📡 API Reference

### Authentication (Public — không cần token)

| Method | Endpoint | Mô tả | Body |
|--------|----------|-------|------|
| `POST` | `/api/auth/login` | Đăng nhập, nhận JWT | `{username, password}` |
| `POST` | `/api/auth/register` | Đăng ký tài khoản | `{username, password, role}` |

**Response login:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ADMIN",
  "expiresIn": 86400000
}
```

### Employees (cần Bearer token)

| Method | Endpoint | Quyền | Mô tả |
|--------|----------|-------|-------|
| `GET` | `/api/employees` | USER, ADMIN | Danh sách (filter theo `?department=`) |
| `GET` | `/api/employees/{id}` | USER, ADMIN | Lấy theo ID |
| `GET` | `/api/employees/search?keyword=` | USER, ADMIN | Tìm theo tên/phòng ban |
| `POST` | `/api/employees` | **ADMIN only** | Tạo mới |
| `PUT` | `/api/employees/{id}` | **ADMIN only** | Cập nhật |
| `DELETE` | `/api/employees/{id}` | **ADMIN only** | Xóa |

### Departments (cần Bearer token)

| Method | Endpoint | Quyền | Mô tả |
|--------|----------|-------|-------|
| `GET` | `/api/departments` | USER, ADMIN | Danh sách + employee count |
| `POST` | `/api/departments` | **ADMIN only** | Tạo mới |

### Reports (cần Bearer token)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| `GET` | `/api/reports/summary` | Tổng hợp đơn giản (Module 8, cache) |
| `GET` | `/api/reports/statistics` | Thống kê đầy đủ (Module 10, cache) |
| `GET` | `/api/reports/by-department` | Phân tích theo phòng ban |

### Web UI (không cần token)

| URL | Mô tả |
|-----|-------|
| `/employees/list` | Danh sách nhân viên |
| `/employees/add` | Form thêm mới |
| `/employees/edit/{id}` | Form chỉnh sửa |
| `/employees/search` | Tìm kiếm |
| `/employees/statistics` | Trang thống kê |

---

## 🔐 Phân quyền

### JWT Flow

```
1. POST /api/auth/login → Server xác thực → Trả JWT (24h)
2. Client lưu token
3. Mọi request tiếp theo: Authorization: Bearer <token>
4. JwtAuthFilter đọc token → Verify → Set SecurityContext
5. @PreAuthorize("hasRole('ADMIN')") kiểm tra role
```

### Role Matrix

| Endpoint | Không token | USER | ADMIN |
|----------|:-----------:|:----:|:-----:|
| `POST /api/auth/**` | ✅ | ✅ | ✅ |
| `GET /api/employees` | ❌ 403 | ✅ | ✅ |
| `POST/PUT/DELETE /api/employees` | ❌ 403 | ❌ 403 | ✅ |
| `GET /api/departments` | ❌ 403 | ✅ | ✅ |
| `POST /api/departments` | ❌ 403 | ❌ 403 | ✅ |
| `GET /api/reports/**` | ❌ 403 | ✅ | ✅ |
| `/employees/**` (Web UI) | ✅ | ✅ | ✅ |
| `/actuator/**` | ✅ | ✅ | ✅ |

---

## 🗄 Database Schema

```
departments
├── id           BIGINT PK AUTO_INCREMENT
├── name         VARCHAR(100) UNIQUE NOT NULL
└── description  VARCHAR(255)

employees
├── id              BIGINT PK AUTO_INCREMENT
├── employee_code   VARCHAR(20) UNIQUE NOT NULL    ← EMP-20261001
├── name            VARCHAR(100) NOT NULL
├── email           VARCHAR(150) UNIQUE NOT NULL
├── position        VARCHAR(100)
├── salary          DOUBLE
├── created_at      DATETIME
└── department_id   BIGINT FK → departments.id

users
├── id        BIGINT PK AUTO_INCREMENT
├── username  VARCHAR(50) UNIQUE NOT NULL
├── password  VARCHAR(255) NOT NULL               ← BCrypt (strength 12)
├── role      ENUM('ADMIN','USER') NOT NULL
└── enabled   BOOLEAN DEFAULT TRUE
```

---

## 📊 Seed Data mặc định

Khi khởi động lần đầu (DB trống), hệ thống tự tạo:

**Phòng ban:**

| Tên | Mô tả |
|-----|-------|
| Engineering | Software development |
| HR | Human resources |
| Finance | Financial management |

**Nhân viên:**

| Tên | Email | Chức vụ | Phòng ban | Lương |
|-----|-------|---------|-----------|-------|
| Nguyen Van An | an.nguyen@company.com | Backend Developer | Engineering | 25,000,000 |
| Tran Thi Bich | bich.tran@company.com | HR Manager | HR | 30,000,000 |
| Le Van Cuong | cuong.le@company.com | Frontend Developer | Engineering | 22,000,000 |
| Pham Thi Dung | dung.pham@company.com | Accountant | Finance | 20,000,000 |

**Tài khoản:**

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | ADMIN — full CRUD |
| `user` | `user123` | USER — chỉ xem |

---

## 📝 Logging

Cấu hình `logback-spring.xml` với `SizeAndTimeBasedRollingPolicy`:

```
logs/
├── employee-management.log              ← File đang ghi (hôm nay)
├── employee-management-2026-03-26.0.log ← File cũ (sang ngày / vượt 10MB)
└── employee-management-2026-03-26.1.log ← Cùng ngày, vượt 10MB lần 2
```

| Điều kiện | Hành động |
|-----------|-----------|
| Sang ngày mới 00:00 | File cũ đổi tên, tạo file mới |
| File vượt 10MB | File cũ đổi tên (tăng index .0, .1...) |
| File cũ hơn 30 ngày | Tự động xóa |
| Tổng dung lượng > 500MB | Xóa file cũ nhất |

> **Log không bao giờ bị ghi đè** — luôn append.

---

## 🐛 Troubleshooting

| Lỗi | Nguyên nhân | Fix |
|-----|-------------|-----|
| `Port 8080 was already in use` | Process cũ chưa tắt | `lsof -ti :8080 \| xargs kill -9` |
| `Access denied for user '${DB_USERNAME}'` | Biến môi trường chưa load | `source ~/.zshrc` |
| `MalformedInputException` khi build | Maven filter gặp ký tự UTF-8 | Thêm `<filtering>false</filtering>` trong `pom.xml` |
| `cacheManager bean not found` | Thiếu `@EnableCaching` hoặc Caffeine dependency | Kiểm tra `EmployeeManagementApplication.java` và `pom.xml` |
| `LazyInitializationException` | Gọi lazy relation ngoài transaction | Dùng `JOIN FETCH` trong `@Query` |
| `Duplicate entry 'EMP-...'` | Counter reset sau restart (MySQL giữ data) | `UtilityService` lazy init counter từ max DB sequence |
| `This site can't be reached` trên Web UI | Session STATELESS block browser | `SecurityConfig` đã tách 2 filter chains |
| `/favicon.ico ERROR` trong log | `GlobalExceptionHandler` catch 404 favicon | Kiểm tra `favicon.ico` trong `src/main/resources/static/` |

---

## ⌨️ Các lệnh thường dùng

| Lệnh | Mục đích |
|------|----------|
| `./mvnw spring-boot:run` | Chạy với profile prod (MySQL) |
| `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` | Chạy với profile dev (H2) |
| `./mvnw clean spring-boot:run` | Clean build rồi chạy |
| `./mvnw package -DskipTests` | Build JAR |
| `./mvnw test` | Chạy unit tests |
| `lsof -ti :8080 \| xargs kill -9` | Kill process đang chiếm port 8080 |
| `tail -f logs/employee-management.log` | Xem log realtime |
| `source ~/.zshrc` | Reload biến môi trường |
