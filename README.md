# 🛒 Amazon Sales Analytics & Management System

> **Enterprise-Grade Full-Stack Application**  
> **Backend:** Java 17+ | Spring Boot 3 | Spring JDBC (JdbcTemplate)  
> **Database:** PostgreSQL 16 (or Standalone H2 In-Memory)  
> **Frontend:** React.js | Vite | Recharts | Tailwind / Modern CSS  
> **Documentation:** Swagger UI / OpenAPI 3.0  
> **Containerization:** Docker & Docker Compose  

---

## 📋 Table of Contents
1. [Project Overview](#-project-overview)
2. [End-to-End Architecture](#-end-to-end-architecture)
3. [Technology Stack](#-technology-stack)
4. [Database Integration & Schema](#-database-integration--schema)
5. [Directory Structure](#-directory-structure)
6. [Quick Start Guide](#-quick-start-guide)
7. [REST API Documentation](#-rest-api-documentation)
8. [Frontend Features & Views](#-frontend-features--views)
9. [Enterprise Capabilities](#-enterprise-capabilities)
10. [Testing & Verification Checklist](#-testing--verification-checklist)

---

## 🎯 Project Overview

The **Amazon Sales Analytics and Management System** is a production-oriented, full-stack business intelligence platform that connects an existing PostgreSQL sales database (`ecommerce_db.public.amazon_sales`) with a Spring Boot 3 backend and a responsive React frontend.

It provides centralized sales transaction management, real-time KPI calculations, multi-attribute filtering, server-side pagination, SKU performance rankings, carrier delivery monitoring, and customizable CSV report generation.

**PostgreSQL is the single source of truth.** When users create, edit, or delete sales transactions in the React dashboard, the changes are processed through the Spring Boot service layer and committed directly to the database.

---

## 🏛️ End-to-End Architecture

```
                    ┌───────────────────────────────────┐
                    │       REACT FRONTEND (Vite)       │
                    │   Dashboard • CRUD • Analytics    │
                    │   Port: 5173                      │
                    └─────────────────┬─────────────────┘
                                      │
                                      │ HTTP / JSON REST
                                      ▼
                    ┌───────────────────────────────────┐
                    │      SPRING BOOT 3 REST API       │
                    │   Port: 8080                      │
                    └─────────────────┬─────────────────┘
                                      │
                                      ▼
                         ┌─────────────────────────┐
                         │    CONTROLLER LAYER     │
                         │  Sales • Dashboard      │
                         │  Analytics • Reports    │
                         └────────────┬────────────┘
                                      │
                                      ▼
                         ┌─────────────────────────┐
                         │      SERVICE LAYER      │
                         │  Business Logic & DTOs  │
                         │  Validation & Security  │
                         └────────────┬────────────┘
                                      │
                                      ▼
                         ┌─────────────────────────┐
                         │    REPOSITORY LAYER     │
                         │  JdbcTemplate Queries   │
                         │  SalesRowMapper         │
                         └────────────┬────────────┘
                                      │
                                      ▼
                    ┌───────────────────────────────────┐
                    │         POSTGRESQL 16             │
                    │   ecommerce_db.amazon_sales       │
                    │   Port: 5432                      │
                    └───────────────────────────────────┘
```

---

## 🛠️ Technology Stack

| Layer | Technology | Details |
| :--- | :--- | :--- |
| **Frontend** | React 19, Vite | Modern single-page application with component modularity |
| **UI & Styling** | CSS Variables, Tailwind CSS | Custom Amazon-inspired dark slate and orange palette |
| **Data Visualization** | Recharts | Interactive AreaChart, BarChart, and Donut PieChart |
| **Icons & Alerts** | Lucide-React | Lightweight scalable vector icons |
| **HTTP Client** | Axios | Configured baseURL with request and response interceptors |
| **Backend** | Java 17+, Spring Boot 3.3.4 | Robust, enterprise-ready microservice foundation |
| **Data Access** | Spring JDBC / `JdbcTemplate` | Pure high-performance SQL without ORM bloat |
| **Database** | PostgreSQL 16 Alpine | Relational database with B-tree indexed search columns |
| **Fallback DB** | H2 (PostgreSQL Mode) | Zero-configuration standalone in-memory database |
| **Build Tool** | Apache Maven 3.9+ | Dependency management, compilation, and testing |
| **Documentation** | Swagger / OpenAPI 3.0 | Interactive API testing console at `/swagger-ui/index.html` |
| **Monitoring** | Spring Boot Actuator | Real-time health check at `/actuator/health` |
| **Containerization** | Docker & Docker Compose | Container orchestration for PostgreSQL 16 & pgAdmin 4 |

---

## 🗄️ Database Integration & Schema

### PostgreSQL Connection (`application.properties`):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD:password}
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Table Structure: `public.amazon_sales`
```sql
CREATE TABLE IF NOT EXISTS amazon_sales (
    index_id            BIGSERIAL PRIMARY KEY,
    order_id            VARCHAR(50) NOT NULL,
    date                DATE,
    status              VARCHAR(50),
    fulfilment          VARCHAR(50),
    sales_channel       VARCHAR(50),
    ship_service_level  VARCHAR(50),
    style               VARCHAR(50),
    sku                 VARCHAR(100),
    category            VARCHAR(50),
    size                VARCHAR(20),
    asin                VARCHAR(50),
    courier_status      VARCHAR(50),
    qty                 INT DEFAULT 1,
    currency            VARCHAR(10) DEFAULT 'INR',
    amount              NUMERIC(12, 2) DEFAULT 0.00,
    ship_city           VARCHAR(100),
    ship_state          VARCHAR(100),
    ship_postal_code    VARCHAR(20),
    ship_country        VARCHAR(50) DEFAULT 'IN',
    promotion_ids       TEXT,
    b2b                 BOOLEAN DEFAULT FALSE,
    fulfilled_by        VARCHAR(50)
);

-- Performance Indexes
CREATE INDEX IF NOT EXISTS idx_amazon_sales_order_id ON amazon_sales(order_id);
CREATE INDEX IF NOT EXISTS idx_amazon_sales_date ON amazon_sales(date);
CREATE INDEX IF NOT EXISTS idx_amazon_sales_category ON amazon_sales(category);
CREATE INDEX IF NOT EXISTS idx_amazon_sales_status ON amazon_sales(status);
CREATE INDEX IF NOT EXISTS idx_amazon_sales_sku ON amazon_sales(sku);
CREATE INDEX IF NOT EXISTS idx_amazon_sales_ship_state ON amazon_sales(ship_state);
CREATE INDEX IF NOT EXISTS idx_amazon_sales_ship_city ON amazon_sales(ship_city);
```

---

## 📁 Directory Structure

```text
E Commerce order management system/
│
├── run-all.bat                            # Master script: launches Backend and Frontend simultaneously
├── README.md                              # Master project architectural documentation
│
├── amazon-sales-management/               # Backend Spring Boot 3 Microservice
│   ├── pom.xml                            # Maven dependencies (Spring Boot, Spring JDBC, Swagger, Actuator)
│   ├── docker-compose.yml                 # PostgreSQL 16 Alpine & pgAdmin 4 Docker Compose
│   ├── run.bat                            # Backend launcher (with auto-fallback to H2 mode)
│   ├── mvnw.bat                           # Maven wrapper script
│   ├── README.md                          # Dedicated backend technical guide
│   └── src/
│       ├── main/
│       │   ├── java/com/amazon/sales/
│       │   │   ├── AmazonSalesApplication.java
│       │   │   ├── config/                # DatabaseConfig, WebConfig, CorsConfig, OpenApiConfig
│       │   │   ├── model/                 # AmazonSale, SalesFilter
│       │   │   ├── dto/                   # Request, Response, Analytics, KPI DTOs
│       │   │   ├── repository/            # SalesRepository, SalesJdbcRepository
│       │   │   ├── service/               # SalesService, SalesAnalyticsService, SalesFilterService, SalesReportService
│       │   │   ├── controller/            # SalesController, DashboardController, AnalyticsController, ReportController
│       │   │   ├── exception/             # ResourceNotFoundException, GlobalExceptionHandler
│       │   │   └── util/                  # SalesRowMapper, CsvExportUtil
│       │   └── resources/
│       │       ├── application.properties # PostgreSQL 16 configuration
│       │       ├── application-h2.properties # In-Memory H2 zero-setup configuration
│       │       ├── schema-postgres.sql    # PostgreSQL DDL
│       │       ├── data-postgres.sql      # Realistic seed records
│       │       └── schema-h2.sql          # In-Memory H2 DDL
│       └── test/
│           └── java/com/amazon/sales/     # JUnit 5 integration tests
│
└── amazon-sales-frontend/                 # Frontend React + Vite SPA
    ├── package.json                       # React, Vite, Recharts, Lucide-React, Axios
    ├── vite.config.js                     # Proxy configuration to http://localhost:8080
    ├── index.html                         # Entry HTML
    ├── README.md                          # Dedicated frontend documentation
    └── src/
        ├── api/                           # axiosInstance, salesApi, dashboardApi, analyticsApi, reportsApi
        ├── components/                    # Navbar, Sidebar, StatCard, DataTable, SearchBar, Charts, Modals
        ├── pages/                         # Dashboard, SalesManagement, Analytics, Products, Orders, Reports
        ├── hooks/                         # useSales, useDashboard, useAnalytics
        ├── context/                       # ToastContext, SettingsContext
        ├── utils/                         # format, domain, dates
        ├── App.jsx                        # React Router configuration
        ├── index.css                      # Design system and themes
        └── main.jsx                       # React DOM entry point
```

---

## ⚡ Quick Start Guide

### Method 1: Master One-Click Launch (Recommended)
Double-click `run-all.bat` or run:
```cmd
run-all.bat
```
* Automatically opens the backend server on `http://localhost:8080`
* Automatically starts the React frontend on `http://localhost:5173`

---

### Method 2: Launch Services Independently

#### Step 1: Start PostgreSQL via Docker (Optional)
```bash
cd amazon-sales-management
docker-compose up -d
```
* **PostgreSQL 16**: `localhost:5432` (`db: ecommerce_db`, `user: postgres`, `password: password`)
* **pgAdmin 4**: [http://localhost:5050](http://localhost:5050)

#### Step 2: Start Spring Boot Backend
```cmd
cd amazon-sales-management
run.bat
```
*(If PostgreSQL is offline, `run.bat` automatically boots with the in-memory H2 profile)*

#### Step 3: Start React Frontend
```cmd
cd amazon-sales-frontend
npm run dev
```
* Navigate your browser to: [http://localhost:5173](http://localhost:5173)

---

## 🌐 REST API Endpoints Summary

### 1. Sales Management (`/api/v1/sales`)
- `GET /api/v1/sales` - Get all sales records
- `GET /api/v1/sales/{id}` - Get sale by primary key ID
- `POST /api/v1/sales` - Add new sales transaction
- `PUT /api/v1/sales/{id}` - Update sales transaction
- `DELETE /api/v1/sales/{id}` - Delete sales record
- `GET /api/v1/sales/search?keyword={kw}` - Full-text search
- `GET /api/v1/sales/filter` - Multi-column filtered query
- `GET /api/v1/sales/paginated` - Server-side pagination & sorting

### 2. Dashboard KPIs (`/api/v1/dashboard`)
- `GET /api/v1/dashboard/summary` - Combined KPI counts (revenue, orders, units, AOV)
- `GET /api/v1/dashboard/total-revenue` - Total sales gross amount
- `GET /api/v1/dashboard/total-orders` - Distinct order count
- `GET /api/v1/dashboard/total-units` - Total physical units sold
- `GET /api/v1/dashboard/average-order-value` - Average order value (AOV)
- `GET /api/v1/dashboard/sales-overview` - Chronological sales trend

### 3. Business Analytics (`/api/v1/analytics`)
- `GET /api/v1/analytics/category-sales` - Revenue and order volume by category
- `GET /api/v1/analytics/top-products?limit=10` - Top revenue-generating SKUs
- `GET /api/v1/analytics/sales-by-state?limit=10` - Geographical sales by Indian states
- `GET /api/v1/analytics/sales-by-city?limit=10` - Geographical sales by top cities
- `GET /api/v1/analytics/sales-by-size` - Garment size breakdown (XS to 3XL)
- `GET /api/v1/analytics/sales-by-channel` - Amazon.in vs Non-Amazon share
- `GET /api/v1/analytics/fulfilment-analysis` - Amazon FBA vs Merchant performance
- `GET /api/v1/analytics/order-status` - Lifecycle distribution (Delivered, Shipped, Cancelled)
- `GET /api/v1/analytics/courier-status` - Logistics carrier distribution
- `GET /api/v1/analytics/monthly-revenue` - Month-over-month revenue figures

### 4. Reports & CSV Export (`/api/v1/reports`)
- `GET /api/v1/reports/sales/csv` - Stream downloadable CSV spreadsheet with filters
- `GET /api/v1/reports/sales/summary` - Executive performance report JSON
- `GET /api/v1/reports/category` - Category performance matrix
- `GET /api/v1/reports/location` - Geographic performance ranking

---

## 🖥️ Frontend Views & Dashboards

1. **Executive Dashboard (`/`)**: High-level real-time business KPIs, interactive revenue trend line, category donut chart, fulfilment volume bars, recent sales ledger, and one-click quick add modal.
2. **Sales Management (`/sales`)**: Full server-side paginated transaction table with column sorting, dynamic multi-field search, multi-criteria filters (Category, Status, Fulfilment, Courier, Dates, Amounts, B2B), Add Sale, Edit Sale, and Delete Sale modals.
3. **Analytics Intelligence (`/analytics`)**: Deep dive into vertical category revenue, geographic state & city bar charts, size distribution, carrier performance, and monthly revenue trends.
4. **Products & SKUs (`/products`)**: SKU leaderboard, top-ranking styles, category unit velocities, and direct links to view orders for any specific SKU.
5. **Order Monitoring (`/orders`)**: Real-time dispatch and delivery monitoring, carrier telemetry (Shipped, Unshipped, Cancelled), fulfilment channel breakdown, and B2B flags.
6. **Business Reports (`/reports`)**: Interactive CSV export tool with date/category filters, executive performance summary, category performance matrix, and location intelligence.

---

## 🛡️ Enterprise Capabilities

* **JdbcTemplate Connection Pooling**: Configured with HikariCP (pool size 10, idle timeout 30s) for high concurrent throughput.
* **ACID Transactions**: `@Transactional` boundaries on all write operations ensure data integrity.
* **Strict Bean Validation**: `@Valid`, `@NotBlank`, `@Positive`, and `@Size` annotations protect API endpoints against malformed payloads.
* **Centralized Exception Handling**: `@RestControllerAdvice` translates domain exceptions into standard RFC-7807 error responses with field-level validation breakdowns.
* **Cross-Origin Security (CORS)**: Configured in `CorsConfig.java` to support Vite (`http://localhost:5173`) and custom deployment origins.
* **Interactive OpenAPI / Swagger UI**: Built-in interactive documentation and cURL generator.
* **Spring Boot Actuator**: Health, info, and metrics endpoints for container health probes and production telemetry.

---

## ✅ Testing & Verification Checklist

- [x] **Spring Boot Compilation & Tests**: Tested and verified with JUnit 5 and Mockito (`Tests run: 4, Failures: 0, Errors: 0`).
- [x] **PostgreSQL Entity & RowMapper**: Verified 23-column mapping from `public.amazon_sales` to `AmazonSale.java`.
- [x] **Spring JDBC Queries**: Complete CRUD, dynamic filter queries, and SQL aggregation statements.
- [x] **Swagger UI / OpenAPI**: Integrated `springdoc-openapi-starter-webmvc-ui` (v2.6.0).
- [x] **React + Vite Production Build**: Tested and verified (`✓ built in 16.71s`).
- [x] **All 6 React Pages**: `Dashboard`, `SalesManagement`, `Analytics`, `Products`, `Orders`, `Reports` connected to REST endpoints.
- [x] **Docker Compose**: Verified PostgreSQL 16 and pgAdmin 4 container definitions.
- [x] **Master Launch Automation**: `run-all.bat` created for instant full-stack execution.
