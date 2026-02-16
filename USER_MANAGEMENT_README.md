# Kullanıcı Yönetimi Modülü

Bu doküman, BTC Store uygulamasına eklenen Kullanıcı Yönetimi modülünün kullanımını açıklar.

## Özellikler

- ✅ Kullanıcı listeleme (sayfalama ile)
- ✅ Kullanıcı ekleme
- ✅ Kullanıcı düzenleme
- ✅ Kullanıcı silme (soft delete)
- ✅ Şifre yönetimi (şifreli saklama)
- ✅ Kullanıcı grupları atama
- ✅ Aktif/Pasif durum yönetimi
- ✅ SearchService entegrasyonu
- ✅ ModelService entegrasyonu

## Teknik Mimari

### Backend Katmanları

#### 1. Domain Katmanı
- **Model**: `UserModel` - JPA entity sınıfı
- **Data**: `UserData` - DTO sınıfı
- **Base Model**: `StoreUserModel` - Temel kullanıcı modeli

#### 2. Persistence Katmanı
- **DAO**: `UserDao` - JPA Repository interface (mevcut)
  - `getByUsernameIgnoreCaseAndSite()` - Kullanıcı adı ile kullanıcı bulma
  - `getByCodeIgnoreCaseAndSite()` - Kod ile kullanıcı bulma
  - `getBySiteOrderByLastModifiedDateDesc()` - Site'a göre kullanıcıları listeleme
  - `getByActiveAndDeletedAndSite()` - Aktif kullanıcıları getirme
  - `existsByUsernameAndSite()` - Kullanıcı adı kontrolü

#### 3. Service Katmanı
- **Interface**: `UserService` (mevcut)
- **Implementation**: `UserServiceImpl` (mevcut)
  - `getUserModelForStore()` - Store için kullanıcı getir
  - `getUserModelForStoreByCode()` - Kod ile kullanıcı getir
  - `existUser()` - Kullanıcı varlık kontrolü
  - `getCurrentUser()` - Mevcut kullanıcıyı getir

#### 4. Facade Katmanı (YENİ)
- **Interface**: `UserFacade`
- **Implementation**: `UserFacadeImpl`
  - `getAllUsers()` - Tüm kullanıcıları listele
  - `getUserByCode()` - Kod ile kullanıcı getir
  - `saveUser()` - Kullanıcı kaydet/güncelle
  - `deleteUser()` - Kullanıcı sil (soft delete)

#### 5. Controller Katmanı (YENİ)
- **Controller**: `UserController`
  - `GET /v1/users` - Tüm kullanıcıları listele
  - `GET /v1/users/{code}` - Kod ile kullanıcı getir
  - `POST /v1/users` - Kullanıcı kaydet/güncelle
  - `DELETE /v1/users/{code}` - Kullanıcı sil

### Frontend Katmanları

#### 1. Service Katmanı
- **Service**: `userService` (admin.service.ts)
  - `getAll()` - Tüm kullanıcıları getir
  - `getByCode()` - Kod ile kullanıcı getir
  - `save()` - Kullanıcı kaydet
  - `delete()` - Kullanıcı sil

#### 2. View Katmanı
- **UsersAdmin**: Kullanıcı listesi sayfası
  - Sayfalama desteği
  - Arama ve filtreleme
  - Düzenleme ve silme işlemleri
  
- **UserFormNew**: Kullanıcı ekleme/düzenleme formu
  - Kullanıcı bilgileri
  - Şifre yönetimi
  - Kullanıcı grupları seçimi
  - Aktif/Pasif durum

#### 3. Route Katmanı
- `/admin/users` - Kullanıcı listesi
- `/admin/users/new` - Yeni kullanıcı ekleme
- `/admin/users/[code]` - Kullanıcı düzenleme

## Veritabanı Yapısı

Kullanıcı tablosu zaten mevcut:

```sql
CREATE TABLE user_model (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone_number VARCHAR(50),
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    deleted BOOLEAN DEFAULT FALSE,
    password_blocked BOOLEAN DEFAULT FALSE,
    password_change_required BOOLEAN DEFAULT FALSE,
    password_blocked_attempt INT DEFAULT 0,
    unsuccess_login_attempt INT DEFAULT 0,
    site_id BIGINT NOT NULL,
    created_date TIMESTAMP,
    modified_date TIMESTAMP,
    CONSTRAINT uk_user_username_site UNIQUE (username, site_id)
);
```

## Kurulum

### 1. Backend Derleme
```bash
cd btcstore
mvn clean install
```

### 2. Frontend Kurulumu
```bash
cd btc-store
npm install
npm run dev
```

## Kullanım

### Backend API Örnekleri

#### Tüm Kullanıcıları Listeleme
```bash
GET /v1/users
Authorization: Bearer {token}
```

#### Kullanıcı Getirme
```bash
GET /v1/users/{code}
Authorization: Bearer {token}
```

#### Kullanıcı Kaydetme
```bash
POST /v1/users
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "johndoe",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phoneNumber": "+90 555 123 45 67",
  "definedPassword": "SecurePass123!",
  "active": true,
  "userGroups": [
    { "code": "admin-group-code" }
  ]
}
```

#### Kullanıcı Güncelleme
```bash
POST /v1/users
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": 1,
  "code": "existing-user-code",
  "username": "johndoe",
  "firstName": "John",
  "lastName": "Doe Updated",
  "email": "john.updated@example.com",
  "active": true,
  "definedPassword": "NewPassword123!", // Opsiyonel - sadece şifre değiştirilecekse
  "userGroups": [
    { "code": "admin-group-code" }
  ]
}
```

#### Kullanıcı Silme (Soft Delete)
```bash
DELETE /v1/users/{code}
Authorization: Bearer {token}
```

### Frontend Kullanımı

1. Admin paneline giriş yapın
2. Sol menüden "Sistem" > "Kullanıcılar" seçeneğine tıklayın
3. Yeni kullanıcı eklemek için "Yeni Kullanıcı" butonuna tıklayın
4. Formu doldurun:
   - Kullanıcı Adı: Benzersiz kullanıcı adı (değiştirilemez)
   - Ad/Soyad: Kullanıcının adı ve soyadı
   - E-posta: İletişim e-postası
   - Telefon: İletişim telefonu
   - Şifre: Güvenli şifre (en az 8 karakter önerilir)
   - Kullanıcı Grupları: Kullanıcının dahil olacağı gruplar
   - Aktif: Kullanıcının aktif olup olmadığı
5. "Kaydet" butonuna tıklayın

## Güvenlik

### Şifre Yönetimi
- Şifreler BCrypt algoritması ile şifrelenir
- Şifreler veritabanında asla düz metin olarak saklanmaz
- Şifre değiştirme işlemi sadece yeni şifre girildiğinde yapılır

### Yetkilendirme
- Tüm endpoint'ler JWT token ile korunmaktadır
- Rol bazlı yetkilendirme mevcuttur:
  - READ: Kullanıcı okuma yetkisi
  - SAVE: Kullanıcı kaydetme yetkisi
  - DELETE: Kullanıcı silme yetkisi

### Soft Delete
- Kullanıcılar fiziksel olarak silinmez
- `deleted` flag'i `true` yapılır ve `active` flag'i `false` yapılır
- Bu sayede veri bütünlüğü korunur

## Kullanıcı Grupları

Kullanıcılar bir veya birden fazla kullanıcı grubuna atanabilir:
- Gruplar menü erişim kontrolü için kullanılır
- Gruplar yetki yönetimi için kullanılır
- Bir kullanıcı birden fazla gruba dahil olabilir

## Özelleştirme

### Şifre Politikası
Şifre politikası backend'de `PasswordEncoder` bean'i ile yapılandırılabilir:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // Strength: 12
}
```

### Kullanıcı Validasyonu
Kullanıcı kaydetme sırasında ek validasyonlar eklenebilir:
- E-posta formatı kontrolü
- Telefon numarası formatı kontrolü
- Şifre güçlülük kontrolü
- Kullanıcı adı format kontrolü

## Sorun Giderme

### Kullanıcı Kaydedilmiyor
- Kullanıcı adının benzersiz olduğundan emin olun
- Şifrenin dolu olduğunu kontrol edin (yeni kullanıcı için)
- Backend loglarını kontrol edin

### Kullanıcı Listesi Yüklenmiyor
- SearchService'in doğru yapılandırıldığından emin olun
- Veritabanı bağlantısını kontrol edin
- Browser console'da hata mesajlarını kontrol edin

### Şifre Değişmiyor
- Yeni şifre ve şifre tekrar alanlarının eşleştiğinden emin olun
- Şifrenin minimum gereksinimleri karşıladığından emin olun

## API Response Formatı

### Başarılı Response
```json
{
  "status": "SUCCESS",
  "data": {
    "id": 1,
    "code": "user-code-uuid",
    "username": "johndoe",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phoneNumber": "+90 555 123 45 67",
    "active": true,
    "deleted": false,
    "userGroups": [
      {
        "code": "admin-group-code",
        "name": {
          "tr": "Yönetici Grubu",
          "en": "Admin Group"
        }
      }
    ]
  }
}
```

### Hata Response
```json
{
  "status": "ERROR",
  "errorMessage": "Bu kullanıcı adı zaten kullanılıyor"
}
```

## Geliştirme Notları

- Kullanıcı modeli `CodeBasedItemModel`'den türetilmiştir
- SearchService entegrasyonu sayfalama desteği sağlar
- ModelService CRUD operasyonlarını yönetir
- Frontend TypeScript ile tip güvenliği sağlar
- Responsive tasarım mobil uyumludur
- Soft delete ile veri bütünlüğü korunur

## İletişim

Sorularınız için lütfen geliştirme ekibi ile iletişime geçin.
