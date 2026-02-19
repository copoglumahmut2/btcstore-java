-- Fix Product Documents Table Name Issue
-- Backend'de "product_documents" bekliyor ama "store_product_documents" olarak oluşturulmuş

-- OPTION 1: Rename the table (recommended)
RENAME TABLE store_product_documents TO product_documents;

-- OPTION 2: If rename doesn't work, drop and recreate
-- DROP TABLE IF EXISTS store_product_documents;
-- CREATE TABLE product_documents (
--     product_id BIGINT NOT NULL,
--     document_id BIGINT NOT NULL,
--     PRIMARY KEY (product_id, document_id),
--     CONSTRAINT fk_product_documents_product FOREIGN KEY (product_id) 
--         REFERENCES product(id) ON DELETE CASCADE,
--     CONSTRAINT fk_product_documents_document FOREIGN KEY (document_id) 
--         REFERENCES document(id) ON DELETE CASCADE
-- );

-- Verify the table name
SHOW TABLES LIKE '%product_documents%';

-- Check if data exists
SELECT COUNT(*) as total_records FROM product_documents;

-- View the data
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
