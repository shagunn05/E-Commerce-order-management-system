# Apex Commerce — Modern Order Management System (Frontend)
Enterprise-grade, responsive React.js administrator dashboard designed for the **Apex Commerce Order Management System**, synchronized with domain aggregates, DTOs, and REST controllers of the Java Spring Boot backend.
---
## 🚀 Quick Start Guide
### 1. Prerequisites
Ensure **Node.js** (v18+ or LTS) and **npm** (or `pnpm` / `yarn`) are installed on your machine.
If you don't have Node.js installed yet, download the LTS installer from [nodejs.org](https://nodejs.org/) or install via Windows Winget:
```powershell
winget install OpenJS.NodeJS.LTS
```
### 2. Navigate to Frontend Directory
```powershell
cd "F:\Management system\frontend"
```
### 3. Install Dependencies
```powershell
npm install
```
### 4. Start Local Development Server
```powershell
npm run dev
```
The application will launch immediately at:
👉 **`http://localhost:5173`** (or `http://127.0.0.1:5173`)
### 5. Production Build
```powershell
npm run build
npm run preview
```
---
├── .env.example                      # Reference template for environments
├── .gitignore                        # Git exclusion rules for node_modules & dist
├── index.html                        # Entry HTML with typography & favicon
├── package.json                      # Project metadata, dependencies & scripts
├── vite.config.js                    # Vite bundler configuration with React plugin
├── public/
│   └── favicon.svg                   # Brand SVG icon
└── src/
    ├── App.jsx                       # Main application routing and context providers
    ├── index.css                     # Complete design system & responsive styling
    ├── main.jsx                      # DOM mount point
    ├── components/
    │   ├── Charts.jsx                # Recharts: Area, Pie, Vertical & Horizontal Bar Charts
    │   ├── DataTable.jsx             # Sortable, paginated enterprise data table
    │   ├── EmptyState.jsx            # Empty search/filter placeholder
    │   ├── FilterDropdown.jsx        # Styled selector dropdown
    │   ├── LoadingSpinner.jsx        # Data fetching loading indicator
    │   ├── Modal.jsx                 # Accessible dialog with blur backdrop & transitions
    │   ├── Navbar.jsx                # Top app bar with Quick Priority Dispatch & admin profile
    │   ├── ProductCard.jsx           # Card view with stock alerts & SKU badges
    │   ├── SearchBar.jsx             # Instant search input with clear button
    │   ├── Sidebar.jsx               # Dark navy collapsible navigation menu
    │   ├── StatCard.jsx              # Executive metric card with trend deltas
    │   ├── StatusBadge.jsx           # Semantic badge for Order Status & Product Types
    │   └── TierBadge.jsx             # Customer Loyalty Tier badge (Regular, Silver, Gold, Platinum)
    ├── context/
    │   ├── SettingsContext.jsx       # Admin profile, store policies, & mock toggle (persisted)
    │   └── ToastContext.jsx          # Non-blocking floating toast notification system
    ├── data/
    │   ├── customers.js              # Realistic customer profiles matching Customer.java
    │   ├── orders.js                 # Realistic multi-item orders matching Order.java
    │   └── products.js               # Products covering PHYSICAL, DIGITAL, and SUBSCRIPTION
    ├── layouts/
    │   └── DashboardLayout.jsx       # Persistent sidebar + navbar wrapper
    ├── pages/
    │   ├── AnalyticsPage.jsx         # Executive sales analytics, valuation & leaderboards
    │   ├── CustomersPage.jsx         # Customer directory, loyalty progression & points award
    │   ├── DashboardPage.jsx         # Operations overview, revenue trajectory & recent orders
    │   ├── OrdersPage.jsx            # Order management, state transitions & PriorityQueue fulfillment
    │   ├── ProductsPage.jsx          # Catalog management, search, filters & CRUD modals
    │   └── SettingsPage.jsx          # Store policies, admin profile & API endpoints inspector
    ├── services/
    |    ├── apiClient.js              # Axios instance with interceptors and error handling
    │   ├── config.js                 # API baseURL configuration & latency simulation
    │   ├── endpoints.js              # REST endpoints mirroring Spring Boot controllers
    │   ├── config.js                 # API baseURL configuration & latency simulation
    │   ├── endpoints.js              # REST endpoints mirroring Spring Boot controllers
    │   ├── analyticsService.js       # Business intelligence & statistics service
    │   ├── customersService.js       # Customer registry & points mutation service
    │   ├── ordersService.js          # Order dispatch, transitions & retrieval service
    │   └── productsService.js        # Catalog CRUD & stock inventory service
    └── utils/
        ├── analytics.js              # Java Streams-style aggregations and summaries
        ├── dates.js                  # Date parsers and month aggregators
        ├── domain.js                 # Domain invariants, transitions & color mappings
        ├── format.js                 # Currency, number, percentage & date formatters
        └── pricing.js                # Polymorphic discounts & shipping fee calculators
```
---
## 🧩 Domain Invariants & Spring Boot Synchronization
| Domain Feature | Java Spring Boot Backend Implementation | Frontend React Implementation |
| :--- | :--- | :--- |
| **Product Types** | `PhysicalProduct` (weight shipping), `DigitalProduct` (download URL), `SubscriptionProduct` (monthly cycles) | Dynamic Add/Edit form fields, specialized Product Cards, and accurate shipping calculations |
| **Order Status Machine** | `OrderStatus.java` with `canTransitionTo()` enforcement | Enforced transitions: `PENDING` → `PROCESSING`/`CANCELLED`, `PROCESSING` → `SHIPPED`/`CANCELLED`, `SHIPPED` → `DELIVERED` |
| **Loyalty Tiers** | `CustomerTier.java`: `REGULAR` (0%), `SILVER` (5%), `GOLD` (10%), `PLATINUM` (15%) | `TierBadge`, live discount calculations, and interactive tier progression bar towards milestone goals |
| **Fulfillment Priority Queue** | `PriorityQueue<OrderFulfillmentRequest>` in `OrderService` | "Dispatch Priority" button in Navbar and fulfillment queue inspector |
| **Discounts & Free Shipping** | Free shipping over $100; extra 5% bulk discount for 5+ items | `utils/pricing.js` accurately models domain rules in order totals |
---
## ⚙️ Switching from Mock Data to Live Backend
When your Spring Boot backend server is running on `http://localhost:8080`:
1. Open `frontend/.env`
2. Change `VITE_USE_MOCK_DATA` to `false`:
```env
VITE_API_BASE_URL=http://localhost:8080
VITE_USE_MOCK_DATA=false
```
3. Restart Vite (`npm run dev`) or toggle it directly inside the **Settings → Backend & API Endpoints** page without restarting!