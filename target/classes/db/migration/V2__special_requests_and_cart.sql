CREATE TABLE special_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    store_id INTEGER NOT NULL REFERENCES stores(id),
    source_url TEXT NOT NULL,
    detected_name VARCHAR(255),
    detected_description TEXT,
    detected_images TEXT,
    detected_variants TEXT,
    detected_price_eur NUMERIC(14,2),
    calculated_price_aoa NUMERIC(18,2),
    price_breakdown TEXT,
    selected_variant TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'ANALYSING',
    cart_item_id UUID,
    product_id UUID REFERENCES products(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_special_requests_user ON special_requests(user_id);
CREATE INDEX idx_special_requests_status ON special_requests(status);
CREATE INDEX idx_special_requests_store ON special_requests(store_id);

ALTER TABLE cart_items
    ADD COLUMN item_type VARCHAR(20) NOT NULL DEFAULT 'CATALOG',
    ADD COLUMN special_request_id UUID REFERENCES special_requests(id);

ALTER TABLE cart_items ALTER COLUMN product_id DROP NOT NULL;

ALTER TABLE cart_items ADD CONSTRAINT cart_items_product_or_special_chk CHECK (
    (item_type = 'CATALOG' AND product_id IS NOT NULL AND special_request_id IS NULL)
    OR (item_type = 'SPECIAL_REQUEST' AND special_request_id IS NOT NULL AND product_id IS NULL)
);
