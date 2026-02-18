-- Action Type Constraint'ini Güncelleme
-- Bu script call_request_histories tablosundaki action_type constraint'ini günceller

-- Mevcut constraint'leri görmek için (opsiyonel - önce çalıştırın)
-- SELECT constraint_name, check_clause 
-- FROM information_schema.check_constraints 
-- WHERE table_name = 'call_request_histories';

-- Constraint adı genellikle otomatik oluşturulur, birkaç olasılık:
-- CONSTRAINT_1C, CHECK_CONSTRAINT, veya benzeri

-- Seçenek 1: Constraint adını biliyorsanız
ALTER TABLE call_request_histories DROP CONSTRAINT IF EXISTS CONSTRAINT_1C;

-- Seçenek 2: Tüm olası constraint adlarını deneyin
ALTER TABLE call_request_histories DROP CONSTRAINT IF EXISTS constraint_action_type;
ALTER TABLE call_request_histories DROP CONSTRAINT IF EXISTS check_action_type;

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

COMMIT;

-- Test için:
-- SELECT action_type, COUNT(*) 
-- FROM call_request_histories 
-- GROUP BY action_type;
