# Ürün İletişim Sistemi - Hızlı Başlangıç

## 🚀 5 Dakikada Kurulum

### 1. Database Migration (2 dakika)

```bash
# PostgreSQL
psql -U username -d database_name -f PRODUCT_CONTACT_MIGRATION.sql
psql -U username -d database_name -f PRODUCT_CONTACT_EMAIL_TEMPLATE.sql

# H2 (otomatik veya manuel)
# PRODUCT_CONTACT_MIGRATION_H2.sql
# PRODUCT_CONTACT_EMAIL_TEMPLATE_H2.sql
```

### 2. Backend Restart (2 dakika)

```bash
cd btcstore
mvn clean install
mvn spring-boot:run
```

### 3. Test (1 dakika)

```bash
# Tarayıcıda aç
http://localhost:3000/products/[product-code]

# "İletişime Geç" butonuna tıkla
# Formu doldur ve gönder
```

## ✅ Kontrol Listesi

- [ ] Database migration çalıştırıldı
- [ ] Email template eklendi
- [ ] Backend başlatıldı
- [ ] Frontend çalışıyor
- [ ] Test başarılı

## 📝 Önemli Notlar

1. **Ürün Sorumlusu Atama:**
   - Admin panelinde ürün düzenle
   - "Sorumlu Kullanıcılar" seç
   - Kaydet

2. **Email Gönderimi:**
   - Ürün sorumlusu varsa → Email gönderilir
   - Ürün sorumlusu yoksa → Normal call request

3. **Email Template:**
   - Code: `product_contact_request`
   - Admin panelinde düzenlenebilir
   - Değişkenler: `{{productName}}`, `{{customerName}}`, vb.

## 🐛 Sorun mu var?

### Email Gönderilmiyor?
```sql
-- Template kontrolü
SELECT * FROM email_template WHERE code = 'product_contact_request';

-- Sorumlu kullanıcı kontrolü
SELECT u.email FROM user u 
JOIN product_responsible_users pru ON u.id = pru.user_id 
WHERE pru.product_id = [PRODUCT_ID];
```

### Call Request Oluşturulmuyor?
```bash
# Log kontrolü
tail -f logs/application.log | grep "Product contact"
```

## 📚 Detaylı Dokümantasyon

- **[PRODUCT_CONTACT_README.md](PRODUCT_CONTACT_README.md)** - Ana dokümantasyon
- **[PRODUCT_CONTACT_IMPLEMENTATION_GUIDE.md](PRODUCT_CONTACT_IMPLEMENTATION_GUIDE.md)** - Detaylı kılavuz
- **[PRODUCT_CONTACT_EMAIL_GUIDE.md](PRODUCT_CONTACT_EMAIL_GUIDE.md)** - Email template kılavuzu
- **[PRODUCT_CONTACT_CHECKLIST.md](PRODUCT_CONTACT_CHECKLIST.md)** - Test checklist

## 🎯 Özet

Ürün iletişim sistemi artık hazır! Müşteriler ürün detay sayfasından iletişime geçebilir, ürün sorumlu kullanıcıları otomatik olarak email alır.

**Anahtar Özellikler:**
- ✅ Otomatik ürün ilişkilendirme
- ✅ Ürün sorumlu kullanıcılarına atama
- ✅ Özel email template
- ✅ Ayrı süreç yönetimi
- ✅ KVKK entegrasyonu
