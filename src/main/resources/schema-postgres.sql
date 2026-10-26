DROP TABLE IF EXISTS audit_logs CASCADE;
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS customers CASCADE;

-- 1. Customers Table
CREATE TABLE customers (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           email VARCHAR(150) NOT NULL UNIQUE,
                           tier VARCHAR(20) NOT NULL DEFAULT 'REGULAR',
                           loyalty_points INT DEFAULT 0,
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Products Table
CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          sku VARCHAR(50) NOT NULL UNIQUE,
                          name VARCHAR(150) NOT NULL,
                          category VARCHAR(50) NOT NULL,
                          price NUMERIC(10, 2) NOT NULL,
                          stock_quantity INT NOT NULL DEFAULT 0,
                          product_type VARCHAR(30) NOT NULL,
                          weight_kg NUMERIC(5, 2) DEFAULT 0.0,
                          download_url VARCHAR(255),
                          billing_period_months INT DEFAULT 0,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Orders Table
CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        order_number VARCHAR(50) NOT NULL UNIQUE,
                        customer_id BIGINT NOT NULL,
                        status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                        total_amount NUMERIC(12, 2) NOT NULL,
                        discount_applied NUMERIC(10, 2) DEFAULT 0.0,
                        priority_level INT DEFAULT 1,
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT
);

-- 4. Order Items Table
CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             quantity INT NOT NULL,
                             unit_price NUMERIC(10, 2) NOT NULL,
                             subtotal NUMERIC(12, 2) NOT NULL,
                             CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);

-- 5. Audit Logs Table
CREATE TABLE audit_logs (
                            id BIGSERIAL PRIMARY KEY,
                            endpoint VARCHAR(255) NOT NULL,
                            http_method VARCHAR(10) NOT NULL,
                            client_ip VARCHAR(50),
                            status_code INT,
                            execution_time_ms BIGINT,
                            recorded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);