-- Add legal document relation to call_request table
-- This allows tracking which version of KVKK/GDPR document was accepted by the customer

-- Add foreign key column
ALTER TABLE call_request 
ADD COLUMN legal_document_id BIGINT NULL;

-- Add foreign key constraint
ALTER TABLE call_request 
ADD CONSTRAINT fk_call_request_legal_document 
FOREIGN KEY (legal_document_id) 
REFERENCES legal_documents(id);

-- Add index for better query performance
CREATE INDEX idx_call_request_legal_document 
ON call_request(legal_document_id);

-- Add comment for documentation
COMMENT ON COLUMN call_request.legal_document_id IS 'Reference to the legal document (KVKK/GDPR) version that was accepted by the customer';
