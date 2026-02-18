-- Site Configuration Top Banner Fields Migration
-- Bu script site_configuration tablosuna üst banner alanlarını ekler

-- Top Banner alanlarını ekle
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_tr VARCHAR(500);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_en VARCHAR(500);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_de VARCHAR(500);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_fr VARCHAR(500);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_es VARCHAR(500);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_it VARCHAR(500);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_bg_color VARCHAR(20);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_color VARCHAR(20);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_link VARCHAR(500);

-- Varsayılan değerleri güncelle (opsiyonel)
UPDATE site_configuration 
SET 
    top_banner_enabled = FALSE,
    top_banner_bg_color = '#1e40af',
    top_banner_text_color = '#ffffff'
WHERE top_banner_enabled IS NULL;

-- Örnek veri (test için - 6 dil desteği)
-- UPDATE site_configuration 
-- SET 
--     top_banner_enabled = TRUE,
--     top_banner_text_tr = '🎉 Yeni ürünlerimizi keşfedin! %20 indirim fırsatını kaçırmayın.',
--     top_banner_text_en = '🎉 Discover our new products! Don''t miss 20% discount opportunity.',
--     top_banner_text_de = '🎉 Entdecken Sie unsere neuen Produkte! Verpassen Sie nicht 20% Rabatt.',
--     top_banner_text_fr = '🎉 Découvrez nos nouveaux produits! Ne manquez pas 20% de réduction.',
--     top_banner_text_es = '🎉 ¡Descubre nuestros nuevos productos! No te pierdas 20% de descuento.',
--     top_banner_text_it = '🎉 Scopri i nostri nuovi prodotti! Non perdere lo sconto del 20%.',
--     top_banner_bg_color = '#1e40af',
--     top_banner_text_color = '#ffffff',
--     top_banner_link = '/products'
-- WHERE id = 1;
