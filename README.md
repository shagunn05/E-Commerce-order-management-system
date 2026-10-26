# 🛒 Apex Global E-Commerce Order Management System

An enterprise-grade, event-driven Spring Boot 3 & Java 17+ reference application configured for **PostgreSQL** (Docker Compose) and **H2** (In-Memory).

---

## 🏛️ Project Directory Structure

```text
F:\Management system
├── docker-compose.yml                                # PostgreSQL 16 & pgAdmin 4 container services
├── pom.xml                                           # Maven dependencies (Spring Boot 3, PostgreSQL, H2, Web, JDBC)
├── README.md                                         # Complete architectural guide & API documentation
└── src/main
    ├── java/com/ecommerce
    │   ├── EcommerceApplication.java                 # Spring Boot application bootstrap entry point
    │   │
    │   ├── config/                                   # Spring Core & Infrastructure Configuration
    │   │   ├── AppConfig.java                        # Base IoC configurations
    │   │   ├── AsyncConfig.java                      # @EnableAsync thread pool task executor
    │   │   └── WebConfig.java                        # Servlet & Filter Registration Beans
    │   │
    │   ├── domain/event/                             # Asynchronous Domain Events
    │   │   ├── OrderCreatedEvent.java                # Immutable domain event payload
    │   │   └── OrderEventListener.java               # Asynchronous event consumer (@Async @EventListener)
    │   │
    │   ├── dto/                                      # Data Transfer Objects with Bean Validation
    │   │   ├── OrderRequestDto.java                  # Validated order submission payload
    │   │   ├── OrderResponseDto.java                 # Structured order response with item breakdown
    │   │   ├── OrderItemDto.java                     # Line item request DTO
    │   │   ├── CustomerRegistrationDto.java          # New customer registration DTO
    │   │   ├── ProductRequestDto.java                # Product creation DTO
    │   │   └── ErrorResponseDto.java                 # RFC-7807 compliant JSON error payload
    │   │
    │   ├── exception/                                # Custom Domain Exception Hierarchy
    │   │   ├── EcommerceException.java               # Base unchecked domain exception
    │   │   ├── ResourceNotFoundException.java        # 404 Not Found mapping
    │   │   ├── InsufficientStockException.java       # 400 Out of Stock mapping
    │   │   ├── InvalidOrderStateException.java       # 409 State transition conflict mapping
    │   │   ├── DuplicateResourceException.java       # 409 Duplicate resource mapping
    │   │   ├── DatabaseOperationException.java       # Wraps low-level checked SQLException
    │   │   └── GlobalExceptionHandler.java           # Centralized @RestControllerAdvice
    │   │
    │   ├── model/                                    # Pure OOP Domain Models
    │   │   ├── Customer.java                         # Encapsulated patron domain model with loyalty tiers
    │   │   ├── CustomerTier.java                     # Enum (REGULAR, SILVER, GOLD, PLATINUM)
    │   │   ├── Discountable.java                     # Interface for discount calculation contracts
    │   │   ├── Product.java                          # Abstract base product (Encapsulation & Abstraction)
    │   │   ├── PhysicalProduct.java                  # Extends Product (Polymorphic weight & shipping fees)
    │   │   ├── DigitalProduct.java                   # Extends Product (Polymorphic instant download & discounts)
    │   │   ├── SubscriptionProduct.java              # Extends Product (Recurring billing cycles)
    │   │   ├── Order.java                            # Order aggregate root (Collections & Streams calculations)
    │   │   ├── OrderItem.java                        # Line item representation with unit price & subtotals
    │   │   ├── OrderStatus.java                      # Lifecycle state enum with state transition checks
    │   │   └── OrderFulfillmentRequest.java          # Implements Comparable for PriorityQueue dispatch
    │   │
    │   ├── repository/                               # Data Access Layer (Raw JDBC & Abstractions)
    │   │   ├── CustomerRepository.java               # Customer repository interface
    │   │   ├── ProductRepository.java                # Product repository interface
    │   │   ├── OrderRepository.java                  # Order repository interface
    │   │   ├── CustomerJdbcRepository.java           # Pure JDBC customer repository
    │   │   ├── ProductJdbcRepository.java            # Pure JDBC product repository (Polymorphic row mapping)
    │   │   ├── OrderJdbcRepository.java              # Pure JDBC with manual ACID transactions & batching
    │   │   └── InMemoryProductRepository.java        # Fallback repository for Spring @Qualifier demo
    │   │
    │   ├── service/                                  # Business Service Layer
    │   │   ├── CustomerService.java                  # Customer registration & loyalty tier lookup
    │   │   ├── ProductService.java                   # Catalog management & Java Collections Sets/Streams
    │   │   ├── OrderService.java                     # PriorityQueue fulfillment & Event publishing
    │   │   └── AnalyticsService.java                 # Advanced Streams API grouping, stats, & partitioning
    │   │
    │   └── web/controller/                           # Presentation Layer (Spring Boot REST)
    │       ├── CustomerController.java               # Customer registration and query endpoints
    │       ├── ProductController.java                # Product catalog CRUD and price filtering
    │       ├── OrderController.java                  # Order placement and priority queue dispatch
    │       └── AnalyticsController.java              # Real-time metrics and sales valuation
    │
    └── resources/
        ├── application.properties                    # PostgreSQL datasource, connection pool, & server configs
        ├── application-h2.properties                 # In-Memory H2 zero-setup database profile
        ├── schema-postgres.sql                       # PostgreSQL DDL table definitions
        └── data-postgres.sql                         # Initial seed data for products and customers
```

---

## ⚡ Quick Start: How to Run

### Option 1: Running with PostgreSQL via Docker Compose (Recommended)

1. **Start PostgreSQL 16 & pgAdmin 4**:
   ```bash
   docker-compose up -d
   ```
    * **PostgreSQL** runs at: `localhost:5432` (`user: postgres`, `password: password`, `db: ecommercedb`)
    * **pgAdmin 4** runs at: `http://localhost:5050` (`email: admin@ecommerce.com`, `password: admin`)

2. **Launch the Spring Boot Application**:
   ```bash
   mvn clean spring-boot:run
   ```

---

### Option 2: Running with In-Memory H2 (Zero-Setup / No Docker Required)

Run with the `h2` profile enabled:
```bash
mvn clean spring-boot:run -Dspring-boot.run.profiles=h2
```
* **H2 Database Web Console**: `http://localhost:8080/h2-console`
    * **JDBC URL**: `jdbc:h2:mem:ecommercedb`
    * **User Name**: `sa`
    * **Password**: `password`

---

## 📡 REST API Reference

### 1. Customers API (`/api/v1/customers`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/customers` | Register a new customer |
| `GET`  | `/api/v1/customers` | Retrieve all registered customers |
| `GET`  | `/api/v1/customers/{id}` | Find customer by ID |
| `GET`  | `/api/v1/customers/email/{email}` | Find customer by email address |

#### Sample Request: Register Customer
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "David Miller",
    "email": "david.miller@example.com"
  }'
```

---

### 2. Products API (`/api/v1/products`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET`  | `/api/v1/products` | Retrieve all products |
| `GET`  | `/api/v1/products/{id}` | Find product by numerical ID |
| `GET`  | `/api/v1/products/sku/{sku}` | Find product by SKU |
| `GET`  | `/api/v1/products/categories` | Retrieve distinct categories (`Set<String>`) |
| `GET`  | `/api/v1/products/filter?minPrice=10&maxPrice=200` | Filter by price range (Java Streams) |
| `GET`  | `/api/v1/products/low-stock?threshold=10` | Filter low-stock products |
| `POST` | `/api/v1/products` | Create a new Physical, Digital, or Subscription product |
| `PUT`  | `/api/v1/products/{id}/stock?newStock=40` | Update inventory stock |
| `DELETE`| `/api/v1/products/{id}` | Delete product by ID |

#### Sample Request: Create a Physical Product
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "ELEC-TAB-03",
    "name": "Ultra Slim Tablet 11-inch",
    "category": "Electronics",
    "price": 499.99,
    "stockQuantity": 35,
    "productType": "PHYSICAL",
    "weightKg": 0.55
  }'
```

---

### 3. Orders & Priority Queue API (`/api/v1/orders`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/orders` | Place order (ACID JDBC transaction + publishes `OrderCreatedEvent`) |
| `GET`  | `/api/v1/orders` | Retrieve all orders with line item details |
| `GET`  | `/api/v1/orders/{id}` | Find order by ID |
| `GET`  | `/api/v1/orders/number/{orderNumber}` | Find order by Order Number |
| `GET`  | `/api/v1/orders/customer/{customerId}` | List orders for a specific customer |
| `POST` | `/api/v1/orders/dispatch-next` | **Dispatches highest-priority order from PriorityQueue** |
| `GET`  | `/api/v1/orders/fulfillment-queue` | Inspect current pending orders in `PriorityQueue` |
| `PUT`  | `/api/v1/orders/{id}/cancel` | Cancel order & restore inventory stock |
| `PUT`  | `/api/v1/orders/{id}/ship` | Transition order status to SHIPPED |
| `PUT`  | `/api/v1/orders/{id}/deliver` | Transition order status to DELIVERED |

#### Sample Request: Place an Order
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      { "productId": 1, "quantity": 1 },
      { "productId": 3, "quantity": 2 }
    ]
  }'
```

---

### 4. Business Analytics API (`/api/v1/analytics`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET`  | `/api/v1/analytics/order-status-counts` | Order counts grouped by status (`Collectors.groupingBy`) |
| `GET`  | `/api/v1/analytics/revenue-by-status` | Revenue aggregated by status |
| `GET`  | `/api/v1/analytics/valuation-stats` | Summary statistics (`min`, `max`, `average`, `sum`, `count`) |
| `GET`  | `/api/v1/analytics/inventory-valuation` | Total stock value grouped by category |
| `GET`  | `/api/v1/analytics/top-customers?topN=5` | Top spending customers ranked via Streams |

---

### 5. Low-Level Servlets & Audit Filters

* **Legacy HTML Report**: `http://localhost:8080/legacy/order-report` (Direct HTML table view rendered via `HttpServlet`)
* **CSV Export**: `http://localhost:8080/legacy/order-report?format=csv` (Direct CSV download)
* **Servlet Filter Header**: Every HTTP response contains the custom header `X-Execution-Time-Ms` calculated by `RequestAuditFilter`.
* **Health & Metrics**: `http://localhost:8080/actuator/health`
