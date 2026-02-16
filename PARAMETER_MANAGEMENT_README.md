# Parametre Yönetimi Modülü

Bu doküman, BTC Store uygulamasına eklenen Parametre Yönetimi modülünün kullanımını açıklar.

## Özellikler

- ✅ Parametre listeleme (sayfalama ile)
- ✅ Parametre ekleme
- ✅ Parametre düzenleme
- ✅ Parametre silme
- ✅ Çoklu dil desteği (TR, EN, DE, FR, ES, IT)
- ✅ Farklı veri tipleri (STRING, INTEGER, DOUBLE, BOOLEAN)
- ✅ Parametre tipleri (SYSTEM, USER)
- ✅ Şifreli parametre desteği
- ✅ SearchService entegrasyonu
- ✅ ModelService entegrasyonu

## Teknik Mimari

### Backend Katmanları

#### 1. Domain Katmanı
- **Model**: `ParameterModel` - JPA entity sınıfı
- **Data**: `ParameterData` - DTO sınıfı
- **Base Model**: `StoreParameterModel` - Temel parametre modeli

#### 2. Persistence Katmanı
- **DAO**: `ParameterDao` - JPA Repository interface
  - `findByCodeAndSite()` - Kod ve site'a göre parametre bulma
  - `findBySite()` - Site'a göre tüm parametreleri listeleme

#### 3. Service Katmanı
- **Interface**: `ParameterService`
- **Implementation**: `ParameterServiceImpl`
  - `getAllParameters()` - Tüm parametreleri getir
  - `getParameterByCode()` - Kod ile parametre getir

#### 4. Facade Katmanı
- **Interface**: `ParameterFacade`
- **Implementation**: `ParameterFacadeImpl`
  - `getAllParameters()` - Tüm parametreleri listele
  - `getParameterByCode()` - Kod ile parametre getir
  - `saveParameter()` - Parametre kaydet/güncelle
  - `deleteParameter()` - Parametre sil

#### 5. Controller Katmanı
- **Controller**: `ParameterController`
  - `GET /v1/parameters` - Tüm parametreleri listele
  - `GET /v1/parameters/{code}` - Kod ile parametre getir
  - `POST /v1/parameters` - Parametre kaydet/güncelle
  - `DELETE /v1/parameters/{code}` - Parametre sil

### Frontend Katmanları

#### 1. Service Katmanı
- **Service**: `parameterService` (admin.service.ts)
  - `getAll()` - Tüm parametreleri getir
  - `getByCode()` - Kod ile parametre getir
  - `save()` - Parametre kaydet
  - `delete()` - Parametre sil

#### 2. View Katmanı
- **ParametersAdmin**: Parametre listesi sayfası
  - Sayfalama desteği
  - Arama ve filtreleme
  - Düzenleme ve silme işlemleri
  
- **ParameterForm**: Parametre ekleme/düzenleme formu
  - Çoklu dil desteği
  - Veri tipi seçimi
  - Parametre tipi seçimi
  - Şifreleme seçeneği

#### 3. Route Katmanı
- `/admin/parameters` - Parametre listesi
- `/admin/parameters/new` - Yeni parametre ekleme
- `/admin/parameters/[code]` - Parametre düzenleme

## Veritabanı Yapısı

```sql
CREATE TABLE parameter_model (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    value VARCHAR(1000) NOT NULL,
    data_type VARCHAR(50),
    parameter_type VARCHAR(50),
    encrypt BOOLEAN DEFAULT FALSE,
    description_tr VARCHAR(500),
    description_en VARCHAR(500),
    description_de VARCHAR(500),
    description_fr VARCHAR(500),
    description_es VARCHAR(500),
    description_it VARCHAR(500),
    site_id BIGINT NOT NULL,
    created_date TIMESTAMP,
    modified_date TIMESTAMP,
    CONSTRAINT uk_parameter_code_site UNIQUE (code, site_id)
);
```

## Kurulum

### 1. Veritabanı Kurulumu
```bash
# SQL scriptini çalıştır
mysql -u [kullanıcı] -p [veritabanı] < create-parameter-table.sql
```

### 2. Backend Derleme
```bash
cd btcstore
mvn clean install
```

### 3. Frontend Kurulumu
```bash
cd btc-store
npm install
npm run dev
```

## Kullanım

### Backend API Örnekleri

#### Tüm Parametreleri Listeleme
```bash
GET /v1/parameters
Authorization: Bearer {token}
```

#### Parametre Getirme
```bash
GET /v1/parameters/{code}
Authorization: Bearer {token}
```

#### Parametre Kaydetme
```bash
POST /v1/parameters
Authorization: Bearer {token}
Content-Type: application/json

{
  "code": "SITE_NAME",
  "value": "BTC Store",
  "description": {
    "tr": "Site adı",
    "en": "Site name"
  },
  "dataType": "STRING",
  "parameterType": "SYSTEM",
  "encrypt": false
}
```

#### Parametre Silme
```bash
DELETE /v1/parameters/{code}
Authorization: Bearer {token}
```

### Frontend Kullanımı

1. Admin paneline giriş yapın
2. Sol menüden "Parametreler" seçeneğine tıklayın
3. Yeni parametre eklemek için "Yeni Parametre" butonuna tıklayın
4. Formu doldurun:
   - Kod: Benzersiz parametre kodu
   - Değer: Parametre değeri
   - Açıklama: Çoklu dil desteği ile açıklama
   - Veri Tipi: STRING, INTEGER, DOUBLE, BOOLEAN
   - Parametre Tipi: SYSTEM, USER
   - Şifreli: Hassas veriler için şifreleme
5. "Kaydet" butonuna tıklayın

## Veri Tipleri

- **STRING**: Metin değerler
- **INTEGER**: Tam sayı değerler
- **DOUBLE**: Ondalık sayı değerler
- **BOOLEAN**: true/false değerler

## Parametre Tipleri

- **SYSTEM**: Sistem parametreleri (yönetici tarafından yönetilir)
- **USER**: Kullanıcı parametreleri (kullanıcılar tarafından değiştirilebilir)

## Güvenlik

- Tüm endpoint'ler JWT token ile korunmaktadır
- Rol bazlı yetkilendirme mevcuttur:
  - READ: Parametre okuma yetkisi
  - SAVE: Parametre kaydetme yetkisi
  - DELETE: Parametre silme yetkisi
- Hassas parametreler şifrelenebilir

## Özelleştirme

### Yeni Veri Tipi Ekleme
1. `DataType` enum'ına yeni tip ekleyin
2. Frontend'de select option'a ekleyin

### Yeni Parametre Tipi Ekleme
1. `ParameterType` enum'ına yeni tip ekleyin
2. Frontend'de select option'a ekleyin

## Sorun Giderme

### Parametre Kaydedilmiyor
- Kod alanının benzersiz olduğundan emin olun
- Değer alanının dolu olduğunu kontrol edin
- Backend loglarını kontrol edin

### Parametre Listesi Yüklenmiyor
- SearchService'in doğru yapılandırıldığından emin olun
- Veritabanı bağlantısını kontrol edin
- Browser console'da hata mesajlarını kontrol edin

## Geliştirme Notları

- Parametre modeli `CodeBasedItemModel`'den türetilmiştir
- SearchService entegrasyonu sayfalama desteği sağlar
- ModelService CRUD operasyonlarını yönetir
- Frontend TypeScript ile tip güvenliği sağlar
- Responsive tasarım mobil uyumludur

## İletişim

Sorularınız için lütfen geliştirme ekibi ile iletişime geçin.
