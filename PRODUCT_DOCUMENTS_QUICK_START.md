# Ürün Dokümanları - Hızlı Başlangıç

## Sorun: product_documents tablosuna kayıt atamıyorum

### Çözüm: 3 Kolay Adım

## 1️⃣ Tabloyu Oluştur

Database'de şu script'i çalıştır:

```sql
-- SETUP_AND_TEST_COMPLETE.sql dosyasını çalıştır
-- VEYA aşağıdaki komutu çalıştır:

CREATE TABLE product_documents (
    product_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, document_id),
    CONSTRAINT fk_product_documents_product FOREIGN KEY (product_id) 
        REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_documents_document FOREIGN KEY (document_id) 
        REFERENCES document(id) ON DELETE CASCADE
);
```

## 2️⃣ Test Verisi Oluştur

En kolay yol: `SETUP_AND_TEST_COMPLETE.sql` dosyasını çalıştır. Bu:
- Tabloyu oluşturur
- Test dokümanı oluşturur
- İlk ürüne bağlar
- Sonucu gösterir

## 3️⃣ Test Et

1. **Backend'i yeniden başlat**
2. **Frontend'de login ol:** `http://localhost:3000/sales-login`
3. **Ürün detay sayfasına git:** Script'in verdiği product_code'u kullan
4. **Dokümanları gör!**

---

## Manuel Yöntem

Eğer manuel yapmak istersen:

### Adım 1: Mevcut verileri kontrol et

```sql
-- Ürünleri listele
SELECT id, code, name_tr FROM product WHERE deleted = false LIMIT 5;

-- Dokümanları listele
SELECT id, code, title_tr FROM document WHERE deleted = false LIMIT 5;
```

### Adım 2: İlişki kur

```sql
-- Örnek: product_id=1, document_id=1
INSERT INTO product_documents (product_id, document_id) VALUES (1, 1);
```

### Adım 3: Kontrol et

```sql
SELECT 
    p.code as product_code,
    d.title_tr as document_title
FROM product_documents pd
JOIN product p ON pd.product_id = p.id
JOIN document d ON pd.document_id = d.id;
```

---

## Yaygın Hatalar

### ❌ "Cannot add or update a child row: a foreign key constraint fails"

**Sebep:** Girdiğin ID'ler tablolarda yok

**Çözüm:**
```sql
-- ID'lerin var olduğunu kontrol et
SELECT id FROM product WHERE id = 1;
SELECT id FROM document WHERE id = 1;
```

### ❌ "Duplicate entry"

**Sebep:** Bu ilişki zaten var

**Çözüm:**
```sql
-- Mevcut ilişkileri kontrol et
SELECT * FROM product_documents WHERE product_id = 1;
```

### ❌ "Table doesn't exist"

**Sebep:** Tablo oluşturulmamış

**Çözüm:** `SETUP_AND_TEST_COMPLETE.sql` dosyasını çalıştır

---

## Dosya Rehberi

| Dosya | Ne İşe Yarar |
|-------|--------------|
| `SETUP_AND_TEST_COMPLETE.sql` | ⭐ **EN KOLAYI** - Her şeyi yapar |
| `CREATE_PRODUCT_DOCUMENTS_TABLE.sql` | Sadece tablo oluşturur |
| `QUICK_ADD_DOCUMENT_TO_PRODUCT.sql` | Manuel ekleme için adımlar |
| `CREATE_TEST_DOCUMENT.sql` | Test dokümanı oluşturur |
| `PRODUCT_DOCUMENTS_DEBUG_GUIDE.md` | Detaylı troubleshooting |

---

## Hızlı Test Komutu

Tek komutla her şeyi yap:

```sql
-- 1. Tablo oluştur
CREATE TABLE IF NOT EXISTS product_documents (
    product_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, document_id)
);

-- 2. İlk ürüne ilk dokümanı bağla
INSERT INTO product_documents (product_id, document_id)
SELECT 
    (SELECT id FROM product WHERE deleted = false LIMIT 1),
    (SELECT id FROM document WHERE deleted = false LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM product_documents LIMIT 1);

-- 3. Kontrol et
SELECT p.code, d.title_tr 
FROM product_documents pd
JOIN product p ON pd.product_id = p.id
JOIN document d ON pd.document_id = d.id;
```

---

## Sonraki Adımlar

1. ✅ Tabloyu oluştur
2. ✅ Test verisi ekle
3. ✅ Backend'i yeniden başlat
4. ✅ `/sales-login` sayfasından login ol
5. ✅ Ürün detay sayfasına git
6. ✅ Dokümanları gör!

**Sorun devam ederse:** `PRODUCT_DOCUMENTS_DEBUG_GUIDE.md` dosyasına bak
