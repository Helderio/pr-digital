CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    email VARCHAR(320) NOT NULL UNIQUE,
    phone VARCHAR(50),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    city VARCHAR(120),
    address VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS stores (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(80) NOT NULL UNIQUE,
    base_url VARCHAR(500) NOT NULL,
    logo_url VARCHAR(500),
    country VARCHAR(10) NOT NULL DEFAULT 'PT',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT
);

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    store_id INT NOT NULL REFERENCES stores(id),
    source_url VARCHAR(1000) NOT NULL,
    name VARCHAR(300) NOT NULL,
    description TEXT,
    category VARCHAR(80),
    images TEXT,
    variants TEXT,
    price_eur NUMERIC(14,2),
    price_aoa NUMERIC(18,2),
    price_breakdown TEXT,
    imported_by VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    last_synced_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS cart_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    product_id UUID NOT NULL REFERENCES products(id),
    quantity INT NOT NULL,
    selected_variant TEXT,
    price_aoa_snapshot NUMERIC(18,2) NOT NULL,
    added_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_ref VARCHAR(30) NOT NULL UNIQUE,
    user_id UUID NOT NULL REFERENCES users(id),
    items TEXT NOT NULL,
    total_eur NUMERIC(14,2) NOT NULL,
    total_aoa NUMERIC(18,2) NOT NULL,
    profit_aoa NUMERIC(18,2) NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL,
    delivery_address TEXT NOT NULL,
    tracking_number VARCHAR(120),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES orders(id),
    method VARCHAR(40) NOT NULL,
    amount_aoa NUMERIC(18,2) NOT NULL,
    reference VARCHAR(120),
    status VARCHAR(30) NOT NULL,
    confirmed_at TIMESTAMP,
    gateway_response TEXT
);

CREATE TABLE IF NOT EXISTS exchange_rates (
    id SERIAL PRIMARY KEY,
    rate NUMERIC(14,2) NOT NULL,
    margin_pct NUMERIC(6,2) NOT NULL,
    service_fee_aoa NUMERIC(18,2) NOT NULL,
    set_by UUID,
    valid_from TIMESTAMP NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_products_store ON products(store_id);
CREATE INDEX IF NOT EXISTS idx_products_status ON products(status);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_cart_items_user ON cart_items(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_exchange_rates_current ON exchange_rates(is_current);

-- Seed: taxa de câmbio inicial
INSERT INTO exchange_rates (rate, margin_pct, service_fee_aoa, set_by, valid_from, is_current)
VALUES (1020.00, 7.00, 5000.00, null, NOW(), true)
ON CONFLICT DO NOTHING;

-- Seed: lojas iniciais
INSERT INTO stores (name, slug, base_url, country, is_active, display_order) VALUES
('Zara Portugal',  'zara',     'https://www.zara.com/pt',        'PT', true, 1),
('SHEIN',          'shein',    'https://www.shein.com/pt',       'PT', true, 2),
('Pull&Bear',      'pullbear', 'https://www.pullandbear.com/pt', 'PT', true, 3),
('Bershka',        'bershka',  'https://www.bershka.com/pt',     'PT', true, 4),
('Apple Portugal', 'apple',    'https://www.apple.com/pt',       'PT', true, 5),
('Farfetch',       'farfetch', 'https://www.farfetch.com/pt',    'PT', true, 6),
('Temu',           'temu',     'https://www.temu.com/pt',        'PT', true, 7)
ON CONFLICT DO NOTHING;

-- Seed: admin inicial
-- password: admin123 (BCrypt hash)
INSERT INTO users (id, name, email, phone, password_hash, role, city, is_active)
VALUES (gen_random_uuid(), 'Admin PR Digital', 'admin@prdigital.ao',
        '+244900000000',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y',
        'ADMIN', 'Luanda', true)
ON CONFLICT (email) DO NOTHING;
