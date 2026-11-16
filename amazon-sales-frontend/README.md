# 🛒 Amazon Sales Analytics & Management Platform (Frontend)

An enterprise-grade, high-performance Business Intelligence & Order Management dashboard built with **React.js, Vite, and Recharts**, styled with modern CSS & Tailwind, connecting seamlessly to the Spring Boot 3 REST API.

---

## 🏛️ Directory Structure

```text
amazon-sales-frontend/
│
├── package.json               # Dependencies (React 19, Vite, Recharts, Lucide-React, Axios)
├── vite.config.js             # Vite configuration with proxy to http://localhost:8080
├── index.html                 # Single page application entry template
│
└── src/
    ├── api/                   # Dedicated Axios API clients
    │   ├── axiosInstance.js   # Configured baseURL, headers, and error interceptors
    │   ├── salesApi.js        # CRUD, search, filter, and paginated calls
    │   ├── dashboardApi.js    # High-level KPIs, revenue, and daily trends
    │   ├── analyticsApi.js    # Category, geography, size, and courier metrics
    │   └── reportsApi.js      # CSV streaming download & executive summaries
    │
    ├── components/            # Reusable UI component library
    │   ├── Navbar.jsx         # Top navigation bar with database status & CSV export
    │   ├── Sidebar.jsx        # Navigation sidebar with branding & routes
    │   ├── StatCard.jsx       # Interactive KPI cards with trend indicators
    │   ├── DataTable.jsx      # Sortable, paginated, responsive data table
    │   ├── SearchBar.jsx      # Debounced search bar with clear action
    │   ├── Charts.jsx         # AreaChart, BarChart, and PieChart components
    │   ├── SaleModal.jsx      # Add / Edit Amazon sales modal with 23 validated fields
    │   ├── DeleteConfirmModal.jsx # Destructive action confirmation dialog
    │   ├── StatusBadge.jsx    # Domain color-coded status badges
    │   ├── LoadingSpinner.jsx # Smooth CSS loading spinner
    │   └── EmptyState.jsx     # Visual feedback when no data matches criteria
    │
    ├── pages/                 # Full dashboard views
    │   ├── Dashboard.jsx      # Executive overview: KPIs, trends, categories, recent sales
    │   ├── SalesManagement.jsx# Full CRUD table: pagination, search, multi-filters, add/edit/delete
    │   ├── Analytics.jsx      # Deep BI: category matrix, state & city sales, sizes, fulfilment
    │   ├── Products.jsx       # SKU & Style intelligence, top revenue products, catalog view
    │   ├── Orders.jsx         # Live order monitoring: logistics, courier status, B2B tracking
    │   └── Reports.jsx        # Customizable CSV report generator & executive summaries
    │
    ├── hooks/                 # Custom React hooks
    │   ├── useSales.js        # Sales state, server-side pagination, and CRUD handlers
    │   ├── useDashboard.js    # Real-time dashboard KPI state management
    │   └── useAnalytics.js    # Multi-dimensional analytics fetcher
    │
    ├── context/               # Application-level Context Providers
    │   ├── ToastContext.jsx   # Global success, error, and info toast notifications
    │   └── SettingsContext.jsx# Theme and user configuration provider
    │
    ├── utils/                 # Utility helpers
    │   ├── format.js          # Currency (₹ INR), date, number, and percentage formatters
    │   ├── domain.js          # Status badge classes and domain constants
    │   └── dates.js           # Date range calculations
    │
    ├── App.jsx                # React Router setup with 6 main business views
    ├── App.css                # Component-specific styles
    ├── index.css              # Global design system, CSS variables, and layout classes
    └── main.jsx               # React DOM root entry point
```

---

## ⚡ Quick Start: Running the Frontend

### 1. Development Server
```bash
cd amazon-sales-frontend
npm run dev
```
* **Local Web Dashboard**: [http://localhost:5173](http://localhost:5173)
* Automatically proxies `/api` calls to the Spring Boot backend at `http://localhost:8080`.

### 2. Production Build
```bash
npm run build
npm run preview
```
* Generates an optimized production bundle inside `dist/`.

---

## 🖥️ Screen Views & Features

### 1. Executive Dashboard (`/`)
* **Real-Time KPIs**: Total Revenue, Distinct Orders, Units Fulfilled, Average Order Value (AOV).
* **Revenue Trend Chart**: Interactive Recharts AreaChart tracking revenue over time.
* **Category Breakdown**: Donut chart displaying revenue distribution across product verticals.
* **Fulfilment Ratio**: Bar chart comparing Amazon FBA vs Merchant fulfilment.
* **Quick Add Sale**: One-click modal to insert a new sales transaction directly into PostgreSQL.

### 2. Sales Management (`/sales`)
* **Server-Side Pagination**: Efficient navigation across thousands of records.
* **Multi-Attribute Filters**: Filter simultaneously by Category, Order Status, Fulfilment, Courier Status, Date Range, Min/Max Amount, and B2B flag.
* **Real-Time Full-Text Search**: Instant search matching Order ID, SKU, Style, ASIN, City, or State.
* **Full CRUD Operations**:
  * **Add Sale**: Complete 23-column modal with input validation.
  * **Edit Sale**: Modify existing records with pre-populated values.
  * **Delete Sale**: Confirmation modal with immediate PostgreSQL database row deletion.

### 3. Business Analytics (`/analytics`)
* **Category Matrix**: Interactive bar charts and detailed tabular breakdown of revenue, units, and average price.
* **Geographical Distribution**: Horizontal bar charts displaying Top States and Top Cities across India.
* **Garment Size Analytics**: Volume distribution across XS, S, M, L, XL, XXL, and 3XL.
* **Sales Channel Breakdown**: Market share ratio between Amazon.in and non-Amazon channels.
* **Monthly Revenue Trajectory**: Month-over-month revenue tracking.

### 4. Products & SKUs (`/products`)
* **Top SKU Leaderboard**: Identifies top revenue-generating and highest-volume SKUs.
* **Style & Category Intelligence**: Discover top styles (Kurta, Set, Western Dress) and unit velocities.
* **Direct Navigation**: "View Orders" button immediately filters the sales ledger for any chosen SKU.

### 5. Order Monitoring (`/orders`)
* **Carrier Tracking**: Status breakdown across Courier Shipped, Unshipped, and Cancelled orders.
* **Order Lifecycle**: Distinct views for Delivered, In-Transit, Cancelled, and Pending orders.
* **B2B vs B2C Indicator**: Visual badges distinguishing wholesale enterprise orders.

### 6. Business Reports (`/reports`)
* **Custom CSV Export**: Select date ranges, category, and status filters, then stream an RFC-4180 CSV directly from the PostgreSQL backend.
* **Executive Summary Report**: Formatted KPI summary with timestamp, top categories, and top states.
* **Location Intelligence**: High-level geographic performance rankings.
