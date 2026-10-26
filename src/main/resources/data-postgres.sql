INSERT INTO customers (name, email, tier, loyalty_points) VALUES
                                                              ('Alice Johnson', 'alice.johnson@example.com', 'PLATINUM', 850),
                                                              ('Bob Smith', 'bob.smith@example.com', 'GOLD', 420),
                                                              ('Charlie Davis', 'charlie.davis@example.com', 'SILVER', 150),
                                                              ('Diana Prince', 'diana.prince@example.com', 'REGULAR', 30);

INSERT INTO products (sku, name, category, price, stock_quantity, product_type, weight_kg, download_url, billing_period_months) VALUES
                                                                                                                                    ('ELEC-LPTP-01', 'Pro Laptop 16-Inch', 'Electronics', 1299.99, 25, 'PHYSICAL', 2.15, NULL, 0),
                                                                                                                                    ('ELEC-WCH-02', 'Wireless Noise-Canceling Headphones', 'Electronics', 199.50, 60, 'PHYSICAL', 0.35, NULL, 0),
                                                                                                                                    ('DIGI-EBOOK-01', 'Mastering Advanced Java & Spring Boot', 'Books', 39.99, 9999, 'DIGITAL', 0.0, 'https://cdn.ecommerce.com/downloads/mastering-java.pdf', 0),
                                                                                                                                    ('DIGI-SOFT-02', 'CodeCraft IDE Pro License Key', 'Software', 149.00, 500, 'DIGITAL', 0.0, 'https://cdn.ecommerce.com/downloads/codecraft-key.bin', 0),
                                                                                                                                    ('SUBS-DEV-01', 'Cloud Developer Premium Membership', 'Services', 29.99, 1000, 'SUBSCRIPTION', 0.0, NULL, 1),
                                                                                                                                    ('SUBS-ENT-02', 'Enterprise Architecture Annual Suite', 'Services', 299.00, 200, 'SUBSCRIPTION', 0.0, NULL, 12);

INSERT INTO orders (order_number, customer_id, status, total_amount, discount_applied, priority_level) VALUES
    ('ORD-202610-0001', 1, 'PROCESSING', 1459.48, 80.00, 4);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
                                                                                   (1, 1, 1, 1299.99, 1299.99),
                                                                                   (1, 2, 1, 199.50, 199.50),
                                                                                   (1, 3, 1, 39.99, 39.99);