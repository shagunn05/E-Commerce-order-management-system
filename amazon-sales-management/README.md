# 🛒 Amazon Sales Analytics & Management System (Backend)

An enterprise-grade, high-performance Spring Boot 3 & Java 17+ RESTful backend built with **Spring JDBC (JdbcTemplate)**, designed to connect directly to PostgreSQL 16 (`ecommerce_db.public.amazon_sales`) and support standalone in-memory execution via H2 (PostgreSQL compatibility mode).

---

## 🏛️ System Architecture

```text
amazon-sales-management/
│
├── pom.xml                                   # Spring Boot 3, Spring JDBC, PostgreSQL, H2, Validation, Swagger UI
├── docker-compose.yml                        # PostgreSQL 16 Alpine container & pgAdmin 4 orchestration
├── README.md                                 # Backend architecture & API reference
├── mvnw.bat                                  # Maven wrapper pointing to IntelliJ bundled or local Maven
├── run.bat                                   # One-click launcher with auto-fallback to H2 mode
│
└── src/
    ├── main/
    │   ├── java/com/amazon/sales/
    │   │   ├── AmazonSalesApplication.java   # Spring Boot bootstrap entry point
    │   │   │
    │   │   ├── config/                       # Infrastructure & Web Configuration
    │   │   │   ├── DatabaseConfig.java       # JdbcTemplate bean & PlatformTransactionManager
    │   │   │   ├── WebConfig.java            # WebMvcConfigurer with ISO Date formatters
    │   │   │   ├── CorsConfig.java           # Cross-Origin Resource Sharing for React/Vite
    │   │   │   └── OpenApiConfig.java        # Swagger / OpenAPI 3.0 metadata & documentation
    │   │   │
    │   │   ├── model/                        # Pure Domain Entities
    │   │   │   ├── AmazonSale.java           # 23-column domain entity matching amazon_sales table
    │   │   │   └── SalesFilter.java          # Multi-attribute filter & pagination model
    │   │   │
    │   │   ├── dto/                          # Validated Data Transfer Objects
    │   │   │   ├── SalesRequestDto.java      # Validated payload for POST and PUT operations
    │   │   │   ├── SalesResponseDto.java     # Complete JSON response representing a sales transaction
    │   │   │   ├── SalesAnalyticsDto.java    # Aggregated analytics overview response
    │   │   │   ├── DashboardStatsDto.java    # Executive KPI counts (revenue, orders, units, AOV)
    │   │   │   ├── CategorySalesDto.java     # Category revenue, volume, and average price breakdown
    │   │   │   ├── LocationSalesDto.java     # State and City geographic revenue distribution
    │   │   │   ├── DailySalesDto.java        # Daily chronological revenue and unit trend
    │   │   │   ├── MonthlyRevenueDto.java    # Month-over-month revenue performance
    │   │   │   ├── StatusDistributionDto.java# Status, courier, channel, and fulfilment distributions
    │   │   │   ├── TopProductDto.java        # SKU and style sales rankings
    │   │   │   ├── PageResponseDto.java      # Server-side pagination metadata wrapper
    │   │   │   └── ErrorResponseDto.java     # RFC-7807 compliant standardized error response
    │   │   │
    │   │   ├── repository/                   # Data Access Layer (Spring JDBC / JdbcTemplate)
    │   │   │   ├── SalesRepository.java      # CRUD & Analytics contract interface
    │   │   │   └── SalesJdbcRepository.java  # Production JdbcTemplate repository with SQL queries
    │   │   │
    │   │   ├── service/                      # Business Logic Layer
    │   │   │   ├── SalesService.java         # CRUD operations and entity-DTO conversions
    │   │   │   ├── SalesAnalyticsService.java# Business intelligence, revenue, and geographical metrics
    │   │   │   ├── SalesFilterService.java   # Full-text search, dynamic multi-attribute filters, sorting
    │   │   │   └── SalesReportService.java   # CSV report compilation and executive summary generation
    │   │   │
    │   │   ├── controller/                   # REST API Presentation Layer
    │   │   │   ├── SalesController.java      # /api/v1/sales (CRUD, search, filter, pagination)
    │   │   │   ├── DashboardController.java  # /api/v1/dashboard (KPIs, revenue, orders, trends)
    │   │   │   ├── AnalyticsController.java  # /api/v1/analytics (category, geo, size, fulfilment)
    │   │   │   └── ReportController.java     # /api/v1/reports (CSV streaming export, executive reports)
    │   │   │
    │   │   ├── exception/                    # Global Exception Handling
    │   │   │   ├── ResourceNotFoundException.java     # 404 Not Found mapping
    │   │   │   ├── DuplicateResourceException.java    # 409 Conflict mapping
    │   │   │   ├── DatabaseOperationException.java    # 500 Database error wrapper
    │   │   │   └── GlobalExceptionHandler.java        # @RestControllerAdvice with validation errors
    │   │   │
    │   │   └── util/                         # Utilities & Mappers
    │   │       ├── SalesRowMapper.java       # High-performance ResultSet to AmazonSale mapper
    │   │       └── CsvExportUtil.java        # RFC-4180 streaming CSV generator
    │   │
    │   └── resources/
    │       ├── application.properties        # PostgreSQL 16 production datasource configuration
    │       ├── application-h2.properties     # Standalone in-memory zero-configuration profile
    │       ├── schema-postgres.sql           # PostgreSQL DDL table and indexes definition
    │       ├── data-postgres.sql             # Realistic seed dataset for out-of-the-box readiness
    │       ├── schema-h2.sql                 # H2 DDL table definition (PostgreSQL mode)
    │       └── data.sql                      # Fallback seed data
    │
    └── test/
        └── java/com/amazon/sales/
            └── AmazonSalesApplicationTests.java # JUnit 5 integration and mock tests
```

---

## 🗄️ Database Table Schema (`public.amazon_sales`)

| Column Name | PostgreSQL Type | Java Field Type | Description |
| :--- | :--- | :--- | :--- |
| `index_id` | `BIGSERIAL PRIMARY KEY` | `Long` | Unique surrogate database primary key |
| `order_id` | `VARCHAR(50) NOT NULL` | `String` | Amazon Marketplace Order Identifier |
| `date` | `DATE` | `LocalDate` | Order creation date |
| `status` | `VARCHAR(50)` | `String` | Order lifecycle status (Shipped, Delivered, Cancelled) |
| `fulfilment` | `VARCHAR(50)` | `String` | Fulfilment channel (Amazon, Merchant) |
| `sales_channel` | `VARCHAR(50)` | `String` | Sales channel (Amazon.in, Non-Amazon) |
| `ship_service_level` | `VARCHAR(50)` | `String` | Shipping priority (Expedited, Standard) |
| `style` | `VARCHAR(50)` | `String` | Apparel garment style code (e.g., SET389, JNE3787) |
| `sku` | `VARCHAR(100)` | `String` | Unique Stock Keeping Unit |
| `category` | `VARCHAR(50)` | `String` | Vertical classification (Kurta, Set, Western Dress, Top) |
| `size` | `VARCHAR(20)` | `String` | Garment size (XS, S, M, L, XL, XXL, 3XL) |
| `asin` | `VARCHAR(50)` | `String` | Amazon Standard Identification Number |
| `courier_status` | `VARCHAR(50)` | `String` | Logistics carrier status (Shipped, Unshipped, Cancelled) |
| `qty` | `INT DEFAULT 1` | `Integer` | Number of units purchased |
| `currency` | `VARCHAR(10) DEFAULT 'INR'` | `String` | Currency denomination |
| `amount` | `NUMERIC(12,2) DEFAULT 0.00` | `BigDecimal` | Total order monetary amount |
| `ship_city` | `VARCHAR(100)` | `String` | Destination delivery city |
| `ship_state` | `VARCHAR(100)` | `String` | Destination delivery state |
| `ship_postal_code` | `VARCHAR(20)` | `String` | Destination postal pin code |
| `ship_country` | `VARCHAR(50) DEFAULT 'IN'` | `String` | Destination ISO country code |
| `promotion_ids` | `TEXT` | `String` | Applied promotional and coupon campaigns |
| `b2b` | `BOOLEAN DEFAULT FALSE` | `Boolean` | Commercial Business-to-Business flag |
| `fulfilled_by` | `VARCHAR(50)` | `String` | Carrier handler (Easy Ship, Amazon) |

---

## ⚡ Quick Start: Running the Backend

### Option 1: Standalone (Auto-Fallback to H2 In-Memory)
```cmd
cd amazon-sales-management
run.bat
```
* Attempts connection to PostgreSQL on port 5432.
* If PostgreSQL is not active, automatically switches to in-memory H2 PostgreSQL compatibility mode.
* Server starts on: `http://localhost:8080`

### Option 2: Running with PostgreSQL 16 via Docker Compose
```bash
docker-compose up -d
mvn clean spring-boot:run
```
* **PostgreSQL Database**: `localhost:5432` (`db: ecommerce_db`, `user: postgres`, `password: password`)
* **pgAdmin 4 Web Console**: `http://localhost:5050` (`email: admin@amazonsales.com`, `password: admin`)

### Option 3: Manual Maven Command with Profiles
```bash
# Production PostgreSQL
mvn clean spring-boot:run

# In-Memory H2 Profile
mvn clean spring-boot:run -Dspring-boot.run.profiles=h2
```

---

## 📚 Interactive Swagger / OpenAPI Documentation

Once the server is running, navigate to:
* **Interactive Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **OpenAPI 3.0 JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🌐 Complete REST API Reference

### 1. Sales Management API (`/api/v1/sales`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/sales` | Retrieve all sales records (limit 1000) |
| `GET` | `/api/v1/sales/{id}` | Retrieve a specific sale by `index_id` |
| `POST` | `/api/v1/sales` | Create a new sales record with bean validation |
| `PUT` | `/api/v1/sales/{id}` | Update an existing sales transaction |
| `DELETE` | `/api/v1/sales/{id}` | Permanently delete a sales record |
| `GET` | `/api/v1/sales/search?keyword={query}` | Search across Order ID, SKU, Style, ASIN, City, State |
| `GET` | `/api/v1/sales/filter` | Multi-parameter filter (category, status, fulfilment, courier, etc.) |
| `GET` | `/api/v1/sales/paginated` | Server-side pagination and sorting (`page`, `size`, `sortBy`, `sortDirection`) |

#### Sample Create Request (`POST /api/v1/sales`):
```json
{
  "orderId": "402-1234567-8901234",
  "date": "2026-11-16",
  "status": "Shipped",
  "fulfilment": "Amazon",
  "salesChannel": "Amazon.in",
  "shipServiceLevel": "Expedited",
  "style": "SET400",
  "sku": "SET400-KR-NP-L",
  "category": "Set",
  "size": "L",
  "asin": "B09ABC1234",
  "courierStatus": "Shipped",
  "qty": 1,
  "currency": "INR",
  "amount": 799.00,
  "shipCity": "MUMBAI",
  "shipState": "MAHARASHTRA",
  "shipPostalCode": "400001",
  "shipCountry": "IN",
  "b2b": false,
  "fulfilledBy": "Amazon"
}
```

### 2. Dashboard KPI API (`/api/v1/dashboard`)

| Endpoint | Description |
| :--- | :--- |
| `GET /api/v1/dashboard/summary` | Combined metrics: total records, distinct orders, total revenue, average order value |
| `GET /api/v1/dashboard/total-revenue` | Total monetary gross sales (`{"totalRevenue": 2450000.00}`) |
| `GET /api/v1/dashboard/total-orders` | Distinct Amazon order count (`{"totalOrders": 128900}`) |
| `GET /api/v1/dashboard/total-units` | Aggregated physical units sold (`{"totalUnits": 135400}`) |
| `GET /api/v1/dashboard/average-order-value` | Average monetary spend per order (`{"averageOrderValue": 648.50}`) |
| `GET /api/v1/dashboard/sales-overview` | Daily chronological sales trend (date, revenue, order count) |

### 3. Business Analytics API (`/api/v1/analytics`)

| Endpoint | Description |
| :--- | :--- |
| `GET /api/v1/analytics/category-sales` | Revenue, order count, and average unit price per product category |
| `GET /api/v1/analytics/top-products?limit=10` | Top revenue and volume-generating SKUs |
| `GET /api/v1/analytics/sales-by-state?limit=10` | Geographic breakdown by Indian states |
| `GET /api/v1/analytics/sales-by-city?limit=10` | Geographic breakdown by top destination cities |
| `GET /api/v1/analytics/sales-by-size` | Garment sizing volume distribution (XS, S, M, L, XL, XXL, 3XL) |
| `GET /api/v1/analytics/sales-by-channel` | Volume ratio: Amazon.in vs Non-Amazon |
| `GET /api/v1/analytics/fulfilment-analysis` | Fulfilment speed & volume: Amazon FBA vs Merchant |
| `GET /api/v1/analytics/order-status` | Order lifecycle breakdown (Delivered, Shipped, Cancelled, Pending) |
| `GET /api/v1/analytics/courier-status` | Carrier status ratio (Shipped, Unshipped, Cancelled) |
| `GET /api/v1/analytics/monthly-revenue` | Historical month-over-month revenue figures |
| `GET /api/v1/analytics/daily-sales?limit=30` | Daily sales trends for charting |

### 4. Reports & CSV Export API (`/api/v1/reports`)

| Endpoint | Description |
| :--- | :--- |
| `GET /api/v1/reports/sales/csv` | Streams RFC-4180 formatted CSV download with optional filter parameters |
| `GET /api/v1/reports/sales/summary` | Executive performance JSON report containing KPIs, top categories, and top states |
| `GET /api/v1/reports/category` | Category matrix breakdown report |
| `GET /api/v1/reports/location` | Geographic distribution intelligence report |
