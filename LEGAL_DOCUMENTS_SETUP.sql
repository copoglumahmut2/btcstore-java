-- KVKK/GDPR Legal Documents Setup
-- Bu script yasal dokümanlar için gerekli tabloyu oluşturur ve örnek veriler ekler
-- NOT: Bu dokümanlar yönetim panelinde görüntülenir

-- Tablo oluşturma (eğer yoksa)
CREATE TABLE IF NOT EXISTS legal_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255) UNIQUE, -- Otomatik oluşturulacak
    site_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    
    -- Başlık (Çok dilli)
    title_tr VARCHAR(500),
    title_en VARCHAR(500),
    title_de VARCHAR(500),
    title_fr VARCHAR(500),
    title_es VARCHAR(500),
    title_it VARCHAR(500),
    
    -- İçerik (Çok dilli)
    content_tr TEXT,
    content_en TEXT,
    content_de TEXT,
    content_fr TEXT,
    content_es TEXT,
    content_it TEXT,
    
    -- Kısa açıklama (Opsiyonel)
    short_text_tr VARCHAR(1000),
    short_text_en VARCHAR(1000),
    short_text_de VARCHAR(1000),
    short_text_fr VARCHAR(1000),
    short_text_es VARCHAR(1000),
    short_text_it VARCHAR(1000),
    
    -- Versiyon bilgileri
    version VARCHAR(20) NOT NULL,
    effective_date TIMESTAMP,
    is_current_version BOOLEAN DEFAULT TRUE,
    
    -- Audit alanları
    active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    
    UNIQUE KEY uk_legal_doc_code (code),
    INDEX idx_legal_doc_site (site_id),
    INDEX idx_legal_doc_type (document_type),
    INDEX idx_legal_doc_current (is_current_version),
    
    FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Örnek KVKK Aydınlatma Metni
INSERT INTO legal_documents (
    code, site_id, document_type, 
    title_tr, title_en,
    content_tr, content_en,
    short_text_tr, short_text_en,
    version, effective_date, is_current_version,
    created_by
) VALUES (
    'kvkk-aydinlatma-metni',
    1, -- site_id'yi kendi sisteminize göre ayarlayın
    'KVKK',
    'KVKK Aydınlatma Metni',
    'KVKK Information Text',
    '<h2>Kişisel Verilerin İşlenmesi Hakkında Aydınlatma Metni</h2>
<p>6698 sayılı Kişisel Verilerin Korunması Kanunu ("KVKK") uyarınca, kişisel verileriniz veri sorumlusu sıfatıyla şirketimiz tarafından aşağıda açıklanan kapsamda işlenebilecektir.</p>

<h3>1. Veri Sorumlusu</h3>
<p>Şirket Adı: [ŞİRKET ADI]<br>
Adres: [ADRES]<br>
E-posta: [EMAIL]</p>

<h3>2. İşlenen Kişisel Veriler</h3>
<ul>
<li>Kimlik Bilgileri: Ad, soyad</li>
<li>İletişim Bilgileri: Telefon numarası, e-posta adresi</li>
</ul>

<p>Haklarınızı kullanmak için [EMAIL] adresine başvurabilirsiniz.</p>',
    '<h2>Personal Data Processing Information Text</h2>
<p>In accordance with the Personal Data Protection Law No. 6698 ("KVKK"), your personal data may be processed by our company as the data controller.</p>

<p>To exercise your rights, you can contact [EMAIL].</p>',
    'Kişisel verilerinizin korunması hakkında detaylı bilgi.',
    'Detailed information about the protection of your personal data.',
    '1.0',
    NOW(),
    TRUE,
    'SYSTEM'
);

-- Örnek GDPR Privacy Policy
INSERT INTO legal_documents (
    code, site_id, document_type,
    title_tr, title_en,
    content_tr, content_en,
    short_text_tr, short_text_en,
    version, effective_date, is_current_version,
    display_order,
    created_by
) VALUES (
    'gdpr-privacy-policy',
    1,
    'GDPR',
    'GDPR Gizlilik Politikası',
    'GDPR Privacy Policy',
    '<h2>GDPR Gizlilik Politikası</h2>
<p>Avrupa Birliği Genel Veri Koruma Yönetmeliği (GDPR) kapsamında kişisel verilerinizin korunması bizim için önemlidir.</p>

<h3>1. Veri Sorumlusu</h3>
<p>Şirket: [ŞİRKET ADI]<br>
Adres: [ADRES]<br>
E-posta: [EMAIL]</p>

<h3>2. Toplanan Veriler</h3>
<ul>
<li>İsim ve iletişim bilgileri</li>
<li>İletişim tercihleri</li>
</ul>

<p>Haklarınızı kullanmak için [EMAIL] adresine başvurabilirsiniz.</p>',
    '<h2>GDPR Privacy Policy</h2>
<p>The protection of your personal data is important to us under GDPR.</p>

<p>To exercise your rights, you can contact [EMAIL].</p>',
    'GDPR kapsamında haklarınız ve veri koruma politikamız.',
    'Your rights under GDPR and our data protection policy.',
    '1.0',
    NOW(),
    TRUE,
    'SYSTEM'
);

INSERT INTO legal_documents (
    code, site_id, document_type,
    title_tr, title_en,
    content_tr, content_en,
    short_text_tr, short_text_en,
    version, effective_date, is_current_version,
    created_by
) VALUES (
    'cookie-policy',
    1,
    'COOKIE_POLICY',
    'Çerez Politikası',
    'Cookie Policy',
    '<h2>Çerez Politikası</h2>
<p>Web sitemizde çerezler kullanılmaktadır.</p>

<h3>1. Çerez Nedir?</h3>
<p>Çerezler, web sitelerini ziyaret ettiğinizde cihazınıza kaydedilen küçük metin dosyalarıdır.</p>

<h3>2. Kullanılan Çerez Türleri</h3>
<ul>
<li><strong>Zorunlu Çerezler:</strong> Web sitesinin çalışması için gereklidir</li>
<li><strong>Performans Çerezleri:</strong> Site performansını ölçmek için kullanılır</li>
</ul>',
    '<h2>Cookie Policy</h2>
<p>Cookies are used on our website.</p>

<h3>1. What is a Cookie?</h3>
<p>Cookies are small text files saved to your device when you visit websites.</p>',
    'Web sitemizde kullanılan çerezler hakkında bilgi.',
    'Information about cookies used on our website.',
    '1.0',
    NOW(),
    TRUE,
    'SYSTEM'
);

-- Mevcut kayıtları kontrol etme sorgusu
SELECT 
    id,
    code,
    document_type,
    title_tr,
    short_text_tr,
    version,
    effective_date,
    is_current_version,
    active
FROM legal_documents
WHERE active = TRUE
ORDER BY effective_date DESC, created_date DESC;
