# Call Request - Privacy Policy Entegrasyonu Uygulama Özeti

## ✅ Tamamlanan İşler

### Backend Değişiklikleri

#### 1. Domain Katmanı
- ✅ `StoreCallRequestModel.java` - `acceptedLegalDocument` ilişkisi eklendi
- ✅ `StoreCallRequestData.java` - Legal document bilgi alanları eklendi
  - `acceptedLegalDocumentId`
  - `acceptedLegalDocumentCode`
  - `acceptedLegalDocumentVersion`

#### 2. Facade Katmanı
- ✅ `LegalDocumentFacade.java` - `getCurrentPrivacyPolicyDocument()` metodu eklendi
- ✅ `LegalDocumentFacadeImpl.java` - Privacy Policy dokümanı getirme implementasyonu
- ✅ `CallRequestFacadeImpl.java` - Legal document set etme ve mapping

#### 3. Controller Katmanı
- ✅ `PublicController.java` - `/legal-documents/privacy-policy/current` endpoint eklendi
- ✅ Import eklendi: `LegalDocumentFacade`

### Frontend Değişiklikleri

#### 1. Type Definitions
- ✅ `legalDocument.ts` - Yeni tip tanımları oluşturuldu
  - `LegalDocument` interface
  - `LegalDocumentType` enum
  - `LocalizedText` interface

#### 2. Call Request Types
- ✅ `callRequest.ts` - Legal document alanları eklendi
  - `acceptedLegalDocumentId`
  - `acceptedLegalDocumentCode`
  - `acceptedLegalDocumentVersion`

#### 3. Call Request Form
- ✅ `CallRequest.tsx` - Dinamik Privacy Policy entegrasyonu
  - Privacy Policy dokümanı fetch edilir
  - `shortText` dinamik olarak gösterilir
  - Modal'da tam içerik ve versiyon gösterilir
  - Form submit'te legal document bilgileri gönderilir

#### 4. Admin Panel
- ✅ `CallRequestDetail.tsx` - Privacy Policy versiyon bilgisi gösterimi
  - Onaylanan versiyon görüntülenir
  - Doküman kodu görüntülenir

### Database
- ✅ `CALL_REQUEST_LEGAL_DOCUMENT_RELATION.sql` - Migration script oluşturuldu
  - `legal_document_id` kolonu eklendi
  - Foreign key constraint eklendi
  - Index eklendi

### Dokümantasyon
- ✅ `CALL_REQUEST_KVKK_INTEGRATION_GUIDE.md` - Detaylı kullanım rehberi
- ✅ `CALL_REQUEST_KVKK_IMPLEMENTATION_SUMMARY.md` - Bu dosya

## 📊 Değişiklik Özeti

### Değiştirilen Dosyalar

**Backend (Java):**
1. `btcstore/domain/src/main/java/com/btc_store/domain/model/store/StoreCallRequestModel.java`
2. `btcstore/domain/src/main/java/com/btc_store/domain/data/store/StoreCallRequestData.java`
3. `btcstore/facade/src/main/java/com/btc_store/facade/LegalDocumentFacade.java`
4. `btcstore/facade/src/main/java/com/btc_store/facade/impl/LegalDocumentFacadeImpl.java`
5. `btcstore/facade/src/main/java/com/btc_store/facade/impl/CallRequestFacadeImpl.java`
6. `btcstore/webapp/src/main/java/com/btc_store/controller/v1/PublicController.java`

**Frontend (TypeScript/React):**
7. `btc-store/src/types/legalDocument.ts` (YENİ)
8. `btc-store/src/types/callRequest.ts`
9. `btc-store/src/views/CallRequest.tsx`
10. `btc-store/src/views/admin/CallRequestDetail.tsx`

**Database:**
11. `btcstore/CALL_REQUEST_LEGAL_DOCUMENT_RELATION.sql` (YENİ)

**Dokümantasyon:**
12. `btcstore/CALL_REQUEST_KVKK_INTEGRATION_GUIDE.md` (YENİ)
13. `btcstore/CALL_REQUEST_KVKK_IMPLEMENTATION_SUMMARY.md` (YENİ)

## 🔄 İş Akışı

### 1. Kullanıcı Tarafı (Frontend)
```
1. Sayfa yüklenir
   ↓
2. GET /api/v1/public/legal-documents/privacy-policy/current
   ↓
3. Privacy Policy dokümanı state'e kaydedilir
   ↓
4. shortText checkbox yanında gösterilir
   ↓
5. Kullanıcı formu doldurur
   ↓
6. POST /api/v1/public/call-requests
   - customerName, email, phone, message
   - gdprConsent: true
   - acceptedLegalDocumentId
   - acceptedLegalDocumentCode
   - acceptedLegalDocumentVersion
```

### 2. Backend Tarafı
```
1. PublicController.createCallRequest()
   ↓
2. CallRequestFacade.createCallRequest()
   ↓
3. Legal document code ile doküman bulunur
   ↓
4. CallRequestModel.acceptedLegalDocument set edilir
   ↓
5. CallRequestService.createCallRequest()
   ↓
6. Database'e kaydedilir (legal_document_id ile)
   ↓
7. RabbitMQ event gönderilir
   ↓
8. Mail gönderilir
```

### 3. Admin Panel
```
1. Call request detayı açılır
   ↓
2. CallRequestData'da legal document bilgileri var
   ↓
3. Privacy Policy onay bilgisi gösterilir
   - "KVKK/GDPR onayı verilmiş"
   - "Onaylanan Versiyon: 1.0"
   - "(privacy-policy-v1-0-20240219143022)"
```

## 🎯 Özellikler

### ✅ Dinamik Privacy Policy Metni
- Yönetim panelinden Privacy Policy metni güncellenebilir
- Güncel versiyon otomatik olarak formda gösterilir
- Çok dilli destek (TR, EN, DE, FR, ES, IT)

### ✅ Versiyon Takibi
- Her call request hangi Privacy Policy versiyonunu onayladığını tutar
- Admin panelde görüntülenebilir
- Audit trail için kayıt tutulur

### ✅ Çok Dilli Destek
- shortText her dilde farklı olabilir
- Kullanıcının dil seçimine göre doğru metin gösterilir
- Fallback mekanizması: TR → EN → Default

### ✅ Modal Görünüm
- "Gizlilik Politikasını görüntüle" linki
- Modal'da tam içerik gösterilir
- Versiyon bilgisi gösterilir

## 🚀 Deployment Adımları

### 1. Database Migration
```bash
# Production database'e bağlan
mysql -u username -p database_name

# Migration script'i çalıştır
source CALL_REQUEST_LEGAL_DOCUMENT_RELATION.sql;

# Kontrol et
DESCRIBE call_request;
SHOW INDEX FROM call_request;
```

### 2. Backend Deploy
```bash
cd btcstore

# Build
mvn clean install

# Test
mvn test

# Deploy
mvn spring-boot:run
# veya
java -jar target/btcstore.jar
```

### 3. Frontend Deploy
```bash
cd btc-store

# Dependencies
npm install

# Build
npm run build

# Deploy
npm start
# veya
pm2 start npm --name "btc-store" -- start
```

## 🧪 Test Checklist

### Backend Tests
- [ ] KVKK dokümanı getirme endpoint'i çalışıyor mu?
- [ ] Call request oluştururken legal document set ediliyor mu?
- [ ] Foreign key constraint çalışıyor mu?
- [ ] Legal document silindiğinde call request etkileniyor mu?

### Frontend Tests
- [ ] KVKK dokümanı sayfa yüklenirken çekiliyor mu?
- [ ] shortText doğru gösteriliyor mu?
- [ ] Modal açılıyor ve içerik gösteriliyor mu?
- [ ] Versiyon bilgisi gösteriliyor mu?
- [ ] Form submit'te legal document bilgileri gönderiliyor mu?

### Integration Tests
- [ ] End-to-end: Form doldur → Submit → Database kontrol
- [ ] Admin panel: Call request detayında versiyon görünüyor mu?
- [ ] KVKK güncelleme: Yeni versiyon formda görünüyor mu?
- [ ] Eski call request'ler eski versiyon ile kalıyor mu?

### Edge Cases
- [ ] KVKK dokümanı yoksa ne oluyor?
- [ ] Legal document silinirse call request ne oluyor?
- [ ] Çok dilli içerik eksikse fallback çalışıyor mu?
- [ ] Network hatası durumunda ne oluyor?

## 📝 Notlar

### Önemli Noktalar
1. **Backward Compatibility**: Eski call request'lerde `legal_document_id` NULL olabilir
2. **Null Safety**: Frontend ve backend'de null check'ler var
3. **Performance**: Lazy loading ile legal document ilişkisi
4. **Security**: Public endpoint sadece güncel ve aktif dokümanı döner

### Gelecek Geliştirmeler
- [ ] Diğer legal document tipleri için de entegrasyon (GDPR, Cookie Policy)
- [ ] Call request listesinde KVKK versiyon filtresi
- [ ] KVKK versiyon değişiklik raporu
- [ ] Kullanıcı onay geçmişi sayfası

## 🔗 İlgili Linkler

- [LEGAL_DOCUMENTS_GUIDE.md](LEGAL_DOCUMENTS_GUIDE.md)
- [CALL_REQUEST_SYSTEM_GUIDE.md](CALL_REQUEST_SYSTEM_GUIDE.md)
- [CALL_REQUEST_KVKK_INTEGRATION_GUIDE.md](CALL_REQUEST_KVKK_INTEGRATION_GUIDE.md)

---

**Hazırlayan:** Kiro AI Assistant  
**Tarih:** 2024-02-19  
**Versiyon:** 1.0  
**Durum:** ✅ Tamamlandı
