# Ürün İletişim Özelliği - Özet

## Ne Yapıldı?

Ürün detay sayfasına iletişim özelliği eklendi. Kullanıcılar artık ilgili ürün hakkında doğrudan iletişime geçebilirler.

**ÖNEMLİ:** Ürün iletişimi normal call request'lerden ayrı bir süreç izler:
- ✅ Ürün sorumlu kullanıcılarına özel email gönderimi
- ✅ Otomatik atama ve status yönetimi
- ✅ Özel email template: `product_contact_request`
- ✅ Ürün bilgileri email'de gösterilir

## Değişiklikler

### Backend

1. **Database**: `call_request` tablosuna `product_id` kolonu eklendi
2. **Model**: `StoreCallRequestModel` ve `StoreCallRequestData`'ya `product` field'ı eklendi
3. **Facade**: `CallRequestFacade`'e `createProductContactRequest()` metodu eklendi
4. **Email**: Ürün sorumlu kullanıcılarına özel email gönderimi eklendi
5. **Template**: `product_contact_request` email template'i oluşturuldu
6. **Atama**: Ürün sorumlu kullanıcılarına otomatik atama
7. **Controller**: `PublicController`'a `POST /v1/public/products/{code}/contact` endpoint'i eklendi

### Frontend

1. **Service**: `public.service.ts`'e `createProductContactRequest()` ve `getCurrentPrivacyPolicy()` metodları eklendi
2. **Component**: `ProductContact.tsx` backend API'ye bağlandı (mock data kaldırıldı)
3. **UI**: `ProductDetail.tsx`'e sağ tarafta iletişim bölümü eklendi

## Yeni Endpoint

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
  "acceptedLegalDocument": { "code": "privacy-policy-code" }
}
```

## Kurulum

1. Database migration'ı çalıştırın:
   ```sql
   -- PostgreSQL için
   \i PRODUCT_CONTACT_MIGRATION.sql
   
   -- H2 için
   \i PRODUCT_CONTACT_MIGRATION_H2.sql
   ```

2. Email template'i yükleyin:
   ```sql
   -- PostgreSQL için
   \i PRODUCT_CONTACT_EMAIL_TEMPLATE.sql
   
   -- H2 için
   \i PRODUCT_CONTACT_EMAIL_TEMPLATE_H2.sql
   ```

3. Backend'i yeniden başlatın

4. Ürünlere sorumlu kullanıcılar atayın (Admin panelinden)

5. Frontend zaten hazır, değişiklik gerekmez

## Kullanım

1. Ürün detay sayfasına gidin
2. "İletişime Geç" butonuna tıklayın
3. Formu doldurun
4. Gönder

## Özellikler

- ✅ Ürün otomatik olarak call request'e bağlanır
- ✅ Subject otomatik oluşturulur: "Ürün Hakkında İletişim: [Ürün Adı]"
- ✅ KVKK dokümanı otomatik yüklenir
- ✅ Responsive tasarım
- ✅ Hata yönetimi
- ✅ Loading states
- ✅ **Ürün sorumlu kullanıcılarına otomatik atama**
- ✅ **Özel email template ile bildirim**
- ✅ **Ürün bilgileri email'de gösterilir**
- ✅ **Ayrı süreç (normal call request'lerden bağımsız)**

## Email Template Değişkenleri

- `{{productName}}` - Ürün adı
- `{{productCode}}` - Ürün kodu
- `{{productDescription}}` - Ürün açıklaması
- `{{customerName}}` - Müşteri adı
- `{{customerEmail}}` - Müşteri email
- `{{customerPhone}}` - Müşteri telefon
- `{{message}}` - Müşteri mesajı
- `{{createdDate}}` - Talep tarihi

## Dosyalar

- `PRODUCT_CONTACT_MIGRATION.sql` - PostgreSQL migration
- `PRODUCT_CONTACT_MIGRATION_H2.sql` - H2 migration
- `PRODUCT_CONTACT_EMAIL_TEMPLATE.sql` - PostgreSQL email template
- `PRODUCT_CONTACT_EMAIL_TEMPLATE_H2.sql` - H2 email template
- `PRODUCT_CONTACT_IMPLEMENTATION_GUIDE.md` - Detaylı kılavuz
- Backend: `StoreCallRequestModel.java`, `CallRequestFacadeImpl.java`, `PublicController.java`
- Frontend: `ProductContact.tsx`, `ProductDetail.tsx`, `public.service.ts`

## İş Akışı

1. Müşteri ürün detay sayfasından iletişim formunu doldurur
2. Form backend'e gönderilir
3. Call request oluşturulur ve ürüne bağlanır
4. **Ürün sorumlu kullanıcıları varsa:**
   - Call request bu kullanıcılara atanır
   - Status: ASSIGNED
   - Özel email template ile bildirim gönderilir
5. **Ürün sorumlusu yoksa:**
   - Status: PENDING
   - Normal call request süreci işler
6. Müşteri başarı mesajı görür
7. Ürün sorumluları email alır (varsa)
