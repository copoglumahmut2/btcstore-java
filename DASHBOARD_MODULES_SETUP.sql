-- Dashboard Modules Table
CREATE TABLE dashboard_modules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    name_tr VARCHAR(255),
    name_en VARCHAR(255),
    name_de VARCHAR(255),
    name_fr VARCHAR(255),
    name_es VARCHAR(255),
    name_it VARCHAR(255),
    description_tr VARCHAR(500),
    description_en VARCHAR(500),
    description_de VARCHAR(500),
    description_fr VARCHAR(500),
    description_es VARCHAR(500),
    description_it VARCHAR(500),
    link VARCHAR(255) NOT NULL,
    icon VARCHAR(50) NOT NULL,
    display_order INT DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    show_count BOOLEAN DEFAULT TRUE,
    search_item_type VARCHAR(100), -- SearchService için model tipi (örn: BannerModel, ProductModel)
    search_filters TEXT, -- JSON formatında filtreler
    module_type VARCHAR(50) DEFAULT 'CARD', -- CARD, QUICK_ACTION
    site_id BIGINT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT dashboard_modules_unique_keys UNIQUE (code, site_id),
    CONSTRAINT fk_dashboard_modules_site FOREIGN KEY (site_id) REFERENCES sites(id)
);

CREATE INDEX dashboard_modules_code_idx ON dashboard_modules(code, site_id);

-- Dashboard Modules - User Groups İlişki Tablosu
CREATE TABLE dashboard_modules_user_groups (
    dashboard_module_id BIGINT NOT NULL,
    user_group_id BIGINT NOT NULL,
    PRIMARY KEY (dashboard_module_id, user_group_id),
    CONSTRAINT fk_dm_ug_module FOREIGN KEY (dashboard_module_id) REFERENCES dashboard_modules(id) ON DELETE CASCADE,
    CONSTRAINT fk_dm_ug_group FOREIGN KEY (user_group_id) REFERENCES user_groups(id) ON DELETE CASCADE
);

-- Insert default CARD modules (site_id = 1 olarak varsayıyorum, kendi site_id'nizi kullanın)
INSERT INTO dashboard_modules (code, name_tr, name_en, link, icon, display_order, active, show_count, search_item_type, module_type, site_id) VALUES
('banners', 'Banner Yönetimi', 'Banner Management', '/admin/banners', 'Image', 1, TRUE, TRUE, 'BannerModel', 'CARD', 1),
('categories', 'Kategori Yönetimi', 'Category Management', '/admin/categories', 'FolderTree', 2, TRUE, TRUE, 'CategoryModel', 'CARD', 1),
('products', 'Ürün Yönetimi', 'Product Management', '/admin/products', 'Package', 3, TRUE, TRUE, 'ProductModel', 'CARD', 1),
('references', 'Referans Yönetimi', 'Reference Management', '/admin/references', 'Users', 4, TRUE, TRUE, 'ReferenceModel', 'CARD', 1),
('stories', 'Başarı Hikayeleri', 'Success Stories', '/admin/stories', 'BookOpen', 5, TRUE, TRUE, 'SuccessStoryModel', 'CARD', 1),
('callRequests', 'Arama Talepleri', 'Call Requests', '/admin/call-requests', 'MessageSquare', 6, TRUE, TRUE, 'CallRequestModel', 'CARD', 1),
('productContactForms', 'Ürün İletişim Formları', 'Product Contact Forms', '/admin/product-contacts', 'FileText', 7, TRUE, TRUE, 'ProductContactModel', 'CARD', 1),
('partners', 'Partner Yönetimi', 'Partner Management', '/admin/partners', 'Handshake', 8, TRUE, TRUE, 'PartnerModel', 'CARD', 1),
('sectors', 'Sektör Yönetimi', 'Sector Management', '/admin/sectors', 'Building', 9, TRUE, TRUE, 'SectorModel', 'CARD', 1),
('menus', 'Menü Yönetimi', 'Menu Management', '/admin/menus', 'Menu', 10, TRUE, TRUE, 'MenuModel', 'CARD', 1),
('emailTemplates', 'Email Şablonları', 'Email Templates', '/admin/email-templates', 'Mail', 11, TRUE, TRUE, 'EmailTemplateModel', 'CARD', 1),
('legalDocuments', 'Yasal Dokümanlar', 'Legal Documents', '/admin/legal-documents', 'FileText', 12, TRUE, TRUE, 'LegalDocumentModel', 'CARD', 1),
('users', 'Kullanıcı Yönetimi', 'User Management', '/admin/users', 'Users', 13, TRUE, TRUE, 'UserModel', 'CARD', 1),
('userGroups', 'Kullanıcı Grupları', 'User Groups', '/admin/user-groups', 'UsersRound', 14, TRUE, TRUE, 'UserGroupModel', 'CARD', 1),
('userRoles', 'Kullanıcı Rolleri', 'User Roles', '/admin/user-roles', 'Shield', 15, TRUE, TRUE, 'UserRoleModel', 'CARD', 1),
('parameters', 'Parametreler', 'Parameters', '/admin/parameters', 'Settings', 16, TRUE, TRUE, 'ParameterModel', 'CARD', 1),
('siteConfiguration', 'Site Ayarları', 'Site Configuration', '/admin/site-configuration', 'Globe', 17, TRUE, TRUE, 'SiteConfigurationModel', 'CARD', 1);

-- Insert default QUICK_ACTION modules
INSERT INTO dashboard_modules (code, name_tr, name_en, description_tr, description_en, link, icon, display_order, active, show_count, module_type, site_id) VALUES
('viewCallRequests', 'Arama Taleplerini Görüntüle', 'View Call Requests', 'Tüm arama taleplerini görüntüle ve yönet', 'View and manage all call requests', '/admin/call-requests', 'Phone', 1, TRUE, FALSE, 'QUICK_ACTION', 1),
('viewProductContacts', 'Ürün İletişim Formları', 'Product Contact Forms', 'Ürün hakkında gelen iletişim formlarını görüntüle', 'View product contact form submissions', '/admin/product-contacts', 'MessageSquare', 2, TRUE, FALSE, 'QUICK_ACTION', 1),
('manageSiteSettings', 'Site Ayarlarını Yönet', 'Manage Site Settings', 'Logo, banner ve genel site ayarlarını düzenle', 'Edit logo, banner and general site settings', '/admin/site-configuration', 'Settings', 3, TRUE, FALSE, 'QUICK_ACTION', 1),
('manageUsers', 'Kullanıcı Yönetimi', 'User Management', 'Kullanıcıları ve yetkilerini yönet', 'Manage users and permissions', '/admin/users', 'UserCog', 4, TRUE, FALSE, 'QUICK_ACTION', 1);

-- Örnek: Belirli modülleri belirli user group'lara atama
-- Önce user_group id'lerini bulun, sonra aşağıdaki gibi ilişkilendirin:
-- 
-- ADMIN grubuna tüm modülleri ver (user_group_id = 1 varsayımı)
-- INSERT INTO dashboard_modules_user_groups (dashboard_module_id, user_group_id)
-- SELECT id, 1 FROM dashboard_modules WHERE site_id = 1;
--
-- EDITOR grubuna sadece content modüllerini ver (user_group_id = 2 varsayımı)
-- INSERT INTO dashboard_modules_user_groups (dashboard_module_id, user_group_id)
-- SELECT id, 2 FROM dashboard_modules WHERE code IN ('banners', 'categories', 'products', 'stories') AND site_id = 1;
