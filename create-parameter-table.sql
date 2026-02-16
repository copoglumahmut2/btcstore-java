-- Parametre tablosu oluşturma scripti
-- Bu tablo zaten mevcut olabilir, bu durumda bu script'i çalıştırmanıza gerek yoktur

CREATE TABLE IF NOT EXISTS parameter_model (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    value VARCHAR(1000) NOT NULL,
    data_type VARCHAR(50),
    parameter_type VARCHAR(50),
    encrypt BOOLEAN DEFAULT FALSE,
    description_tr VARCHAR(500),
    description_en VARCHAR(500),
    description_de VARCHAR(500),
    description_fr VARCHAR(500),
    description_es VARCHAR(500),
    description_it VARCHAR(500),
    site_id BIGINT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_parameter_code_site UNIQUE (code, site_id),
    CONSTRAINT fk_parameter_site FOREIGN KEY (site_id) REFERENCES site_model(id) ON DELETE CASCADE
);

-- Index oluşturma
CREATE INDEX idx_parameter_code_site ON parameter_model(code, site_id);

-- Örnek parametreler ekleme (opsiyonel)
-- INSERT INTO parameter_model (code, value, data_type, parameter_type, encrypt, description_tr, description_en, site_id)
-- VALUES 
-- ('SITE_NAME', 'BTC Store', 'STRING', 'SYSTEM', FALSE, 'Site adı', 'Site name', 1),
-- ('MAX_UPLOAD_SIZE', '10485760', 'INTEGER', 'SYSTEM', FALSE, 'Maksimum dosya yükleme boyutu (byte)', 'Maximum file upload size (bytes)', 1),
-- ('ENABLE_REGISTRATION', 'true', 'BOOLEAN', 'SYSTEM', FALSE, 'Kullanıcı kaydına izin ver', 'Allow user registration', 1);
