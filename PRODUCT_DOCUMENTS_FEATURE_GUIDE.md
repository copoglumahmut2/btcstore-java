# Ürün Dokümanları Feature Kılavuzu

## Genel Bakış

Bu feature, satış ekibinin anasayfadan gizlice login olup ürün detay sayfalarında o ürüne ait dokümanları görmesini sağlar. Normal ziyaretçiler bu dokümanları göremez.

## Mimari

### Frontend (Next.js)

1. **Sales Login Sayfası** (`/sales-login`)
   - Basit kullanıcı adı/şifre login formu
   - Başarılı login sonrası JWT token localStorage'a kaydedilir
   - Anasayfaya yönlendirilir

2. **Header Component**
   - Login durumuna göre "Satış Girişi" veya "Çıkış" butonu gösterir
   - Authenticated kullanıcı için kullanıcı adı tooltip'te görünür

3. **Product Detail Sayfası**
   - `isAuthenticated` kontrolü yapar
   - Authenticated ise ürün dokümanlarını çeker ve gösterir
   - Normal ziyaretçiler doküman bölümünü göremez

4. **Auth Store**
   - Zustand ile state management
   - Token'ları cookie'de saklar
   - Auto-refresh token mekanizması
   - `initializeAuth()` ile sayfa yüklendiğinde token kontrolü

### Backend (Spring Boot)

1. **ProductController**
   - `GET /v1/products/{code}/documents` endpoint'i
   - `@PreAuthorize("isAuthenticated()")` ile korunur
   - Sadece login olmuş kullanıcılar erişebilir

2. **ProductFacade**
   - `getProductDocuments(String productCode)` metodu
   - Ürüne bağlı dokümanları döner

3. **ProductModel**
   - `documents` field'ı (ManyToMany relation)
   - `product_documents` junction table ile ilişki

## Kurulum

### 1. Database Migration

```sql
-- PRODUCT_DOCUMENTS_SETUP.sql dosyasını çalıştırın
```

### 2. Backend Deployment

Backend kodları zaten eklenmiş durumda:
- ProductController'a yeni endpoint eklendi
- ProductFacade'e method eklendi
- ProductModel'e documents field'ı eklendi

### 3. Frontend Deployment

Frontend kodları zaten eklenmiş durumda:
- `/sales-login` sayfası oluşturuldu
- Header'a login/logout butonları eklendi
- ProductDetail'e doküman bölümü eklendi
- Auth store initialize edildi

## Kullanım

### Satış Ekibi İçin

1. `/sales-login` adresine gidin
2. Kullanıcı adı ve şifrenizi girin
3. Anasayfaya yönlendirileceksiniz
4. Herhangi bir ürün detay sayfasına gidin
5. Sayfanın altında "Ürün Dokümanları" bölümünü göreceksiniz

### Admin İçin - Ürüne Doküman Ekleme

```sql
-- Ürüne doküman eklemek için:
INSERT INTO product_documents (product_id, document_id) 
VALUES (
    (SELECT id FROM product WHERE code = 'URUN_KODU'),
    (SELECT id FROM document WHERE code = 'DOKUMAN_KODU')
);
```

## Güvenlik

- Doküman endpoint'i `@PreAuthorize("isAuthenticated()")` ile korunur
- Token'lar HTTP-only cookie'lerde saklanır
- Auto-refresh token mekanizması ile session yönetimi
- Normal ziyaretçiler doküman bölümünü göremez

## API Endpoints

### Sales Login
```
POST /login
Body: { username: string, password: string }
Response: { accessToken, refreshToken, ... }
```

### Get Product Documents
```
GET /v1/products/{code}/documents
Headers: Authorization: Bearer {token}
Response: [{ id, name, description, fileUrl, fileSize, fileType, uploadDate }]
```

## Özellikler

- ✅ Gizli login sayfası
- ✅ Token-based authentication
- ✅ Auto-refresh token
- ✅ Ürün bazlı doküman görüntüleme
- ✅ Responsive tasarım
- ✅ Güvenli endpoint
- ✅ Normal kullanıcılardan gizli

## Test

1. Bir kullanıcı oluşturun (admin panelinden)
2. `/sales-login` sayfasına gidin
3. Login olun
4. Bir ürüne doküman ekleyin (SQL ile)
5. Ürün detay sayfasına gidin
6. Dokümanları görün
7. Logout olun
8. Dokümanların kaybolduğunu görün
