-- Call Request Priority Field Ekleme
-- Bu script call_requests tablosuna priority kolonu ekler

-- Priority kolonu ekle
ALTER TABLE call_requests 
ADD COLUMN priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM';

-- Priority için index ekle (performans için)
CREATE INDEX idx_call_requests_priority ON call_requests(priority);

-- Action Type constraint'ini güncelle (PRIORITY_CHANGED ekle)
-- Önce mevcut constraint'i kaldır
ALTER TABLE call_request_histories DROP CONSTRAINT IF EXISTS constraint_action_type;

-- Yeni constraint'i ekle (PRIORITY_CHANGED dahil)
ALTER TABLE call_request_histories 
ADD CONSTRAINT constraint_action_type 
CHECK (action_type IN (
    'CREATED', 
    'ASSIGNED_TO_GROUP', 
    'ASSIGNED_TO_USER', 
    'STATUS_CHANGED', 
    'PRIORITY_CHANGED',
    'EMAIL_SENT', 
    'COMMENT_ADDED', 
    'COMPLETED', 
    'CANCELLED'
));

-- Mevcut kayıtları güncelle (opsiyonel - zaten default MEDIUM)
-- UPDATE call_requests SET priority = 'MEDIUM' WHERE priority IS NULL;

-- Priority değerleri: LOW, MEDIUM, HIGH, URGENT

COMMIT;

-- Örnek kullanım:
-- UPDATE call_requests SET priority = 'HIGH' WHERE id = 123;
-- UPDATE call_requests SET priority = 'URGENT' WHERE customer_email LIKE '%vip%';
