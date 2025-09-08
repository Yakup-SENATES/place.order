CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- PRODUCTS
CREATE TABLE products (
                          id UUID PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          stock INT NOT NULL
);

-- ORDERS
CREATE TABLE orders (
                        id UUID PRIMARY KEY,
                        status VARCHAR(50) NOT NULL
);

-- ORDER LINES
CREATE TABLE order_lines (
                             id BIGSERIAL PRIMARY KEY,
                             order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                             product_id UUID NOT NULL,
                             product_name VARCHAR(255) NOT NULL,
                             quantity INT NOT NULL,
                             unit_price NUMERIC(15, 2) NOT NULL,
                             line_total NUMERIC(15, 2) NOT NULL
);

-- IDEMPOTENCY KEYS
CREATE TABLE idempotency_keys (
                                  id BIGSERIAL PRIMARY KEY,
                                  key_value VARCHAR(255) UNIQUE NOT NULL,
                                  order_id UUID NOT NULL REFERENCES orders(id),
                                  created_at TIMESTAMP DEFAULT now()
);

-- OUTBOX EVENTS
CREATE TABLE outbox_events (
                               id BIGSERIAL PRIMARY KEY,
                               type VARCHAR(255) NOT NULL,
                               aggregated_id VARCHAR(255) NOT NULL,
                               payload TEXT NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT now(),
                               published BOOLEAN DEFAULT FALSE
);