-- Product Documents Table Creation for H2 Database

-- 1. Drop table if exists
DROP TABLE IF EXISTS product_documents;

-- 2. Create product_documents junction table (H2 syntax)
CREATE TABLE product_documents (
    product_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, document_id)
);

-- 3. Add foreign key constraints
ALTER TABLE product_documents 
ADD CONSTRAINT fk_product_documents_product 
FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE;

ALTER TABLE product_documents 
ADD CONSTRAINT fk_product_documents_document 
FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE;

-- 4. Create indexes
CREATE INDEX idx_product_documents_product ON product_documents(product_id);
CREATE INDEX idx_product_documents_document ON product_documents(document_id);

-- 5. Insert test data (adjust IDs as needed)
-- First, check what products and documents exist:
-- SELECT id, code, name_tr FROM product WHERE deleted = false;
-- SELECT id, code, title_tr FROM document WHERE deleted = false;

-- Then insert:
-- INSERT INTO product_documents (product_id, document_id) VALUES (1, 1);
