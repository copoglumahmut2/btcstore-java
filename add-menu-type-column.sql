-- Add menu_type column to menu_link_item table
ALTER TABLE menu_link_item 
ADD COLUMN menu_type VARCHAR(20) NOT NULL DEFAULT 'PUBLIC';

-- Update existing records
-- Admin panel menüleri için (dashboard, users, settings, vb.)
UPDATE menu_link_item 
SET menu_type = 'ADMIN_PANEL' 
WHERE code IN ('dashboard', 'content', 'banners', 'categories', 'products', 'system', 'users', 'settings', 'menus');

-- Diğer tüm menüler PUBLIC olarak kalacak (zaten default değer)

-- Index ekle (performans için)
CREATE INDEX idx_menu_link_item_menu_type ON menu_link_item(menu_type);
