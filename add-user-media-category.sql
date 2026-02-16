-- User profil resmi için media kategorisi ekleme
-- Bu script'i çalıştırmadan önce mevcut site_id'yi kontrol edin

-- Önce site_id'yi bulalım (genellikle localhost için)
-- SELECT id FROM site WHERE code = 'localhost';

-- User media kategorisi ekle (site_id'yi kendi veritabanınızdaki değerle değiştirin)
INSERT INTO cms_category (id, code, description, site_id, created_date, modified_date)
SELECT 
    COALESCE(MAX(id), 0) + 1,
    'user_cms',
    'User Profile Pictures',
    (SELECT id FROM site WHERE code = 'localhost' LIMIT 1),
    NOW(),
    NOW()
FROM cms_category
WHERE NOT EXISTS (
    SELECT 1 FROM cms_category 
    WHERE code = 'user_cms' 
    AND site_id = (SELECT id FROM site WHERE code = 'localhost' LIMIT 1)
);

-- Kontrol için
SELECT * FROM cms_category WHERE code = 'user_cms';
