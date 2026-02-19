-- COMPLETE SETUP AND TEST SCRIPT
-- Bu script'i çalıştırarak tüm kurulumu ve test verisini oluşturabilirsin

-- ============================================
-- STEP 1: CREATE TABLE
-- ============================================
DROP TABLE IF EXISTS product_documents;

CREATE TABLE product_documents (
    product_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, document_id),
    CONSTRAINT fk_product_documents_product FOREIGN KEY (product_id) 
        REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_documents_document FOREIGN KEY (document_id) 
        REFERENCES document(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_documents_product ON product_documents(product_id);
CREATE INDEX idx_product_documents_document ON product_documents(document_id);

SELECT 'Table created successfully' as status;

-- ============================================
-- STEP 2: CREATE TEST DOCUMENT
-- ============================================
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
    'Bu doküman ürünün detaylı kullanım kılavuzunu içerir',
    'This document contains detailed product user manual',
    true,
    false,
    s.id,
    NOW(),
    NOW()
FROM site s
LIMIT 1;

SELECT 'Test document created' as status;

-- ============================================
-- STEP 3: LINK DOCUMENT TO FIRST PRODUCT
-- ============================================
INSERT INTO product_documents (product_id, document_id)
SELECT 
    p.id,
    d.id
FROM product p
CROSS JOIN document d
WHERE p.deleted = false
  AND p.active = true
  AND d.code LIKE 'TEST_DOC_%'
  AND d.id = (SELECT MAX(id) FROM document WHERE code LIKE 'TEST_DOC_%')
LIMIT 1;

SELECT 'Document linked to product' as status;

-- ============================================
-- STEP 4: VERIFY SETUP
-- ============================================
SELECT 
    '=== VERIFICATION ===' as info,
    COUNT(*) as total_links
FROM product_documents;

SELECT 
    p.id as product_id,
    p.code as product_code,
    p.name_tr as product_name,
    d.id as document_id,
    d.code as document_code,
    d.title_tr as document_title,
    d.description_tr as document_description
FROM product_documents pd
JOIN product p ON pd.product_id = p.id
JOIN document d ON pd.document_id = d.id
ORDER BY pd.product_id, pd.document_id;

-- ============================================
-- STEP 5: GET PRODUCT CODE FOR TESTING
-- ============================================
SELECT 
    '=== USE THIS PRODUCT CODE FOR TESTING ===' as info,
    p.code as product_code_to_test,
    p.name_tr as product_name,
    COUNT(pd.document_id) as document_count
FROM product p
LEFT JOIN product_documents pd ON p.id = pd.product_id
WHERE p.id IN (SELECT product_id FROM product_documents)
GROUP BY p.id, p.code, p.name_tr
LIMIT 1;

-- ============================================
-- CLEANUP (if needed)
-- ============================================
-- To remove test data, uncomment and run:
-- DELETE FROM product_documents WHERE document_id IN (SELECT id FROM document WHERE code LIKE 'TEST_DOC_%');
-- DELETE FROM document WHERE code LIKE 'TEST_DOC_%';
