-- Localhost için site ekleme SQL'i
-- Not: Tablo yapınıza göre düzenleyin

-- Örnek 1: Basit site ekleme
INSERT INTO site (id, code, name, domain, language_id, active, created_date, created_by)
VALUES (1, 'LOCALHOST', 'Localhost Site', 'localhost', 1, true, NOW(), 'SYSTEM');

-- Örnek 2: Eğer language tablosu varsa önce dil ekleyin
INSERT INTO language (id, code, name, active)
VALUES (1, 'tr', 'Türkçe', true)
ON CONFLICT (id) DO NOTHING;

-- Sonra site'ı ekleyin
INSERT INTO site (id, code, name, domain, language_id, active, created_date, created_by)
VALUES (1, 'LOCALHOST', 'Localhost Site', 'localhost', 1, true, NOW(), 'SYSTEM')
ON CONFLICT (id) DO NOTHING;

-- Örnek 3: Eğer mevcut bir site varsa güncelle
UPDATE site 
SET domain = 'localhost' 
WHERE code = 'DEFAULT' OR id = 1;

-- Kontrol için
SELECT * FROM site WHERE domain = 'localhost';
