# Menü Tipi (Menu Type) Özelliği

## Genel Bakış
Menüler artık iki ayrı sayfada yönetiliyor: Admin Panel Menüleri ve Public Menüler. Sistem otomatik olarak hangi sayfada olduğunuza göre `menuType` değerini belirler.

## Menü Tipleri

### 1. ADMIN_PANEL
- Yönetim panelinde görünen menüler
- Admin kullanıcıları için
- Örnek: Dashboard, Kullanıcılar, Ayarlar, İçerik Yönetimi
- Sayfa: `/admin/menus/admin`

### 2. PUBLIC
- Kullanıcı tarafında (frontend) görünen menüler
- Tüm ziyaretçiler için
- Örnek: Ana Sayfa, Ürünler, Hakkımızda, İletişim
- Sayfa: `/admin/menus/public`

## Veritabanı Değişiklikleri

### Yeni Kolon
```sql
ALTER TABLE menu_link_item 
ADD COLUMN menu_type VARCHAR(20) NOT NULL DEFAULT 'PUBLIC';
```

### Migration Script
`add-menu-type-column.sql` dosyasını çalıştırın:
```bash
mysql -u [username] -p [database_name] < add-menu-type-column.sql
```

## Backend Değişiklikleri

### 1. Enum Eklendi
- `domain/src/main/java/com/btc_store/domain/enums/MenuType.java`
- İki değer: `ADMIN_PANEL`, `PUBLIC`

### 2. Model Güncellendi
- `StoreMenuLinkItemModel.java` - `menuType` field eklendi
- `StoreMenuLinkItemData.java` - `menuType` field eklendi

## Frontend Değişiklikleri

### 1. İki Ayrı Sayfa
- **AdminMenus.tsx** (`/admin/menus/admin`) - Admin panel menülerini listeler
- **PublicMenus.tsx** (`/admin/menus/public`) - Public menüleri listeler

### 2. MenuForm.tsx
- `menuType` prop olarak alır (kullanıcı seçmez)
- Hangi sayfadan geldiğine göre otomatik belirlenir
- Form başlığı ve geri dönüş yolu dinamik

### 3. Otomatik menuType Belirleme
- `/admin/menus/admin/new` → `menuType: 'ADMIN_PANEL'`
- `/admin/menus/public/new` → `menuType: 'PUBLIC'`

## Kullanım

### Admin Panel Menüleri
1. Admin panelde "Admin Panel Menüleri" sayfasına gidin (`/admin/menus/admin`)
2. "Yeni Admin Menü" butonuna tıklayın
3. Menü bilgilerini doldurun
4. Sistem otomatik olarak `menuType: 'ADMIN_PANEL'` gönderir

### Public Menüler
1. Admin panelde "Public Menüler" sayfasına gidin (`/admin/menus/public`)
2. "Yeni Public Menü" butonuna tıklayın
3. Menü bilgilerini doldurun
4. Sistem otomatik olarak `menuType: 'PUBLIC'` gönderir

### Sayfalar Arası Geçiş
Her iki sayfada da diğer sayfaya geçiş butonu var:
- Admin Menüler sayfasında: "🌐 Public Menüler" butonu
- Public Menüler sayfasında: "⚙️ Admin Menüler" butonu

### API Kullanımı
Menü kaydederken `menuType` otomatik gönderilir:
```json
{
  "code": "products",
  "name": {
    "tr": "Ürünler",
    "en": "Products"
  },
  "menuType": "PUBLIC",
  "isRoot": true,
  "active": true
}
```

### Menüleri Filtreleme
Frontend'de her sayfa kendi menülerini filtreler:
```typescript
// Admin menüler
const adminMenus = response.data.filter(menu => menu.menuType === 'ADMIN_PANEL');

// Public menüler
const publicMenus = response.data.filter(menu => menu.menuType === 'PUBLIC' || !menu.menuType);
```

## Notlar
- Kullanıcı `menuType` seçemez, sistem otomatik belirler
- Her sayfa sadece kendi tipindeki menüleri gösterir
- Üst menü seçerken de sadece aynı tipteki menüler listelenir
- Mevcut menüler için default değer `PUBLIC` olarak ayarlanmıştır
- Migration script'te admin menüleri otomatik olarak `ADMIN_PANEL` olarak işaretlenir
