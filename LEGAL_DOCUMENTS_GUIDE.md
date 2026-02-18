# KVKK / GDPR Yasal Doküman Yönetim Sistemi

## Genel Bakış

Bu sistem, KVKK ve GDPR uyumluluğu için gerekli yasal metinleri yönetmenizi sağlar. Kullanıcı kayıt sistemi olmayan, sadece çağrı formu olan siteler için tasarlanmıştır.

## Özellikler

### 1. Doküman Tipleri
- **KVKK**: KVKK Aydınlatma Metni
- **GDPR**: GDPR Privacy Policy
- **PRIVACY_POLICY**: Gizlilik Politikası
- **TERMS_OF_USE**: Kullanım Koşulları
- **COOKIE_POLICY**: Çerez Politikası
- **CONSENT_TEXT**: Özel Onay Metni

### 2. Çok Dilli Destek
Tüm dokümanlar 6 dilde yönetilebilir:
- 🇹🇷 Türkçe
- 🇬🇧 İngilizce
- 🇩🇪 Almanca
- 🇫🇷 Fransızca
- 🇪🇸 İspanyolca
- 🇮🇹 İtalyanca

### 3. Versiyon Yönetimi
- Her doküman için versiyon numarası
- Yürürlük tarihi
- Güncel versiyon işaretleme
- Eski versiyonları saklama

### 4. Form Entegrasyonu
- **showOnCallForm**: Çağrı formunda gösterilsin mi?
- **isRequired**: Onay zorunlu mu?
- **shortText**: Formda gösterilecek kısa metin
- **displayOrder**: Gösterim sırası

## Kurulum

### 1. Veritabanı Kurulumu

```bash
# SQL script'i çalıştırın
mysql -u username -p database_name < LEGAL_DOCUMENTS_SETUP.sql
```

Script şunları yapar:
- `legal_documents` tablosunu oluşturur
- Örnek KVKK, GDPR ve Çerez Politikası dokümanlarını ekler
- Gerekli indeksleri oluşturur

### 2. Backend Entegrasyonu

Model dosyaları oluşturuldu:
- `StoreLegalDocumentModel.java` - JPA Entity
- `LegalDocumentModel.java` - Custom Entity
- `StoreLegalDocumentData.java` - Data Transfer Object
- `LegalDocumentType.java` - Enum

### 3. Frontend Entegrasyonu

Yönetim paneli sayfası:
- `LegalDocumentsForm.tsx` - Admin yönetim sayfası

## Kullanım

### Yönetim Panelinde

1. **Yeni Doküman Ekleme**
   - "Yeni Doküman" butonuna tıklayın
   - Doküman tipini seçin
   - Kod, başlık ve içeriği girin
   - Versiyon ve yürürlük tarihini belirleyin
   - Form ayarlarını yapın
   - Kaydedin

2. **Doküman Düzenleme**
   - Doküman kartındaki düzenle ikonuna tıklayın
   - Gerekli değişiklikleri yapın
   - Kaydedin

3. **Versiyon Güncelleme**
   - Mevcut dokümanı düzenleyin
   - Versiyon numarasını artırın (örn: 1.0 → 1.1)
   - Yeni yürürlük tarihi belirleyin
   - "Güncel Versiyon" işaretini koyun
   - Kaydedin
   - Eski versiyon otomatik olarak "güncel değil" olarak işaretlenir

### Çağrı Formunda Kullanım

Formda gösterilecek dokümanlar için:

```typescript
// API'den form için dokümanları çek
const response = await fetch('/api/legal-documents/for-call-form');
const documents = await response.json();

// Her doküman için checkbox oluştur
documents.forEach(doc => {
  if (doc.showOnCallForm) {
    // Checkbox render et
    <label>
      <input 
        type="checkbox" 
        required={doc.isRequired}
      />
      {doc.shortText[currentLanguage]}
      <a href={`/legal/${doc.code}`}>Detaylı Bilgi</a>
    </label>
  }
});
```

### API Endpoint Önerileri

```java
// Backend'de oluşturulması gereken endpoint'ler

// Tüm dokümanları listele (Admin)
GET /api/admin/legal-documents

// Tek doküman getir
GET /api/admin/legal-documents/{id}

// Doküman kaydet/güncelle
POST /api/admin/legal-documents

// Doküman sil
DELETE /api/admin/legal-documents/{id}

// Çağrı formu için aktif dokümanları getir (Public)
GET /api/legal-documents/for-call-form

// Doküman detayını göster (Public)
GET /api/legal-documents/{code}
```

## Örnek Senaryolar

### Senaryo 1: KVKK Metni Güncelleme

1. Yönetim paneline girin
2. Mevcut KVKK dokümanını düzenleyin
3. Versiyon numarasını 1.0'dan 1.1'e çıkarın
4. Yeni içeriği girin
5. Yürürlük tarihini belirleyin
6. "Güncel Versiyon" işaretini koyun
7. Kaydedin

Sonuç: Yeni versiyon aktif olur, eski versiyon arşivde kalır.

### Senaryo 2: Yeni Onay Metni Ekleme

1. "Yeni Doküman" butonuna tıklayın
2. Tip: "CONSENT_TEXT" seçin
3. Kod: "marketing-consent" girin
4. Başlık ve içeriği girin
5. Kısa metin: "Pazarlama iletişimi almayı kabul ediyorum"
6. "Çağrı Formunda Göster" işaretleyin
7. "Onay Zorunlu" işaretini KALDIRIN (opsiyonel onay)
8. Sıralama: 3
9. Kaydedin

Sonuç: Çağrı formunda opsiyonel bir checkbox olarak görünür.

### Senaryo 3: Çok Dilli İçerik

1. Doküman düzenleyin
2. Türkçe içeriği girin
3. "Diğer Diller" butonuna tıklayın
4. İngilizce, Almanca vb. içerikleri girin
5. Kaydedin

Sonuç: Kullanıcı dil seçimine göre doğru içerik gösterilir.

## Veritabanı Yapısı

```sql
legal_documents
├── id (PK)
├── code (Unique per site)
├── site_id (FK)
├── document_type (ENUM)
├── title_* (6 dil)
├── content_* (6 dil)
├── short_text_* (6 dil)
├── version
├── effective_date
├── is_current_version
├── show_on_call_form
├── is_required
├── display_order
└── audit fields
```

## Best Practices

### 1. Versiyon Yönetimi
- Semantic versioning kullanın (1.0, 1.1, 2.0)
- Büyük değişiklikler için major versiyon artırın
- Küçük düzeltmeler için minor versiyon artırın
- Eski versiyonları silmeyin, arşivde tutun

### 2. İçerik Yazımı
- Açık ve anlaşılır dil kullanın
- Yasal gereklilikleri karşılayın
- HTML formatında yazın (başlıklar, listeler vb.)
- Kısa metinleri özenle yazın (formda görünecek)

### 3. Form Entegrasyonu
- Zorunlu onayları minimize edin
- Kısa metinleri net yazın
- "Detaylı Bilgi" linki ekleyin
- Sıralamayı mantıklı yapın (KVKK önce, pazarlama sonda)

### 4. Çok Dilli Yönetim
- En az Türkçe ve İngilizce doldurun
- Profesyonel çeviri kullanın
- Yasal terimleri doğru çevirin
- Tüm dillerde aynı anlamı koruyun

## Güvenlik

- Sadece admin kullanıcılar düzenleyebilir
- Public endpoint'ler sadece aktif dokümanları gösterir
- Silme işlemi soft delete olmalı (active=false)
- Audit log tutun (kim, ne zaman değiştirdi)

## Uyumluluk Kontrol Listesi

✅ KVKK Aydınlatma Metni var mı?
✅ GDPR Privacy Policy var mı?
✅ Çerez Politikası var mı?
✅ Tüm metinler güncel mi?
✅ Yürürlük tarihleri doğru mu?
✅ Çağrı formunda gösteriliyor mu?
✅ Kullanıcı onayı alınıyor mu?
✅ Onaylar veritabanında saklanıyor mu?
✅ Çok dilli içerikler tam mı?

## Sorun Giderme

### Doküman formda görünmüyor
- `show_on_call_form` = true olmalı
- `is_current_version` = true olmalı
- `active` = true olmalı
- `effective_date` geçmiş tarih olmalı

### Eski versiyon hala görünüyor
- Yeni versiyonu kaydederken `is_current_version` = true yapın
- Backend'de eski versiyonu otomatik false yapmalı

### Çok dilli içerik eksik
- En az Türkçe ve İngilizce doldurun
- Frontend'de fallback mekanizması olmalı (TR yoksa EN göster)

## Destek

Sorularınız için:
- Backend: Java Spring Boot
- Frontend: Next.js + TypeScript
- Database: MySQL 8.0+
