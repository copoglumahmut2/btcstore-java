# Kullanıcı Profil Resmi Kurulum Adımları

## 1. Veritabanı Kurulumu

`add-user-media-category.sql` dosyasını veritabanınızda çalıştırın:

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

## 2. Backend'i Yeniden Başlatın

Backend uygulamanızı yeniden başlatın (eğer çalışıyorsa).

## 3. Frontend'i Test Edin

1. Admin paneline giriş yapın
2. Kullanıcılar sayfasına gidin
3. "Yeni Kullanıcı" butonuna tıklayın
4. "Profil Resmi" bölümünde resim yükleme alanını göreceksiniz
5. Bir resim yükleyin ve kullanıcıyı kaydedin

## Özellikler

✅ ImageUpload component'i kullanılıyor (diğer formlar gibi)
✅ Resim önizleme
✅ Resim değiştirme
✅ Resim kaldırma
✅ Lightbox ile büyük görüntüleme
✅ Drag & drop desteği
✅ Maksimum 1 resim
✅ 1:1 aspect ratio (kare profil resmi)

## Sorun Giderme

### Resim yüklenmiyor
- Veritabanında `user_cms` kategorisinin eklendiğinden emin olun
- Backend loglarını kontrol edin
- Browser console'da hata var mı kontrol edin

### Resim görünmüyor
- `.env.local` dosyasında `NEXT_PUBLIC_MEDIA_URL` tanımlı mı kontrol edin
- Media sunucusunun çalıştığından emin olun
- Network sekmesinde resim URL'ini kontrol edin
