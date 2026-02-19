-- Product Documents Table Creation and Test Data

-- 1. Drop table if exists (for clean setup)
DROP TABLE IF EXISTS product_documents;

-- 2. Create product_documents junction table
CREATE TABLE product_documents (
    product_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, document_id),
    CONSTRAINT fk_product_documents_product FOREIGN KEY (product_id) 
        REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_documents_document FOREIGN KEY (document_id) 
        REFERENCES document(id) ON DELETE CASCADE
);

-- 3. Create indexes for better performance
CREATE INDEX idx_product_documents_product ON product_documents(product_id);
CREATE INDEX idx_product_documents_document ON product_documents(document_id);

-- 4. Check if table was created
SHOW TABLES LIKE 'product_documents';

-- 5. Check table structure
DESCRIBE product_documents;

-- 6. List available products
SELECT id, code, name_tr, deleted FROM product WHERE deleted = false ORDER BY id LIMIT 10;

-- 7. List available documents
SELECT id, code, title_tr, deleted FROM document WHERE deleted = false ORDER BY id LIMIT 10;

-- 8. Insert test data (replace IDs with actual values from above queries)
-- Example:
-- INSERT INTO product_documents (product_id, document_id) VALUES (1, 1);
-- INSERT INTO product_documents (product_id, document_id) VALUES (1, 2);

-- 9. Verify inserted data
SELECT 
    pd.product_id,
    pd.document_id,
    p.code as product_code,
    p.name_tr as product_name,
    d.code as document_code,
    d.title_tr as document_title
FROM product_documents pd
LEFT JOIN product p ON pd.product_id = p.id
LEFT JOIN document d ON pd.document_id = d.id;

-- 10. If you get foreign key constraint error, check if IDs exist:
-- SELECT id FROM product WHERE id = YOUR_PRODUCT_ID;
-- SELECT id FROM document WHERE id = YOUR_DOCUMENT_ID;
