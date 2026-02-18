# Site Configuration - Üst Banner Implementasyon Özeti

## Yapılan Değişiklikler

### 1. Backend - Domain Layer

#### Model (StoreSiteConfigurationModel.java)
```java
// Top Banner Fields - Localized kullanımı
@Embedded
@AttributeOverrides({
    @AttributeOverride(name = "tr", column = @Column(name = "top_banner_text_tr", length = 500)),
    @AttributeOverride(name = "en", column = @Column(name = "top_banner_text_en", length = 500)),
    @AttributeOverride(name = "de", column = @Column(name = "top_banner_text_de", length = 500)),
    @AttributeOverride(name = "fr", column = @Column(name = "top_banner_text_fr", length = 500)),
    @AttributeOverride(name = "es", column = @Column(name = "top_banner_text_es", length = 500)),
    @AttributeOverride(name = "it", column = @Column(name = "top_banner_text_it", length = 500))
})
private Localized topBannerText;

@Column(name = "top_banner_enabled")
private Boolean topBannerEnabled = false;

@Column(name = "top_banner_bg_color", length = 20)
private String topBannerBgColor;

@Column(name = "top_banner_text_color", length = 20)
private String topBannerTextColor;

@Column(name = "top_banner_link", length = 500)
private String topBannerLink;
```

#### Data (StoreSiteConfigurationData.java)
```java
// Top Banner Fields - LocalizeData kullanımı
private Boolean topBannerEnabled;
private LocalizeData topBannerText;
private String topBannerBgColor;
private String topBannerTextColor;
private String topBannerLink;
```

### 2. Backend - Facade Layer

#### SiteConfigurationFacadeImpl.java
```java
// Save metoduna eklenen mapping
configModel.setTopBannerEnabled(siteConfigurationData.getTopBannerEnabled());
configModel.setTopBannerText(modelMapper.map(siteConfigurationData.getTopBannerText(), 
        com.btc_store.domain.model.custom.localize.Localized.class));
configModel.setTopBannerBgColor(siteConfigurationData.getTopBannerBgColor());
configModel.setTopBannerTextColor(siteConfigurationData.getTopBannerTextColor());
configModel.setTopBannerLink(siteConfigurationData.getTopBannerLink());
```

### 3. Frontend - Components

#### TopBanner.tsx (Yeni Component)
- Site configuration'dan banner bilgilerini çeker
- Locale'e göre doğru dil metnini gösterir
- Banner aktif değilse veya metin yoksa render edilmez
- Link varsa tıklanabilir yapar
- PublicLayout'a eklendi (Header'dan önce)

```tsx
// Locale'e göre metin seçimi
if (siteConfiguration.topBannerText) {
  switch (locale) {
    case 'tr': bannerText = siteConfiguration.topBannerText.tr || ''; break;
    case 'en': bannerText = siteConfiguration.topBannerText.en || ''; break;
    // ... diğer diller
  }
}
```

### 4. Frontend - Admin Panel

#### SiteConfigurationForm.tsx
- Tab yapısı yeniden düzenlendi:
  - Logo Ayarları
  - Başlık Ayarları (Üst Banner + Header İletişim)
  - Footer Ayarları (Footer İletişim + Footer Menüler)

- Üst Banner formu:
  - Aktif/Pasif checkbox
  - 6 dil desteği (Türkçe, İngilizce, Almanca, Fransızca, İspanyolca, İtalyanca)
  - Accordion yapısı (Türkçe açık, diğerleri "Diğer Diller" butonuyla)
  - Renk seçiciler (arka plan ve yazı rengi)
  - Link alanı (opsiyonel)
  - Canlı önizleme

```tsx
// Form data yapısı
topBannerEnabled: false,
topBannerText: { tr: '', en: '', de: '', fr: '', es: '', it: '' },
topBannerBgColor: '#1e40af',
topBannerTextColor: '#ffffff',
topBannerLink: ''
```

### 5. Frontend - Type Definitions

#### useStore.ts - SiteConfiguration Interface
```typescript
export interface SiteConfiguration {
  // ... diğer alanlar
  topBannerEnabled?: boolean;
  topBannerText?: {
    tr?: string;
    en?: string;
    de?: string;
    fr?: string;
    es?: string;
    it?: string;
  };
  topBannerBgColor?: string;
  topBannerTextColor?: string;
  topBannerLink?: string;
}
```

### 6. Database Migration

#### SITE_CONFIGURATION_TOP_BANNER.sql
```sql
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_tr VARCHAR(500);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_en VARCHAR(500);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_de VARCHAR(500);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_fr VARCHAR(500);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_es VARCHAR(500);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_it VARCHAR(500);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_bg_color VARCHAR(20);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_text_color VARCHAR(20);
ALTER TABLE site_configuration ADD COLUMN IF NOT EXISTS top_banner_link VARCHAR(500);
```

## Veri Akışı

### GET İşlemi
1. Frontend: `siteConfigurationService.get()` çağrısı
2. Backend Controller: `/api/v1/site-configuration` endpoint
3. Facade: `getSiteConfiguration()` - ModelMapper ile Data'ya dönüştürür
4. Service: Database'den SiteConfigurationModel çeker
5. Response: LocalizeData olarak frontend'e döner
6. Frontend: `topBannerText: { tr, en, de, fr, es, it }` şeklinde kullanır

### POST İşlemi
1. Frontend: Form'dan `topBannerText` objesi gönderilir
2. Backend Controller: Multipart request alır
3. Facade: `saveSiteConfiguration()` 
   - LocalizeData → Localized mapping yapar
   - Model'e set eder
4. Service: Database'e kaydeder
5. Response: Kaydedilen data frontend'e döner

## Önemli Notlar

### Backend
- `Localized` → `@Embeddable` olarak model'de kullanılır
- `LocalizeData` → Data transfer için kullanılır
- ModelMapper otomatik olarak aralarında mapping yapar
- Facade'de explicit mapping yapıldı: `modelMapper.map(siteConfigurationData.getTopBannerText(), Localized.class)`

### Frontend
- `topBannerText` tek bir obje olarak yönetilir
- Accordion pattern ile kullanıcı deneyimi iyileştirildi
- Locale'e göre otomatik dil seçimi yapılır
- Fallback mekanizması: tr → en → boş string

### Database
- 6 ayrı kolon (her dil için bir kolon)
- Embedded yapı sayesinde tek bir entity olarak yönetilir
- Migration script ile kolay kurulum

## Test Senaryoları

1. ✅ Banner aktif/pasif kontrolü
2. ✅ 6 dil desteği
3. ✅ Renk özelleştirme
4. ✅ Link ekleme (opsiyonel)
5. ✅ Canlı önizleme
6. ✅ Locale'e göre doğru metin gösterimi
7. ✅ Accordion açma/kapama
8. ✅ Form kaydetme ve yükleme

## API Endpoints

### GET /api/v1/public/site-configuration
Public endpoint - Banner bilgilerini içerir

### GET /api/v1/site-configuration
Admin endpoint - Tüm konfigürasyon bilgileri

### POST /api/v1/site-configuration
Admin endpoint - Konfigürasyon güncelleme
- Content-Type: multipart/form-data
- Body: configData (JSON) + logo dosyaları (opsiyonel)

## Kullanım Örneği

### Admin Panelinde Ayarlama
1. Site Ayarları → Başlık Ayarları
2. "Üst Banner'ı Aktif Et" checkbox'ını işaretle
3. Türkçe metni gir
4. "Diğer Diller" butonuna tıkla
5. İngilizce ve diğer dilleri gir
6. Renkleri seç
7. İsteğe bağlı link ekle
8. Önizlemeyi kontrol et
9. Kaydet

### Sonuç
- Banner tüm sayfalarda en üstte görünür
- Kullanıcının dil seçimine göre doğru metin gösterilir
- Tıklanabilir (link varsa)
- Özelleştirilmiş renkler

## Dosya Listesi

### Backend
- `domain/src/main/java/com/btc_store/domain/model/store/StoreSiteConfigurationModel.java`
- `domain/src/main/java/com/btc_store/domain/data/store/StoreSiteConfigurationData.java`
- `facade/src/main/java/com/btc_store/facade/impl/SiteConfigurationFacadeImpl.java`
- `SITE_CONFIGURATION_TOP_BANNER.sql`

### Frontend
- `src/components/TopBanner.tsx` (YENİ)
- `src/layouts/PublicLayout.tsx`
- `src/views/admin/SiteConfigurationForm.tsx`
- `src/store/useStore.ts`

### Dokümantasyon
- `SITE_CONFIGURATION_TOP_BANNER_GUIDE.md`
- `SITE_CONFIGURATION_IMPLEMENTATION_SUMMARY.md` (bu dosya)
