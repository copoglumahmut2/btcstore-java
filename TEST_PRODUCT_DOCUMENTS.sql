-- Test için ürüne doküman ekleme
-- Önce mevcut ürün ve dokümanları kontrol et

-- Mevcut ürünleri listele
SELECT id, code, name_tr FROM product WHERE deleted = false LIMIT 5;

-- Mevcut dokümanları listele
SELECT id, code, name FROM document WHERE deleted = false LIMIT 5;

-- Test için bir ürüne doküman ekle (ID'leri yukarıdaki sorgulardan al)
-- Örnek:
-- INSERT INTO product_documents (product_id, document_id) 
-- VALUES (1, 1);

-- Ürünün dokümanlarını kontrol et
-- SELECT d.* 
-- FROM document d
-- JOIN product_documents pd ON d.id = pd.document_id
-- WHERE pd.product_id = 1;
