# Site Configuration - Üst Banner ve Yeniden Yapılandırma Kılavuzu

## Genel Bakış

Site yapılandırma sistemi yeniden düzenlendi. Artık daha organize bir yapıda:
- **Logo Ayarları**: Header ve Footer logoları
- **Başlık Ayarları**: Üst Banner + Header İletişim
- **Footer Ayarları**: Footer İletişim + Footer Menüler

## Yeni Özellikler

### 1. Üst Banner Sistemi

Tüm sayfalarda en üstte görünen, özelleştirilebilir bir banner sistemi eklendi.

#### Özellikler:
- ✅ Aktif/Pasif yapılabilir
- ✅ Çok dilli destek (Türkçe/İngilizce)
- ✅ Özelleştirilebilir arka plan rengi
- ✅ Özelleştirilebilir yazı rengi
- ✅ Opsiyonel link (tıklanabilir banner)
- ✅ Canlı önizleme

#### Veritabanı Alanları:
```sql
top_banner_enabled       BOOLEAN      -- Banner aktif mi?
top_banner_text_tr       VARCHAR(500) -- Türkçe metin
top_banner_text_en       VARCHAR(500) -- İngilizce metin
top_banner_bg_color      VARCHAR(20)  -- Arka plan rengi (hex)
top_banner_text_color    VARCHAR(20)  -- Yazı rengi (hex)
top_banner_link          VARCHAR(500) -- Opsiyonel link
```

### 2. Yeniden Yapılandırılmış Tab Sistemi

#### Eski Yapı:
- Logo Ayarları
- İletişim Bilgileri
- Footer Menüler

#### Yeni Yapı:
- **Logo Ayarları**: Header ve Footer logoları
- **Başlık Ayarları**: 
  - Üst Banner (aktif/pasif, metinler, renkler, link)
  - Header İletişim (telefon, göster/gizle)
- **Footer Ayarları**:
  - Footer İletişim (email, telefon, adres)
  - Footer Menüler (public menü seçimi)

## Kurulum

### 1. Veritabanı Migration

```bash
# PostgreSQL için
psql -U username -d database_name -f SITE_CONFIGURATION_TOP_BANNER.sql
```

### 2. Backend Değişiklikleri

Aşağıdaki dosyalar güncellendi:
- `domain/src/main/java/com/btc_store/domain/model/store/StoreSiteConfigurationModel.java`
- `domain/src/main/java/com/btc_store/domain/data/store/StoreSiteConfigurationData.java`

Yeni alanlar otomatik olarak API'ye dahil edilir.

### 3. Frontend Değişiklikleri

Yeni dosyalar:
- `src/components/TopBanner.tsx` - Üst banner component'i
- `src/store/useStore.ts` - SiteConfiguration type tanımı eklendi

Güncellenen dosyalar:
- `src/views/admin/SiteConfigurationForm.tsx` - Yeni tab yapısı ve üst banner formu
- `src/layouts/PublicLayout.tsx` - TopBanner component'i eklendi

## Kullanım

### Admin Panelinde Ayarlama

1. Admin paneline giriş yapın
2. "Site Ayarları" menüsüne gidin
3. "Başlık Ayarları" tabına geçin
4. "Üst Banner'ı Aktif Et" checkbox'ını işaretleyin
5. Banner metinlerini girin (Türkçe ve İngilizce)
6. Renkleri seçin (color picker veya hex kod)
7. İsteğe bağlı olarak bir link ekleyin
8. Önizlemeyi kontrol edin
9. "Değişiklikleri Kaydet" butonuna tıklayın

### Örnek Kullanım Senaryoları

#### 1. Kampanya Duyurusu
```
Aktif: ✓
TR: 🎉 Yeni ürünlerimizi keşfedin! %20 indirim fırsatını kaçırmayın.
EN: 🎉 Discover our new products! Don't miss 20% discount opportunity.
Arka Plan: #dc2626 (kırmızı)
Yazı: #ffffff (beyaz)
Link: /products
```

#### 2. Önemli Duyuru
```
Aktif: ✓
TR: ⚠️ Bakım çalışması: 15 Mart Cumartesi 02:00-06:00 arası hizmet verilemeyecektir.
EN: ⚠️ Maintenance: Service will be unavailable on March 15, Saturday 02:00-06:00.
Arka Plan: #f59e0b (turuncu)
Yazı: #000000 (siyah)
Link: (boş)
```

#### 3. Ücretsiz Kargo
```
Aktif: ✓
TR: 🚚 500 TL ve üzeri alışverişlerde ücretsiz kargo!
EN: 🚚 Free shipping on orders over 500 TL!
Arka Plan: #10b981 (yeşil)
Yazı: #ffffff (beyaz)
Link: /products
```

## API Endpoints

### GET /api/v1/public/site-configuration
Site yapılandırmasını getirir (üst banner dahil).

**Response:**
```json
{
  "status": "SUCCESS",
  "data": {
    "id": 1,
    "headerLogo": { "absolutePath": "/uploads/logo.png" },
    "footerLogo": { "absolutePath": "/uploads/footer-logo.png" },
    "contactPhone": "+90 (555) 123 45 67",
    "showContactPhone": true,
    "footerEmail": "info@company.com",
    "footerPhone": "+90 (555) 123 45 67",
    "footerAddress": "Adres bilgisi...",
    "topBannerEnabled": true,
    "topBannerTextTr": "Kampanya duyurusu",
    "topBannerTextEn": "Campaign announcement",
    "topBannerBgColor": "#1e40af",
    "topBannerTextColor": "#ffffff",
    "topBannerLink": "/products",
    "footerMenus": [...]
  }
}
```

### POST /api/v1/site-configuration
Site yapılandırmasını günceller.

**Request (multipart/form-data):**
- `configData`: JSON (tüm alanlar dahil)
- `headerLogo`: File (opsiyonel)
- `footerLogo`: File (opsiyonel)
- `removeHeaderLogo`: Boolean (opsiyonel)
- `removeFooterLogo`: Boolean (opsiyonel)

## Frontend Component Kullanımı

### TopBanner Component

```tsx
import TopBanner from '@/components/TopBanner';

// Layout'ta kullanım
<div className="min-h-screen flex flex-col">
  <TopBanner />  {/* En üstte */}
  <Header />
  <main>{children}</main>
  <Footer />
</div>
```

Component özellikleri:
- Otomatik olarak site configuration'ı çeker
- Banner aktif değilse veya metin yoksa render edilmez
- Locale'e göre doğru metni gösterir
- Link varsa tıklanabilir yapar
- Hover efekti ekler

## Teknik Detaylar

### Model Yapısı

```java
@Entity
public class StoreSiteConfigurationModel extends SiteBasedItemModel {
    // Mevcut alanlar...
    
    // Yeni üst banner alanları
    @Column(name = "top_banner_enabled")
    private Boolean topBannerEnabled = false;
    
    @Column(name = "top_banner_text_tr", length = 500)
    private String topBannerTextTr;
    
    @Column(name = "top_banner_text_en", length = 500)
    private String topBannerTextEn;
    
    @Column(name = "top_banner_bg_color", length = 20)
    private String topBannerBgColor;
    
    @Column(name = "top_banner_text_color", length = 20)
    private String topBannerTextColor;
    
    @Column(name = "top_banner_link", length = 500)
    private String topBannerLink;
}
```

### TypeScript Type

```typescript
export interface SiteConfiguration {
  id?: number;
  headerLogo?: { absolutePath: string };
  footerLogo?: { absolutePath: string };
  contactPhone?: string;
  showContactPhone?: boolean;
  footerEmail?: string;
  footerPhone?: string;
  footerAddress?: string;
  footerMenus?: MenuItem[];
  topBannerEnabled?: boolean;
  topBannerTextTr?: string;
  topBannerTextEn?: string;
  topBannerBgColor?: string;
  topBannerTextColor?: string;
  topBannerLink?: string;
}
```

## Test Senaryoları

### 1. Banner Aktif/Pasif Testi
- [ ] Banner'ı aktif et, sayfada görünmeli
- [ ] Banner'ı pasif et, sayfada görünmemeli

### 2. Çok Dilli Test
- [ ] Türkçe dil seçili iken TR metni görünmeli
- [ ] İngilizce dil seçili iken EN metni görünmeli

### 3. Renk Testi
- [ ] Arka plan rengi değiştirildiğinde banner rengi değişmeli
- [ ] Yazı rengi değiştirildiğinde metin rengi değişmeli
- [ ] Color picker ile renk seçimi çalışmalı
- [ ] Hex kod ile manuel renk girişi çalışmalı

### 4. Link Testi
- [ ] Link girildiğinde banner tıklanabilir olmalı
- [ ] Link boş bırakıldığında banner tıklanabilir olmamalı
- [ ] Link'e tıklandığında doğru sayfaya yönlendirmeli

### 5. Önizleme Testi
- [ ] Admin panelinde canlı önizleme çalışmalı
- [ ] Metin değiştiğinde önizleme güncellenmeli
- [ ] Renk değiştiğinde önizleme güncellenmeli

## Sorun Giderme

### Banner Görünmüyor
1. Banner aktif mi kontrol edin
2. İlgili dilde metin girilmiş mi kontrol edin
3. Browser console'da hata var mı kontrol edin
4. Site configuration API'si çalışıyor mu kontrol edin

### Renkler Yanlış Görünüyor
1. Hex kod formatı doğru mu kontrol edin (#RRGGBB)
2. Color picker değerleri kaydedilmiş mi kontrol edin
3. Browser cache'i temizleyin

### Link Çalışmıyor
1. Link formatı doğru mu kontrol edin (/ ile başlamalı veya tam URL)
2. Link alanı kaydedilmiş mi kontrol edin

## Gelecek Geliştirmeler

- [ ] Banner için başlangıç/bitiş tarihi
- [ ] Birden fazla banner desteği (carousel)
- [ ] Banner için görsel ekleme
- [ ] Banner için animasyon seçenekleri
- [ ] Banner için hedef kitle seçimi (tüm kullanıcılar/misafirler/üyeler)
- [ ] Banner için A/B test desteği
- [ ] Banner için tıklama istatistikleri

## Notlar

- Banner metni maksimum 500 karakter olabilir
- Renk kodları hex formatında olmalıdır (#RRGGBB)
- Link opsiyoneldir, boş bırakılabilir
- Banner tüm sayfalarda görünür (PublicLayout kullanan sayfalar)
- Banner responsive tasarıma sahiptir
