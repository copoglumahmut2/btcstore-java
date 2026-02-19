# Ürün Dokümanları Debug Kılavuzu

## Sorun: Dokümanlar Gelmiyor

### Adım 1: Database Kontrolü

```sql
-- 1. product_documents tablosu var mı?
SHOW TABLES LIKE 'product_documents';

-- 2. Tablo yoksa oluştur
CREATE TABLE IF NOT EXISTS product_documents (
    product_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, document_id),
    CONSTRAINT fk_product_documents_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_documents_document FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE
);

-- 3. Mevcut ürünleri listele
SELECT id, code, name_tr FROM product WHERE deleted = false LIMIT 5;

-- 4. Mevcut dokümanları listele
SELECT id, code, title_tr FROM document WHERE deleted = false LIMIT 5;

-- 5. Test için bir ürüne doküman ekle (ID'leri yukarıdan al)
INSERT INTO product_documents (product_id, document_id) 
VALUES (
    (SELECT id FROM product WHERE code = 'URUN_KODU' LIMIT 1),
    (SELECT id FROM document WHERE code = 'DOKUMAN_KODU' LIMIT 1)
);

-- 6. Ürünün dokümanlarını kontrol et
SELECT d.id, d.code, d.title_tr, d.deleted
FROM document d
JOIN product_documents pd ON d.id = pd.document_id
WHERE pd.product_id = (SELECT id FROM product WHERE code = 'URUN_KODU' LIMIT 1);
```

### Adım 2: Backend Kontrolü

1. **Backend'i yeniden başlat** (değişiklikler için)
2. **Endpoint'i test et:**

```bash
# Login ol ve token al
curl -X POST http://localhost:9090/webapp/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# Token ile dokümanları çek
curl -X GET http://localhost:9090/webapp/api/v1/products/URUN_KODU/documents \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

3. **Backend loglarını kontrol et:**
   - "Inside getProductDocuments" mesajını ara
   - Hata varsa stack trace'i kontrol et

### Adım 3: Frontend Kontrolü

1. **Browser Console'u aç** (F12)
2. **Login ol:** `/sales-login` sayfasına git
3. **Ürün detay sayfasına git**
4. **Console'da şunları kontrol et:**
   ```
   Loading documents for product: URUN_KODU
   Is authenticated: true
   Fetching documents for product: URUN_KODU
   Document API response: {...}
   Parsed document data: [...]
   Documents received: [...]
   ```

### Adım 4: Network Tab Kontrolü

1. **Network tab'ı aç** (F12 > Network)
2. **Ürün detay sayfasını yenile**
3. **`/documents` isteğini bul**
4. **Kontrol et:**
   - Status: 200 OK olmalı
   - Response: `{status: "SUCCESS", data: [...]}`
   - Headers: `Authorization: Bearer ...` olmalı

### Yaygın Sorunlar ve Çözümleri

#### 1. Token Yok / Geçersiz
**Belirti:** 401 Unauthorized
**Çözüm:** 
- Logout olup tekrar login ol
- Cookie'leri temizle
- `localStorage.clear()` yap

#### 2. Endpoint Bulunamadı
**Belirti:** 404 Not Found
**Çözüm:**
- Backend'i yeniden başlat
- URL'i kontrol et: `/v1/products/{code}/documents`

#### 3. Doküman Yok
**Belirti:** Boş array `[]`
**Çözüm:**
- Database'de ürüne doküman ekle
- `deleted = false` olduğundan emin ol

#### 4. CORS Hatası
**Belirti:** CORS policy error
**Çözüm:**
- Backend CORS ayarlarını kontrol et
- Frontend API URL'ini kontrol et

#### 5. isAuthenticated False
**Belirti:** Doküman bölümü hiç görünmüyor
**Çözüm:**
- `useAuthStore` initialize oldu mu kontrol et
- PublicLayout'ta `initializeAuth()` çağrılıyor mu kontrol et
- Cookie'de token var mı kontrol et

### Test Senaryosu

1. ✅ Database'de `product_documents` tablosu var
2. ✅ En az 1 ürün ve 1 doküman var
3. ✅ Ürün-doküman ilişkisi kurulmuş
4. ✅ Backend çalışıyor
5. ✅ Frontend çalışıyor
6. ✅ `/sales-login` sayfasından login olundu
7. ✅ Token cookie'ye kaydedildi
8. ✅ Ürün detay sayfasına gidildi
9. ✅ Console'da "Loading documents" mesajı görünüyor
10. ✅ Network'te `/documents` isteği 200 dönüyor
11. ✅ Dokümanlar ekranda görünüyor

### Hızlı Test Komutu

```sql
-- Hızlı test için örnek veri
INSERT INTO product_documents (product_id, document_id) 
SELECT 
    (SELECT id FROM product WHERE deleted = false LIMIT 1),
    (SELECT id FROM document WHERE deleted = false LIMIT 1)
WHERE NOT EXISTS (
    SELECT 1 FROM product_documents LIMIT 1
);

-- Kontrol
SELECT 
    p.code as product_code,
    p.name_tr as product_name,
    d.code as document_code,
    d.title_tr as document_title
FROM product_documents pd
JOIN product p ON pd.product_id = p.id
JOIN document d ON pd.document_id = d.id
LIMIT 5;
```
