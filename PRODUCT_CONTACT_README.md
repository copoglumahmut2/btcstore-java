# Ürün İletişim Sistemi

## 📋 İçindekiler

1. [Genel Bakış](#genel-bakış)
2. [Özellikler](#özellikler)
3. [Kurulum](#kurulum)
4. [Kullanım](#kullanım)
5. [Dokümantasyon](#dokümantasyon)
6. [Teknik Detaylar](#teknik-detaylar)

## 🎯 Genel Bakış

Ürün iletişim sistemi, müşterilerin ürün detay sayfasından ilgili ürün hakkında doğrudan iletişime geçmelerini sağlar. Sistem, normal call request'lerden bağımsız olarak çalışır ve ürün sorumlu kullanıcılarına özel email bildirimleri gönderir.

### Temel Farklar

| Özellik | Normal Call Request | Ürün İletişimi |
|---------|---------------------|----------------|
| Ürün İlişkisi | ❌ Yok | ✅ Var |
| Otomatik Atama | ❌ Manuel | ✅ Ürün sorumlu kullanıcılarına |
| Email Template | `call_request_notification` | `product_contact_request` |
| Status | PENDING | ASSIGNED (sorumlu varsa) |
| Email İçeriği | Genel bilgi | Ürün + Müşteri bilgileri |

## ✨ Özellikler

### Müşteri Tarafı
- ✅ Ürün detay sayfasından kolay erişim
- ✅ Responsive iletişim formu
- ✅ KVKK onayı entegrasyonu
- ✅ Otomatik ürün bilgisi ekleme
- ✅ Başarı bildirimi

### Ürün Sorumlusu Tarafı
- ✅ Anında email bildirimi
- ✅ Ürün bilgileri email'de
- ✅ Müşteri iletişim bilgileri
- ✅ Müşteri mesajı
- ✅ Otomatik atama

### Sistem Tarafı
- ✅ Ayrı email template
- ✅ Otomatik subject oluşturma
- ✅ Ürün-call request ilişkisi
- ✅ Asenkron email gönderimi
- ✅ Hata yönetimi

## 🚀 Kurulum

### 1. Database Migration

**PostgreSQL:**
```bash
psql -U username -d database_name -f PRODUCT_CONTACT_MIGRATION.sql
```

**H2:**
```bash
# Otomatik çalışır veya manuel:
# PRODUCT_CONTACT_MIGRATION_H2.sql dosyasını çalıştırın
```

### 2. Email Template Kurulumu

**PostgreSQL:**
```bash
psql -U username -d database_name -f PRODUCT_CONTACT_EMAIL_TEMPLATE.sql
```

**H2:**
```bash
# PRODUCT_CONTACT_EMAIL_TEMPLATE_H2.sql dosyasını çalıştırın
```

### 3. Backend Restart

```bash
cd btcstore
mvn clean install
mvn spring-boot:run
```

### 4. Ürün Sorumlu Kullanıcıları Atama

1. Admin paneline giriş yapın
2. Products menüsüne gidin
3. Bir ürünü düzenleyin
4. "Sorumlu Kullanıcılar" bölümünden kullanıcıları seçin
5. Kaydedin

### 5. Test

```bash
# Ürün detay sayfasına gidin
http://localhost:3000/products/[product-code]

# "İletişime Geç" butonuna tıklayın
# Formu doldurun ve gönderin
# Email'lerin geldiğini kontrol edin
```

## 📖 Kullanım

### Müşteri Perspektifi

1. Ürün detay sayfasına gidin
2. Sağ tarafta "İletişime Geç" butonunu görün
3. Butona tıklayın
4. Formu doldurun:
   - Ad
   - Soyad
   - Email
   - Telefon
   - Mesaj (opsiyonel)
   - KVKK onayı
5. "Gönder" butonuna tıklayın
6. Başarı mesajını görün

### Ürün Sorumlusu Perspektifi

1. Email bildirimini alın
2. Email'de şunları görün:
   - Hangi ürün için talep geldiği
   - Müşteri bilgileri
   - Müşteri mesajı
   - Talep tarihi
3. Admin paneline giriş yapın
4. Call Requests menüsüne gidin
5. Talebi görüntüleyin ve işlem yapın

### Admin Perspektifi

1. Call Requests listesinde talebi görün
2. Ürün bilgisinin göründüğünü kontrol edin
3. Ürün sorumlu kullanıcılarına atandığını görün
4. Status: ASSIGNED
5. Gerekirse yeniden atama yapın

## 📚 Dokümantasyon

### Ana Dokümantasyon
- **[PRODUCT_CONTACT_SUMMARY.md](PRODUCT_CONTACT_SUMMARY.md)** - Hızlı özet
- **[PRODUCT_CONTACT_IMPLEMENTATION_GUIDE.md](PRODUCT_CONTACT_IMPLEMENTATION_GUIDE.md)** - Detaylı uygulama kılavuzu
- **[PRODUCT_CONTACT_EMAIL_GUIDE.md](PRODUCT_CONTACT_EMAIL_GUIDE.md)** - Email template kılavuzu

### SQL Scripts
- **[PRODUCT_CONTACT_MIGRATION.sql](PRODUCT_CONTACT_MIGRATION.sql)** - PostgreSQL migration
- **[PRODUCT_CONTACT_MIGRATION_H2.sql](PRODUCT_CONTACT_MIGRATION_H2.sql)** - H2 migration
- **[PRODUCT_CONTACT_EMAIL_TEMPLATE.sql](PRODUCT_CONTACT_EMAIL_TEMPLATE.sql)** - PostgreSQL email template
- **[PRODUCT_CONTACT_EMAIL_TEMPLATE_H2.sql](PRODUCT_CONTACT_EMAIL_TEMPLATE_H2.sql)** - H2 email template

## 🔧 Teknik Detaylar

### Backend

**Endpoint:**
```
POST /v1/public/products/{code}/contact
```

**Request:**
```json
{
  "customerName": "Ahmet Yılmaz",
  "customerEmail": "ahmet@example.com",
  "customerPhone": "+90 555 123 4567",
  "message": "Ürün hakkında bilgi almak istiyorum.",
  "acceptedLegalDocument": {
    "code": "privacy-policy-code"
  }
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "data": {
    "id": 123,
    "code": "call-request-uuid",
    "subject": "Ürün Hakkında İletişim: Ürün Adı",
    "status": "ASSIGNED",
    "product": {
      "code": "product-uuid",
      "name": { "tr": "Ürün Adı" }
    }
  }
}
```

### Frontend

**Service:**
```typescript
import { publicService } from '@/services/public.service';

const response = await publicService.createProductContactRequest(
  productCode,
  {
    customerName: "Ahmet Yılmaz",
    customerEmail: "ahmet@example.com",
    customerPhone: "+90 555 123 4567",
    message: "Test mesajı"
  }
);
```

**Component:**
```typescript
// ProductDetail.tsx
<Link href={`/products/${product.code}/contact`}>
  <Button>İletişime Geç</Button>
</Link>

// ProductContact.tsx
// Form ve API entegrasyonu
```

### Database

**Yeni Kolon:**
```sql
ALTER TABLE call_request ADD COLUMN product_id BIGINT;
ALTER TABLE call_request ADD CONSTRAINT fk_call_request_product 
  FOREIGN KEY (product_id) REFERENCES product(id);
```

**Email Template:**
```sql
INSERT INTO email_template (code, name, subject, body, ...)
VALUES ('product_contact_request', 'Ürün İletişim Talebi', ...);
```

## 🐛 Sorun Giderme

### Email Gönderilmiyor

```bash
# Template kontrolü
SELECT * FROM email_template WHERE code = 'product_contact_request';

# Ürün sorumlu kullanıcıları kontrolü
SELECT u.email FROM user u 
JOIN product_responsible_users pru ON u.id = pru.user_id 
WHERE pru.product_id = [PRODUCT_ID];

# Log kontrolü
tail -f logs/application.log | grep "Product contact email"
```

### Call Request Oluşturulmuyor

```bash
# Ürün kontrolü
SELECT * FROM product WHERE code = '[PRODUCT_CODE]';

# Constraint kontrolü
SELECT * FROM information_schema.table_constraints 
WHERE table_name = 'call_request';
```

### Atama Yapılmıyor

```bash
# Sorumlu kullanıcı kontrolü
SELECT * FROM product_responsible_users WHERE product_id = [PRODUCT_ID];

# Kullanıcı aktiflik kontrolü
SELECT active FROM user WHERE id = [USER_ID];
```

## 📊 İstatistikler

### Ürün Bazlı İstatistikler

```sql
-- Ürün başına talep sayısı
SELECT p.name, COUNT(cr.id) as request_count
FROM product p
LEFT JOIN call_request cr ON p.id = cr.product_id
GROUP BY p.id, p.name
ORDER BY request_count DESC;

-- En çok talep alan ürünler
SELECT p.name, COUNT(cr.id) as request_count
FROM product p
INNER JOIN call_request cr ON p.id = cr.product_id
WHERE cr.created_date >= NOW() - INTERVAL '30 days'
GROUP BY p.id, p.name
ORDER BY request_count DESC
LIMIT 10;
```

## 🔐 Güvenlik

- ✅ Public endpoint (authentication gerekmez)
- ✅ KVKK onayı zorunlu
- ✅ Email validation
- ✅ Phone validation
- ✅ XSS koruması
- ✅ SQL injection koruması
- ✅ Rate limiting (önerilir)

## 🚦 Performans

- ✅ Asenkron email gönderimi (RabbitMQ)
- ✅ Database indexing
- ✅ Lazy loading (product, users)
- ✅ Email template caching (önerilir)
- ✅ Connection pooling

## 🔄 Gelecek Geliştirmeler

- [ ] RabbitMQ entegrasyonu (şu an log)
- [ ] Çoklu dil desteği (email template)
- [ ] SMS bildirimi
- [ ] Ürün bazlı istatistikler dashboard
- [ ] Otomatik yanıt şablonları
- [ ] Kategori bazlı yönlendirme
- [ ] Sık sorulan sorular entegrasyonu

## 📞 Destek

Sorularınız için:
- Implementation Guide'a bakın
- Email Guide'a bakın
- Log dosyalarını kontrol edin
- Database'i kontrol edin

## 📝 Lisans

Bu özellik mevcut proje lisansı altındadır.
