# Menü Yönetim Sistemi

Bu doküman, sistem altına eklenen menü yönetim modülünün kullanımını açıklar.

## Özellikler

- ✅ Menü adı (6 dilde localize: TR, EN, DE, FR, ES, IT)
- ✅ User group ile ilişkilendirme
- ✅ İkon desteği (emoji veya icon class)
- ✅ Sıralama (displayOrder)
- ✅ Root menü flag'i (ana menü / alt menü)
- ✅ Üst menü seçimi (parent menu)
- ✅ URL tanımlama
- ✅ CRUD operasyonları (Create, Read, Update, Delete)

## Backend Yapısı

### 1. Domain Layer (Entity)
- `StoreMenuModel.java` - Base entity
- `MenuModel.java` - Custom entity
- `StoreMenuData.java` - Base DTO
- `MenuData.java` - Custom DTO

### 2. Persistence Layer (DAO)
- `MenuDao.java` - Repository interface
  - `findByCodeAndSite()` - Kod ve site'a göre menü bulma
  - `findBySiteOrderByDisplayOrderAsc()` - Tüm menüleri sıralı getirme
  - `findByIsRootTrueAndSiteOrderByDisplayOrderAsc()` - Ana menüleri getirme
  - `findAllWithUserGroupsBySite()` - User group'larla birlikte getirme

### 3. Service Layer
- `MenuService.java` - Service interface
- `MenuServiceImpl.java` - Service implementation
  - `getAllMenus()` - Tüm menüleri getir
  - `getRootMenus()` - Ana menüleri getir
  - `getMenuByCode()` - Kod ile menü getir
  - `saveMenu()` - Menü kaydet/güncelle
  - `deleteMenu()` - Menü sil

### 4. Facade Layer
- `MenuFacade.java` - Facade interface
- `MenuFacadeImpl.java` - Facade implementation
  - DTO-Model dönüşümleri
  - User group ilişkilendirmeleri
  - Parent menu yönetimi

### 5. Controller Layer
- `MenuController.java` - REST API endpoints
  - `GET /v1/menus` - Tüm menüleri listele
  - `GET /v1/menus/root` - Ana menüleri listele
  - `GET /v1/menus/{code}` - Tek menü getir
  - `POST /v1/menus` - Menü oluştur/güncelle
  - `DELETE /v1/menus/{code}` - Menü sil

## Frontend Yapısı

### 1. Views
- `Menus.tsx` - Menü listesi sayfası
- `MenuForm.tsx` - Menü oluşturma/düzenleme formu

### 2. Pages (Next.js App Router)
- `/admin/menus` - Menü listesi
- `/admin/menus/new` - Yeni menü oluştur
- `/admin/menus/[id]` - Menü düzenle

### 3. Services
- `menuService` in `admin.service.ts`
  - `getAll()` - Tüm menüleri getir
  - `getRootMenus()` - Ana menüleri getir
  - `getByCode()` - Kod ile menü getir
  - `save()` - Menü kaydet
  - `delete()` - Menü sil

### 4. UI Components
- AdminSidebar'a "Menü Yönetimi" linki eklendi
- Sistem bölümü altında görünür

## Veritabanı

### Tablo: menus
```sql
- id (BIGINT, PK, AUTO_INCREMENT)
- code (VARCHAR(255), UNIQUE with site_id)
- site_id (BIGINT, FK to sites)
- name_tr, name_en, name_de, name_fr, name_es, name_it (VARCHAR(255))
- icon (VARCHAR(100))
- display_order (INT)
- is_root (BOOLEAN)
- url (VARCHAR(500))
- parent_menu_id (BIGINT, FK to menus, self-reference)
- created_by, last_modified_by (VARCHAR(255))
- created_date, last_modified_date (TIMESTAMP)
```

### Junction Table: menu_user_groups
```sql
- menu_id (BIGINT, FK to menus)
- user_group_id (BIGINT, FK to usergroups)
- PRIMARY KEY (menu_id, user_group_id)
```

## Kurulum

### 1. Veritabanı Migration
```bash
# SQL script'i çalıştır
mysql -u username -p database_name < create-menu-table.sql
```

### 2. Backend Build
```bash
cd btcstore
mvn clean install
```

### 3. Frontend
```bash
cd btc-store
npm install
npm run dev
```

## Kullanım

### Yeni Menü Oluşturma
1. Admin paneline giriş yap
2. Sistem > Menü Yönetimi'ne git
3. "Yeni Menü" butonuna tıkla
4. Form alanlarını doldur:
   - Menü Adı (TR) - Zorunlu
   - Menü Adı (EN) - Opsiyonel
   - İkon - Emoji veya icon class
   - URL - Menü linki
   - Sıra - Görüntülenme sırası
   - Üst Menü - Alt menü ise parent seç
   - Ana Menü - Checkbox ile işaretle
5. "Kaydet" butonuna tıkla

### Menü Düzenleme
1. Menü listesinde düzenlemek istediğin menünün yanındaki "Düzenle" butonuna tıkla
2. Gerekli değişiklikleri yap
3. "Kaydet" butonuna tıkla

### Menü Silme
1. Menü listesinde silmek istediğin menünün yanındaki "Sil" butonuna tıkla
2. Onay dialogunda "Sil" butonuna tıkla

## API Endpoints

### GET /v1/menus
Tüm menüleri listeler (sıralı)

**Response:**
```json
{
  "status": "SUCCESS",
  "data": [
    {
      "code": "dashboard",
      "name": {
        "tr": "Kontrol Paneli",
        "en": "Dashboard"
      },
      "icon": "📊",
      "displayOrder": 1,
      "isRoot": true,
      "url": "/admin/dashboard"
    }
  ]
}
```

### GET /v1/menus/root
Sadece ana menüleri listeler

### GET /v1/menus/{code}
Belirli bir menüyü getirir

### POST /v1/menus
Yeni menü oluşturur veya mevcut menüyü günceller

**Request Body:**
```json
{
  "code": "dashboard",
  "name": {
    "tr": "Kontrol Paneli",
    "en": "Dashboard"
  },
  "icon": "📊",
  "displayOrder": 1,
  "isRoot": true,
  "url": "/admin/dashboard",
  "parentMenuCode": null,
  "userGroups": [
    { "code": "admin" }
  ]
}
```

### DELETE /v1/menus/{code}
Menüyü siler

## Güvenlik

- Tüm endpoint'ler `@PreAuthorize` ile korunmuştur
- `MenuModel` için READ, SAVE, DELETE yetkileri gereklidir
- User group bazlı erişim kontrolü yapılabilir

## Notlar

- Menü silme işlemi cascade olarak alt menüleri de siler
- Parent menu seçildiğinde `isRoot` otomatik olarak `false` olur
- En az bir dilde (TR veya EN) menü adı girilmesi zorunludur
- İkon alanına emoji veya CSS icon class'ı girilebilir

## Geliştirme Notları

- ModelMapper kullanılarak DTO-Entity dönüşümleri yapılır
- Site bazlı çalışır (multi-tenant)
- Audit alanları otomatik doldurulur (created_by, created_date, vb.)
