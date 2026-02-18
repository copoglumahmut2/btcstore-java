-- H2 Database için Action Type Constraint Güncelleme

-- H2'de constraint'i kaldırmak için önce adını bulmalıyız
-- Constraint adı genellikle CONSTRAINT_1C gibi otomatik oluşturulur

-- Mevcut constraint'i kaldır (H2 syntax)
ALTER TABLE call_request_histories DROP CONSTRAINT IF EXISTS CONSTRAINT_1C;
ALTER TABLE call_request_histories DROP CONSTRAINT IF EXISTS CONSTRAINT_1;
ALTER TABLE call_request_histories DROP CONSTRAINT IF EXISTS constraint_action_type;

-- Yeni constraint ekle
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

-- Alternatif: Eğer yukarıdaki çalışmazsa, tabloyu yeniden oluşturun
-- (Dikkat: Bu mevcut history verilerini silecektir!)
/*
DROP TABLE IF EXISTS call_request_histories CASCADE;

CREATE TABLE call_request_histories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    call_request_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL CHECK (action_type IN (
        'CREATED', 
        'ASSIGNED_TO_GROUP', 
        'ASSIGNED_TO_USER', 
        'STATUS_CHANGED', 
        'PRIORITY_CHANGED',
        'EMAIL_SENT', 
        'COMMENT_ADDED', 
        'COMPLETED', 
        'CANCELLED'
    )),
    description VARCHAR(500),
    performed_by_user_id BIGINT,
    performed_by_username VARCHAR(255),
    old_status VARCHAR(50),
    new_status VARCHAR(50),
    comment TEXT,
    code VARCHAR(255) NOT NULL,
    site_id BIGINT NOT NULL,
    created_date TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_date TIMESTAMP,
    last_modified_by VARCHAR(255),
    dtype VARCHAR(31) NOT NULL,
    FOREIGN KEY (call_request_id) REFERENCES call_requests(id),
    FOREIGN KEY (site_id) REFERENCES sites(id)
);
*/
