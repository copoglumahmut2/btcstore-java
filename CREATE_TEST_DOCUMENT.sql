-- Create Test Document for Product Documents Feature

-- 1. Check current site_id
SELECT id, code, name FROM site LIMIT 1;

-- 2. Create a test document (adjust site_id if needed)
INSERT INTO document (
    code,
    title_tr,
    title_en,
    description_tr,
    description_en,
    active,
    deleted,
    site_id,
    created_date,
    last_modified_date
) VALUES (
    CONCAT('TEST_DOC_', UNIX_TIMESTAMP()),  -- Unique code
    'Test Dokümanı',                         -- Turkish title
    'Test Document',                         -- English title
    'Bu bir test dokümanıdır',              -- Turkish description
    'This is a test document',              -- English description
    true,                                    -- active
    false,                                   -- deleted
    1,                                       -- site_id (adjust if needed)
    NOW(),                                   -- created_date
    NOW()                                    -- last_modified_date
);

-- 3. Get the created document ID
SELECT 
    id,
    code,
    title_tr,
    description_tr,
    active,
    deleted
FROM document 
WHERE code LIKE 'TEST_DOC_%'
ORDER BY id DESC 
LIMIT 1;

-- 4. Now you can link this document to a product
-- Get a product ID first:
SELECT id, code, name_tr FROM product WHERE deleted = false LIMIT 1;

-- 5. Link document to product (replace IDs)
-- INSERT INTO product_documents (product_id, document_id) 
-- VALUES (
--     (SELECT id FROM product WHERE deleted = false LIMIT 1),
--     (SELECT id FROM document WHERE code LIKE 'TEST_DOC_%' ORDER BY id DESC LIMIT 1)
-- );

-- 6. Verify the link
SELECT 
    p.code as product_code,
    p.name_tr as product_name,
    d.code as document_code,
    d.title_tr as document_title
FROM product_documents pd
JOIN product p ON pd.product_id = p.id
JOIN document d ON pd.document_id = d.id
WHERE d.code LIKE 'TEST_DOC_%';

-- COMPLETE EXAMPLE (all in one):
-- This will create a document and link it to the first available product

-- Step 1: Create document
INSERT INTO document (
    code,
    title_tr,
    title_en,
    description_tr,
    description_en,
    active,
    deleted,
    site_id,
    created_date,
    last_modified_date
)
SELECT 
    CONCAT('TEST_DOC_', UNIX_TIMESTAMP()),
    'Ürün Kullanım Kılavuzu',
    'Product User Manual',
    'Bu doküman ürünün kullanım kılavuzunu içerir',
    'This document contains the product user manual',
    true,
    false,
    s.id,
    NOW(),
    NOW()
FROM site s
LIMIT 1;

-- Step 2: Link to first product
INSERT INTO product_documents (product_id, document_id)
SELECT 
    p.id,
    d.id
FROM product p
CROSS JOIN document d
WHERE p.deleted = false
  AND d.code LIKE 'TEST_DOC_%'
  AND d.id = (SELECT MAX(id) FROM document WHERE code LIKE 'TEST_DOC_%')
LIMIT 1;

-- Step 3: Verify
SELECT 
    p.id as product_id,
    p.code as product_code,
    p.name_tr as product_name,
    d.id as document_id,
    d.code as document_code,
    d.title_tr as document_title
FROM product_documents pd
JOIN product p ON pd.product_id = p.id
JOIN document d ON pd.document_id = d.id
WHERE d.code LIKE 'TEST_DOC_%';
