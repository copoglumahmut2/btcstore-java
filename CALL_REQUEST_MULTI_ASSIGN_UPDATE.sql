-- Call Request Multi-Assignment and Close Feature Update
-- Bu script çoklu atama ve kapatma özelliklerini ekler

-- 1. Junction table oluştur (çoklu grup ataması için)
CREATE TABLE IF NOT EXISTS call_request_assigned_groups (
    call_request_id BIGINT NOT NULL,
    user_group_id BIGINT NOT NULL,
    PRIMARY KEY (call_request_id, user_group_id),
    CONSTRAINT fk_call_request_groups FOREIGN KEY (call_request_id) REFERENCES call_request(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_group FOREIGN KEY (user_group_id) REFERENCES user_group(id) ON DELETE CASCADE
);

-- 2. Junction table oluştur (çoklu kullanıcı ataması için)
CREATE TABLE IF NOT EXISTS call_request_assigned_users (
    call_request_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (call_request_id, user_id),
    CONSTRAINT fk_call_request_users FOREIGN KEY (call_request_id) REFERENCES call_request(id) ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 3. Index'ler ekle (performans için)
CREATE INDEX IF NOT EXISTS idx_call_request_assigned_groups_call_request ON call_request_assigned_groups(call_request_id);
CREATE INDEX IF NOT EXISTS idx_call_request_assigned_groups_user_group ON call_request_assigned_groups(user_group_id);
CREATE INDEX IF NOT EXISTS idx_call_request_assigned_users_call_request ON call_request_assigned_users(call_request_id);
CREATE INDEX IF NOT EXISTS idx_call_request_assigned_users_user ON call_request_assigned_users(user_id);

-- 4. Mevcut verileri migrate et (backward compatibility)
-- Eğer assigned_group varsa, junction table'a ekle
INSERT INTO call_request_assigned_groups (call_request_id, user_group_id)
SELECT cr.id, ug.id 
FROM call_request cr
INNER JOIN user_group ug ON ug.code = cr.assigned_group
WHERE cr.assigned_group IS NOT NULL
ON CONFLICT (call_request_id, user_group_id) DO NOTHING;

-- Eğer assigned_user varsa, junction table'a ekle
INSERT INTO call_request_assigned_users (call_request_id, user_id)
SELECT id, assigned_user_id 
FROM call_request 
WHERE assigned_user_id IS NOT NULL
ON CONFLICT (call_request_id, user_id) DO NOTHING;

COMMIT;

-- Notlar:
-- 1. assigned_group ve assigned_user_id kolonları backward compatibility için korundu
-- 2. Yeni sistemde call_request_assigned_groups ve call_request_assigned_users junction table'ları kullanılıyor
-- 3. CLOSED status'ü eklendi (Java enum'ında)
-- 4. Mail gönderimi grup atamasında da çalışacak şekilde güncellendi
-- 5. UserGroupModel ile ilişki kuruldu (ManyToMany)
