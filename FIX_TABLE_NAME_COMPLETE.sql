-- Complete Fix for Product-Document Relationship
-- Problem: Backend expects "product_documents" but table might be named differently

-- Step 1: Check what tables exist
SHOW TABLES LIKE '%product%document%';
SHOW TABLES LIKE '%document%product%';

-- Step 2: Drop any wrong tables
DROP TABLE IF EXISTS store_product_documents;
DROP TABLE IF EXISTS document_products;

-- Step 3: Create the correct table
DROP TABLE IF EXISTS product_documents;

CREATE TABLE product_documents (
    product_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, document_id),
    CONSTRAINT fk_product_documents_product 
        FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_documents_document 
        FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_documents_product ON product_documents(product_id);
CREATE INDEX idx_product_documents_document ON product_documents(document_id);

-- Step 4: Insert test data
-- Get first product and first document
INSERT INTO product_documents (product_id, document_id)
SELECT 
    (SELECT id FROM product WHERE deleted = false AND active = true ORDER BY id LIMIT 1),
    (SELECT id FROM document WHERE deleted = false AND active = true ORDER BY id LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product_documents LIMIT 1);

-- Step 5: Verify
SELECT 
    'Table created and data inserted' as status,
    COUNT(*) as total_records 
FROM product_documents;

-- Step 6: View the relationships
SELECT 
    pd.product_id,
    pd.document_id,
    p.code as product_code,
    p.name_tr as product_name,
    d.code as document_code,
    d.title_tr as document_title
FROM product_documents pd
JOIN product p ON pd.product_id = p.id
JOIN document d ON pd.document_id = d.id;

-- Step 7: Get product code for testing
SELECT 
    '=== TEST WITH THIS PRODUCT CODE ===' as info,
    p.code as product_code_to_use,
    p.name_tr as product_name,
    COUNT(pd.document_id) as document_count
FROM product p
JOIN product_documents pd ON p.id = pd.product_id
GROUP BY p.id, p.code, p.name_tr
LIMIT 1;
