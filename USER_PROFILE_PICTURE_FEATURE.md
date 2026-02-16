# Kullanıcı Profil Resmi Özelliği

Bu doküman, kullanıcıların profil resmi yükleyebilmesi için eklenen özellikleri açıklar.

## Yapılan Değişiklikler

### Backend (Java)

#### 1. Domain Katmanı

**MediaCategory Enum** (`domain/src/main/java/com/btc_store/domain/enums/MediaCategory.java`)
- `USER("user_cms")` kategorisi eklendi

**StoreUserData** (`domain/src/main/java/com/btc_store/domain/data/store/user/StoreUserData.java`)
- `private MediaData picture;` alanı eklendi

**Not:** `StoreUserModel` zaten `private MediaModel picture;` alanına sahipti.

#### 2. Facade Katmanı

**UserFacade Interface** (`facade/src/main/java/com/btc_store/facade/UserFacade.java`)
```java
UserData saveUser(UserData userData, MultipartFile pictureFile, boolean removePicture);
```
- `saveUser` metodu güncellendi: `pictureFile` ve `removePicture` parametreleri eklendi

**UserFacadeImpl** (`facade/src/main/java/com/btc_store/facade/impl/UserFacadeImpl.java`)
- `MediaService` ve `CmsCategoryService` dependency'leri eklendi
- `saveUser` metodunda profil resmi yükleme mantığı eklendi
- Banner facade'daki media yükleme mantığı örnek alındı:
  - Yeni resim yüklenirse: eski resim silinir, yeni resim kaydedilir
  - Resim kaldırılmak istenirse: mevcut resim silinir
  - Değişiklik yoksa: mevcut resim korunur

#### 3. Controller Katmanı

**UserController** (`webapp/src/main/java/com/btc_store/controller/v1/UserController.java`)
```java
@PostMapping
public ServiceResponseData saveUser(
    @RequestPart(value = "userData") UserData userData,
    @RequestPart(value = "pictureFile", required = false) MultipartFile pictureFile,
    @RequestParam(value = "removePicture", required = false, defaultValue = "false") String removePicture,
    @RequestParam(required = false) String isoCode)
```
- `@RequestBody` yerine `@RequestPart` kullanıldı (multipart/form-data için)
- `pictureFile` ve `removePicture` parametreleri eklendi

### Frontend (React/Next.js)

#### 1. Service Katmanı

**admin.service.ts** (`btc-store/src/services/admin.service.ts`)
```typescript
save: (data: any, pictureFile?: File, removePicture?: boolean) => {
  const formData = new FormData();
  const jsonBlob = new Blob([JSON.stringify(data)], { type: 'application/json' });
  formData.append('userData', jsonBlob);
  if (pictureFile) {
    formData.append('pictureFile', pictureFile);
  } else if (removePicture) {
    formData.append('removePicture', 'true');
  }
  return apiClient.upload('/v1/users', formData);
}
```

#### 2. Component Katmanı

**UserForm.tsx** (`btc-store/src/views/admin/UserForm.tsx`)
- Profil resmi yükleme bölümü eklendi
- Resim önizleme özelliği eklendi
- Resim değiştirme ve kaldırma butonları eklendi
- Backend'den gelen mevcut profil resmi gösteriliyor

**UsersAdmin.tsx** (`btc-store/src/views/admin/UsersAdmin.tsx`)
- Kullanıcı listesinde profil resmi gösterimi eklendi
- Resim yoksa baş harf gösteriliyor

#### 3. Environment Variables

**.env.local** ve **.env.example**
```
NEXT_PUBLIC_MEDIA_URL=http://localhost:9090/webapp/api
```

## Veritabanı Kurulumu

`add-user-media-category.sql` dosyasını çalıştırarak `user_cms` media kategorisini ekleyin:

```sql
-- User profil resmi için media kategorisi ekleme
INSERT INTO cms_category (id, code, description, site_id, created_date, modified_date)
SELECT 
    COALESCE(MAX(id), 0) + 1,
    'user_cms',
    'User Profile Pictures',
    (SELECT id FROM site WHERE code = 'localhost' LIMIT 1),
    NOW(),
    NOW()
FROM cms_category
WHERE NOT EXISTS (
    SELECT 1 FROM cms_category 
    WHERE code = 'user_cms' 
    AND site_id = (SELECT id FROM site WHERE code = 'localhost' LIMIT 1)
);
```

## Kullanım

### Yeni Kullanıcı Oluşturma
1. Admin panelinde "Kullanıcılar" > "Yeni Kullanıcı" sayfasına gidin
2. "Profil Resmi" bölümünde "Resim Yükle" butonuna tıklayın
3. Bir resim seçin (önerilen: 400x400px, max 2MB)
4. Diğer kullanıcı bilgilerini doldurun
5. "Kaydet" butonuna tıklayın

### Mevcut Kullanıcıyı Düzenleme
1. Kullanıcı listesinden düzenlemek istediğiniz kullanıcıya tıklayın
2. Mevcut profil resmi varsa gösterilir
3. "Değiştir" butonu ile yeni resim yükleyebilirsiniz
4. "Kaldır" butonu ile mevcut resmi silebilirsiniz
5. "Kaydet" butonuna tıklayın

## Teknik Detaylar

### Media Yükleme Akışı
1. Frontend'de resim seçilir ve FormData'ya eklenir
2. Backend'de MultipartFile olarak alınır
3. MediaService.storage() ile media sunucusuna yüklenir
4. MediaModel oluşturulur ve UserModel'e bağlanır
5. Eski resim varsa flagMediaForDelete() ile silinmek üzere işaretlenir

### Güvenlik
- Sadece yetkili kullanıcılar (UserModel SAVE yetkisi) profil resmi yükleyebilir
- Dosya tipi kontrolü yapılır (image/*)
- Maksimum dosya boyutu sınırı vardır

### Performans
- Resimler media sunucusunda saklanır
- Veritabanında sadece referans tutulur
- Lazy loading ile gereksiz yükleme önlenir

## Test Edilmesi Gerekenler

- [ ] Yeni kullanıcı oluştururken profil resmi yükleme
- [ ] Mevcut kullanıcının profil resmini değiştirme
- [ ] Profil resmini kaldırma
- [ ] Profil resmi olmadan kullanıcı kaydetme
- [ ] Kullanıcı listesinde profil resimlerinin görünümü
- [ ] Büyük dosya yükleme (hata kontrolü)
- [ ] Geçersiz dosya tipi yükleme (hata kontrolü)
