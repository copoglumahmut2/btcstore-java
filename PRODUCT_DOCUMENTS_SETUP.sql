-- Product Documents Feature Setup
-- This script creates the necessary table for linking products with documents

-- Create product_documents junction table
CREATE TABLE IF NOT EXISTS product_documents (
    product_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, document_id),
    CONSTRAINT fk_product_documents_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_documents_document FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_product_documents_product ON product_documents(product_id);
CREATE INDEX IF NOT EXISTS idx_product_documents_document ON product_documents(document_id);

-- Example: Link a document to a product
-- INSERT INTO product_documents (product_id, document_id) 
-- VALUES (
--     (SELECT id FROM product WHERE code = 'PRODUCT_CODE'),
--     (SELECT id FROM document WHERE code = 'DOCUMENT_CODE')
-- );
