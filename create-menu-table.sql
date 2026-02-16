-- Menu Management Table Creation Script
-- This script creates the menus table and related junction table

-- Create menus table
CREATE TABLE IF NOT EXISTS menus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    site_id BIGINT NOT NULL,
    name_tr VARCHAR(255),
    name_en VARCHAR(255),
    name_de VARCHAR(255),
    name_fr VARCHAR(255),
    name_es VARCHAR(255),
    name_it VARCHAR(255),
    icon VARCHAR(100),
    display_order INT,
    is_root BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    url VARCHAR(500),
    parent_menu_id BIGINT,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    CONSTRAINT menus_unique_keys UNIQUE (code, site_id),
    CONSTRAINT fk_menu_site FOREIGN KEY (site_id) REFERENCES sites(id),
    CONSTRAINT fk_menu_parent FOREIGN KEY (parent_menu_id) REFERENCES menus(id) ON DELETE CASCADE
);

-- Create index for better performance
CREATE INDEX menus_code_idx ON menus(code, site_id);

-- Create junction table for menu-usergroup relationship
CREATE TABLE IF NOT EXISTS menu_user_groups (
    menu_id BIGINT NOT NULL,
    user_group_id BIGINT NOT NULL,
    PRIMARY KEY (menu_id, user_group_id),
    CONSTRAINT fk_menu_usergroups_menu FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE,
    CONSTRAINT fk_menu_usergroups_usergroup FOREIGN KEY (user_group_id) REFERENCES usergroups(id) ON DELETE CASCADE
);

-- Sample data (optional)
-- INSERT INTO menus (code, site_id, name_tr, name_en, icon, display_order, is_root, url, created_by, created_date)
-- VALUES 
-- ('dashboard', 1, 'Kontrol Paneli', 'Dashboard', '📊', 1, TRUE, '/admin/dashboard', 'system', NOW()),
-- ('content', 1, 'İçerik Yönetimi', 'Content Management', '📝', 2, TRUE, NULL, 'system', NOW()),
-- ('banners', 1, 'Bannerlar', 'Banners', '🖼️', 1, FALSE, '/admin/banners', 'system', NOW()),
-- ('categories', 1, 'Kategoriler', 'Categories', '📁', 2, FALSE, '/admin/categories', 'system', NOW());

-- Update parent_menu_id for sub-menus
-- UPDATE menus SET parent_menu_id = (SELECT id FROM menus WHERE code = 'content' LIMIT 1) WHERE code IN ('banners', 'categories');
