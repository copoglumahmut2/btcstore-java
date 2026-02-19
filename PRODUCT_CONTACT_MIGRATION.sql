-- Add product relation to call_request table
-- This allows tracking which product a call request is related to

-- Add product_id column to call_request table
ALTER TABLE call_request 
ADD COLUMN product_id BIGINT;

-- Add foreign key constraint
ALTER TABLE call_request 
ADD CONSTRAINT fk_call_request_product 
FOREIGN KEY (product_id) 
REFERENCES product(id);

-- Add index for better query performance
CREATE INDEX idx_call_request_product_id ON call_request(product_id);

-- Optional: Add comment to document the column
COMMENT ON COLUMN call_request.product_id IS 'Reference to the product this call request is about (optional)';
