# Ürün İletişim Sistemi - Uygulama Kılavuzu

Bu dokümantasyon, ürün detay sayfasından iletişim kurma özelliğinin nasıl uygulandığını açıklar.

## Genel Bakış

Kullanıcılar artık ürün detay sayfasından ilgili ürün hakkında iletişime geçebilirler. Bu özellik mevcut Call Request sistemini kullanır ve ürün bilgisini otomatik olarak ilişkilendirir.

**ÖNEMLİ:** Ürün iletişimi normal call request'lerden ayrı bir süreç izler:
- Ürün sorumlu kullanıcılarına özel email template ile bildirim gönderilir
- Call request otomatik olarak ürün sorumlu kullanıcılarına atanır
- Status otomatik olarak "ASSIGNED" yapılır
- Email template: `product_contact_request`

## Backend Değişiklikleri

### 1. Database Migration

**PostgreSQL için:**
```sql
-- PRODUCT_CONTACT_MIGRATION.sql dosyasını çalıştırın
ALTER TABLE call_request ADD COLUMN product_id BIGINT;
ALTER TABLE call_request ADD CONSTRAINT fk_call_request_product FOREIGN KEY (product_id) REFERENCES product(id);
CREATE INDEX idx_call_request_product_id ON call_request(product_id);
```

**H2 Database için:**
```sql
-- PRODUCT_CONTACT_MIGRATION_H2.sql dosyasını çalıştırın
-- Aynı SQL komutları H2 için de geçerlidir
```

### 2. Email Template Kurulumu

**PostgreSQL için:**
```sql
-- PRODUCT_CONTACT_EMAIL_TEMPLATE.sql dosyasını çalıştırın
-- Ürün iletişimi için özel email template oluşturulur
```

**H2 Database için:**
```sql
-- PRODUCT_CONTACT_EMAIL_TEMPLATE_H2.sql dosyasını çalıştırın
```

**Email Template Özellikleri:**
- Template Code: `product_contact_request`
- Ürün bilgilerini içerir (ad, kod, açıklama)
- Müşteri bilgilerini içerir (ad, email, telefon)
- Müşteri mesajını içerir
- Responsive HTML tasarım
- Değişkenler: `{{productName}}`, `{{productCode}}`, `{{customerName}}`, vb.

### 3. Model Değişiklikleri

**StoreCallRequestModel.java**
- `ProductModel product` field'ı eklendi
- `@ManyToOne` ilişkisi ile product tablosuna bağlandı

**StoreCallRequestData.java**
- `ProductData product` field'ı eklendi
- Frontend ile backend arasında ürün bilgisi taşınır

### 4. Facade Değişiklikleri

**CallRequestFacade.java**
```java
CallRequestData createProductContactRequest(String productCode, CallRequestData callRequestData);
```

**CallRequestFacadeImpl.java**
- Yeni metod implement edildi
- Ürün kodu ile ürün bulunur ve call request'e bağlanır
- Subject otomatik olarak oluşturulur: "Ürün Hakkında İletişim: [Ürün Adı]"
- KVKK dokümanı varsa otomatik olarak bağlanır
- **Ürün sorumlu kullanıcıları varsa:**
  - Call request otomatik olarak bu kullanıcılara atanır
  - Status "ASSIGNED" olarak ayarlanır
  - Özel email template ile bildirim gönderilir
- **Email Gönderimi:**
  - Template: `product_contact_request`
  - Alıcılar: Ürün sorumlu kullanıcılarının email adresleri
  - Değişkenler: Ürün bilgileri, müşteri bilgileri, mesaj

### 5. Controller Değişiklikleri

**PublicController.java**
```java
@PostMapping("/products/{code}/contact")
public ServiceResponseData createProductContactRequest(
    @PathVariable String code,
    @Validated @RequestBody CallRequestData callRequestData,
    @RequestParam(required = false) String isoCode
)
```

## Frontend Değişiklikleri

### 1. Public Service

**public.service.ts**
```typescript
// Ürün için iletişim talebi oluştur
async createProductContactRequest(productCode: string, data: {
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  message?: string;
  acceptedLegalDocument?: { code: string };
})

// Güncel KVKK dokümanını getir
async getCurrentPrivacyPolicy()
```

### 2. ProductContact Component

**ProductContact.tsx**
- Backend API'ye bağlandı (mock data yerine)
- Ürün bilgisi backend'den yüklenir
- KVKK dokümanı backend'den yüklenir
- Form gönderimi backend'e yapılır
- Başarılı gönderim sonrası ürün detay sayfasına yönlendirilir

### 3. ProductDetail Component

**ProductDetail.tsx**
- Sağ tarafta iletişim bölümü eklendi
- "İletişime Geç" butonu ile `/products/{code}/contact` sayfasına yönlendirir
- Responsive tasarım (mobilde üstte, desktop'ta sağda)

## API Endpoints

### Ürün İletişim Talebi Oluştur

**Endpoint:** `POST /v1/public/products/{code}/contact`

**Authentication:** Gerekli değil (Public endpoint)

**Path Parameters:**
- `code`: Ürün kodu (UUID)

**Request Body:**
```json
{
  "customerName": "Ahmet Yılmaz",
  "customerEmail": "ahmet@example.com",
  "customerPhone": "+90 555 123 4567",
  "message": "Bu ürün hakkında detaylı bilgi almak istiyorum.",
  "acceptedLegalDocument": {
    "code": "privacy-policy-uuid"
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
    "customerName": "Ahmet Yılmaz",
    "customerEmail": "ahmet@example.com",
    "customerPhone": "+90 555 123 4567",
    "subject": "Ürün Hakkında İletişim: Ürün Adı",
    "message": "Bu ürün hakkında detaylı bilgi almak istiyorum.",
    "status": "PENDING",
    "priority": "MEDIUM",
    "product": {
      "code": "product-uuid",
      "name": {
        "tr": "Ürün Adı"
      }
    },
    "acceptedLegalDocument": {
      "code": "privacy-policy-uuid",
      "title": {
        "tr": "Gizlilik Politikası"
      }
    }
  }
}
```

### Güncel KVKK Dokümanını Getir

**Endpoint:** `GET /v1/public/legal-documents/privacy-policy/current`

**Authentication:** Gerekli değil (Public endpoint)

**Response:**
```json
{
  "status": "SUCCESS",
  "data": {
    "code": "privacy-policy-uuid",
    "title": {
      "tr": "Gizlilik Politikası",
      "en": "Privacy Policy"
    },
    "shortText": {
      "tr": "KVKK kapsamında kişisel verilerinizin işlenmesini kabul ediyorum.",
      "en": "I accept the processing of my personal data under GDPR."
    },
    "content": {
      "tr": "<p>Detaylı KVKK metni...</p>",
      "en": "<p>Detailed GDPR text...</p>"
    }
  }
}
```

## Kullanım Senaryosu

1. Kullanıcı ürün detay sayfasına gider
2. Sağ tarafta (veya mobilde üstte) "İletişime Geç" butonunu görür
3. Butona tıklayarak iletişim formuna yönlendirilir
4. Form otomatik olarak ürün bilgisini içerir
5. Kullanıcı adını, e-postasını, telefonunu ve mesajını girer
6. KVKK onayını verir
7. Form gönderilir
8. Backend'de:
   - Call request oluşturulur
   - Ürün bilgisi otomatik olarak bağlanır
   - Subject otomatik oluşturulur
   - KVKK dokümanı bağlanır
   - **Ürün sorumlu kullanıcıları varsa:**
     - Call request bu kullanıcılara atanır
     - Status: ASSIGNED olarak ayarlanır
     - Özel email template ile bildirim gönderilir
   - **Ürün sorumlusu yoksa:**
     - Status: PENDING olarak ayarlanır
     - Normal call request süreci işler
9. Kullanıcı başarı mesajı görür ve ürün detay sayfasına yönlendirilir
10. **Ürün sorumlu kullanıcıları email alır:**
    - Hangi ürün için iletişim kurulduğu
    - Müşteri bilgileri (ad, email, telefon)
    - Müşteri mesajı
    - Talep tarihi

## Admin Panelinde Görüntüleme

Admin panelinde call request'ler görüntülenirken:
- Ürün bilgisi varsa gösterilir
- Hangi ürün için iletişim kurulduğu görülür
- Normal call request'lerden ayırt edilebilir
- Ürün sorumlu kullanıcılarına atanmış olarak görünür
- Status: ASSIGNED (ürün sorumlusu varsa) veya PENDING (yoksa)

## Email Template Değişkenleri

Ürün iletişim email template'inde kullanılabilir değişkenler:

| Değişken | Açıklama | Örnek |
|----------|----------|-------|
| `{{productName}}` | Ürün adı | "Akıllı Telefon X1" |
| `{{productCode}}` | Ürün kodu | "PROD-12345" |
| `{{productDescription}}` | Ürün kısa açıklaması | "En yeni teknoloji..." |
| `{{customerName}}` | Müşteri adı soyadı | "Ahmet Yılmaz" |
| `{{customerEmail}}` | Müşteri email | "ahmet@example.com" |
| `{{customerPhone}}` | Müşteri telefon | "+90 555 123 4567" |
| `{{message}}` | Müşteri mesajı | "Bu ürün hakkında..." |
| `{{subject}}` | Talep konusu | "Ürün Hakkında İletişim: ..." |
| `{{createdDate}}` | Oluşturulma tarihi | "15.01.2024 14:30" |

## Özellikler

1. **Otomatik Ürün İlişkilendirme**: Ürün kodu ile otomatik olarak ürün bulunur ve bağlanır
2. **Otomatik Subject Oluşturma**: "Ürün Hakkında İletişim: [Ürün Adı]" formatında
3. **KVKK Entegrasyonu**: Güncel KVKK dokümanı otomatik olarak yüklenir ve bağlanır
4. **Responsive Tasarım**: Mobil ve desktop'ta optimize edilmiş görünüm
5. **Hata Yönetimi**: Ürün bulunamazsa veya hata oluşursa kullanıcıya bilgi verilir
6. **Loading States**: Form gönderimi sırasında buton disable edilir
7. **Otomatik Atama**: Ürün sorumlu kullanıcılarına otomatik atama
8. **Özel Email Template**: Ürün iletişimi için özel tasarlanmış email
9. **Email Bildirimi**: Ürün sorumlu kullanıcılarına anında email gönderimi
10. **Ayrı Süreç**: Normal call request'lerden bağımsız işlem akışı

## Test

### Backend Test

```bash
# Ürün için iletişim talebi oluştur
curl -X POST http://localhost:8080/v1/public/products/PRODUCT-CODE/contact \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Test User",
    "customerEmail": "test@example.com",
    "customerPhone": "+90 555 123 4567",
    "message": "Test message",
    "acceptedLegalDocument": {
      "code": "PRIVACY-POLICY-CODE"
    }
  }'

# KVKK dokümanını getir
curl http://localhost:8080/v1/public/legal-documents/privacy-policy/current
```

### Frontend Test

1. Ürün detay sayfasına gidin: `/products/[product-code]`
2. "İletişime Geç" butonuna tıklayın
3. Formu doldurun ve gönderin
4. Başarı mesajını kontrol edin
5. Admin panelinde call request'i görüntüleyin
6. **Email kontrolü:**
   - Ürün sorumlu kullanıcılarının email kutularını kontrol edin
   - Email template'in doğru render edildiğini kontrol edin
   - Ürün ve müşteri bilgilerinin doğru göründüğünü kontrol edin

### Email Template Test

1. Admin panelinde Email Templates sayfasına gidin
2. `product_contact_request` template'ini bulun
3. Template'i düzenleyin ve test edin
4. Değişkenlerin doğru çalıştığını kontrol edin

## Notlar

- Ürün ilişkisi opsiyoneldir, normal call request'ler de oluşturulabilir
- Subject otomatik oluşturulur ama manuel de girilebilir
- KVKK dokümanı yoksa form yine de çalışır
- Ürün bulunamazsa hata döner
- Call request sistemi ile tam entegre çalışır
- **Ürün sorumlusu yoksa normal call request süreci işler**
- **Email template bulunamazsa email gönderilmez ama call request oluşturulur**
- **Email gönderimi RabbitMQ üzerinden asenkron çalışır**
- **Ürün sorumlu kullanıcılarının email adresi yoksa o kullanıcıya email gönderilmez**

## Kurulum Adımları

1. **Database Migration:**
   ```bash
   # PostgreSQL
   psql -U username -d database_name -f PRODUCT_CONTACT_MIGRATION.sql
   
   # H2 (otomatik çalışır veya manuel)
   ```

2. **Email Template Kurulumu:**
   ```bash
   # PostgreSQL
   psql -U username -d database_name -f PRODUCT_CONTACT_EMAIL_TEMPLATE.sql
   
   # H2
   # PRODUCT_CONTACT_EMAIL_TEMPLATE_H2.sql dosyasını çalıştırın
   ```

3. **Backend Restart:**
   ```bash
   # Backend'i yeniden başlatın
   mvn spring-boot:run
   ```

4. **Ürün Sorumlu Kullanıcıları Atama:**
   - Admin panelinde ürün düzenleme sayfasına gidin
   - "Sorumlu Kullanıcılar" bölümünden kullanıcıları seçin
   - Kaydedin

5. **Test:**
   - Ürün detay sayfasından iletişim formunu test edin
   - Email'lerin geldiğini kontrol edin

## Gelecek Geliştirmeler

1. ~~Email bildirimleri (ürün sorumlu kullanıcılarına)~~ ✅ Tamamlandı
2. Ürün bazlı istatistikler
3. Ürün sayfasında "Sık Sorulan Sorular" bölümü
4. Otomatik yanıt şablonları
5. Ürün kategorisi bazlı yönlendirme
6. RabbitMQ entegrasyonu (şu an log olarak çalışıyor)
7. Email template'lerde dil desteği
8. SMS bildirimi opsiyonu

## Sorun Giderme

### Email Gönderilmiyor

1. Email template'in var olduğunu kontrol edin:
   ```sql
   SELECT * FROM email_template WHERE code = 'product_contact_request';
   ```

2. Ürün sorumlu kullanıcılarının email adreslerini kontrol edin:
   ```sql
   SELECT u.email FROM user u 
   JOIN product_responsible_users pru ON u.id = pru.user_id 
   WHERE pru.product_id = [PRODUCT_ID];
   ```

3. RabbitMQ'nun çalıştığını kontrol edin

4. Log'ları kontrol edin:
   ```bash
   tail -f logs/application.log | grep "Product contact email"
   ```

### Call Request Oluşturulmuyor

1. Ürünün var olduğunu kontrol edin
2. Site bilgisinin doğru olduğunu kontrol edin
3. KVKK dokümanının var olduğunu kontrol edin (opsiyonel)
4. Database constraint'lerini kontrol edin

### Ürün Sorumlusu Atanmıyor

1. Ürünün sorumlu kullanıcıları olduğunu kontrol edin
2. Kullanıcıların aktif olduğunu kontrol edin
3. Log'ları kontrol edin

