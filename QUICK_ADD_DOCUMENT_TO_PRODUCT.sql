-- Quick Script to Add Document to Product
-- Bu script'i kullanarak hızlıca ürüne doküman ekleyebilirsin

-- ADIM 1: Tabloyu oluştur (eğer yoksa)
CREATE TABLE IF NOT EXISTS product_documents (
    product_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, document_id),
    CONSTRAINT fk_product_documents_product FOREIGN KEY (product_id) 
        REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_documents_document FOREIGN KEY (document_id) 
        REFERENCES document(id) ON DELETE CASCADE
);

-- ADIM 2: Mevcut ürünleri listele
SELECT 
    id,
    code,
    name_tr,
    active,
    deleted
FROM product 
WHERE deleted = false 
ORDER BY id 
LIMIT 10;

-- ADIM 3: Mevcut dokümanları listele
SELECT 
    id,
    code,
    title_tr,
    active,
    deleted
FROM document 
WHERE deleted = false 
ORDER BY id 
LIMIT 10;

-- ADIM 4: Ürüne doküman ekle
-- Yukarıdaki sorgulardan aldığın ID'leri kullan
-- Örnek: product_id = 1, document_id = 1

INSERT INTO product_documents (product_id, document_id) 
VALUES (1, 1);

-- Birden fazla doküman eklemek için:
-- INSERT INTO product_documents (product_id, document_id) VALUES 
-- (1, 1),
-- (1, 2),
-- (1, 3);

-- ADIM 5: Eklenen kayıtları kontrol et
SELECT 
    pd.product_id,
    pd.document_id,
    p.code as product_code,
    p.name_tr as product_name,
    d.code as document_code,
    d.title_tr as document_title,
    d.active as document_active,
    d.deleted as document_deleted
FROM product_documents pd
JOIN product p ON pd.product_id = p.id
JOIN document d ON pd.document_id = d.id
ORDER BY pd.product_id, pd.document_id;

-- ADIM 6: Belirli bir ürünün dokümanlarını görmek için
-- (URUN_ID'yi değiştir)
SELECT 
    d.id,
    d.code,
    d.title_tr,
    d.description_tr,
    d.active,
    d.deleted
FROM document d
JOIN product_documents pd ON d.id = pd.document_id
WHERE pd.product_id = 1  -- URUN_ID'yi buraya yaz
  AND d.deleted = false;

-- HATA ÇÖZÜMLERI:

-- Eğer "Cannot add or update a child row: a foreign key constraint fails" hatası alırsan:
-- 1. product_id'nin product tablosunda var olduğundan emin ol:
SELECT id FROM product WHERE id = 1;

-- 2. document_id'nin document tablosunda var olduğundan emin ol:
SELECT id FROM document WHERE id = 1;

-- Eğer "Duplicate entry" hatası alırsan:
-- Bu ürün-doküman ilişkisi zaten var demektir. Kontrol et:
SELECT * FROM product_documents WHERE product_id = 1 AND document_id = 1;

-- Bir ilişkiyi silmek için:
DELETE FROM product_documents WHERE product_id = 1 AND document_id = 1;

-- Bir ürünün tüm doküman ilişkilerini silmek için:
DELETE FROM product_documents WHERE product_id = 1;

-- Bir dokümanın tüm ürün ilişkilerini silmek için:
DELETE FROM product_documents WHERE document_id = 1;
