# Call Request - Privacy Policy Entegrasyonu Rehberi

## 📋 Genel Bakış

Bu entegrasyon, call request formunda Privacy Policy metninin dinamik olarak gösterilmesini ve hangi versiyonun onaylandığının kaydedilmesini sağlar.

## 🎯 Özellikler

### 1. Dinamik Privacy Policy Metni
- Public endpoint üzerinden güncel Privacy Policy dokümanı çekilir
- `shortText` alanı checkbox yanında gösterilir
- "Gizlilik Politikasını görüntüle" linki ile tam içerik modal'da açılır
- Çok dilli destek (TR, EN, DE, FR, ES, IT)

### 2. Versiyon Takibi
- Her call request hangi Privacy Policy versiyonunu onayladığını tutar
- Admin panelinde onaylanan versiyon görüntülenir
- Legal document ile ilişki kurulur (foreign key)

### 3. Yönetim Paneli
- Call request detayında Privacy Policy versiyon bilgisi gösterilir
- Hangi kod ve versiyonun onaylandığı görülebilir

## 🏗️ Yapılan Değişiklikler

### Backend

#### 1. Domain Katmanı

**StoreCallRequestModel.java**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = StoreLegalDocumentModel.LEGAL_DOCUMENT_RELATION)
private LegalDocumentModel acceptedLegalDocument;
```

**StoreCallRequestData.java**
```java
private Long acceptedLegalDocumentId;
private String acceptedLegalDocumentCode;
private String acceptedLegalDocumentVersion;
```

#### 2. Facade Katmanı

**LegalDocumentFacade.java**
```java
/**
 * Get current version of Privacy Policy document for public call request form
 */
LegalDocumentData getCurrentPrivacyPolicyDocument();
```

**LegalDocumentFacadeImpl.java**
```java
@Override
public LegalDocumentData getCurrentPrivacyPolicyDocument() {
    var siteModel = siteService.getCurrentSite();
    
    // Get current Privacy Policy document
    var privacyPolicyDocuments = searchService.search(LegalDocumentModel.class,
            Map.of("documentType", LegalDocumentType.PRIVACY_POLICY,
                   "site", siteModel,
                   "isCurrentVersion", true,
                   "active", true),
            SearchOperator.AND);
    
    if (privacyPolicyDocuments.isEmpty()) {
        log.warn("No current Privacy Policy document found for site: {}", siteModel.getCode());
        return null;
    }
    
    var privacyPolicyDocument = privacyPolicyDocuments.get(0);
    return modelMapper.map(privacyPolicyDocument, LegalDocumentData.class);
}
```

**CallRequestFacadeImpl.java**
```java
// Legal document'i set et
if (callRequestData.getAcceptedLegalDocumentId() != null) {
    try {
        var legalDocument = legalDocumentFacade.getLegalDocumentByCode(
            callRequestData.getAcceptedLegalDocumentCode()
        );
        if (legalDocument != null) {
            var legalDocModel = modelMapper.map(legalDocument, LegalDocumentModel.class);
            callRequestModel.setAcceptedLegalDocument(legalDocModel);
        }
    } catch (Exception e) {
        log.warn("Could not set legal document for call request: {}", e.getMessage());
    }
}

// Data'ya map ederken
private void mapLegalDocument(CallRequestModel model, CallRequestData data) {
    if (model.getAcceptedLegalDocument() != null) {
        data.setAcceptedLegalDocumentId(model.getAcceptedLegalDocument().getId());
        data.setAcceptedLegalDocumentCode(model.getAcceptedLegalDocument().getCode());
        data.setAcceptedLegalDocumentVersion(model.getAcceptedLegalDocument().getVersion());
    }
}
```

#### 3. Controller Katmanı

**PublicController.java**
```java
@GetMapping("/legal-documents/privacy-policy/current")
@Operation(summary = "Get current Privacy Policy document for call request form (Public)")
public ServiceResponseData getCurrentPrivacyPolicyDocument(@Parameter(description = "IsoCode for validation message internalization")
                                                           @RequestParam(required = false) String isoCode) {
    log.info("Inside getCurrentPrivacyPolicyDocument of PublicController.");
    var privacyPolicyDocument = legalDocumentFacade.getCurrentPrivacyPolicyDocument();
    var responseData = new ServiceResponseData();
    responseData.setStatus(ProcessStatus.SUCCESS);
    responseData.setData(privacyPolicyDocument);
    return responseData;
}
```

### Frontend

#### 1. Type Definitions

**legalDocument.ts**
```typescript
export interface LegalDocument {
  id: number;
  code: string;
  documentType: LegalDocumentType;
  title: LocalizedText;
  content: LocalizedText;
  shortText: LocalizedText;
  version: string;
  effectiveDate?: string;
  isCurrentVersion: boolean;
  active: boolean;
}
```

**callRequest.ts**
```typescript
export interface CallRequest {
  // ... existing fields
  acceptedLegalDocumentId?: number;
  acceptedLegalDocumentCode?: string;
  acceptedLegalDocumentVersion?: string;
}
```

#### 2. Call Request Form

**CallRequest.tsx**
```typescript
// Privacy Policy dokümanını çek
useEffect(() => {
  const fetchPrivacyDocument = async () => {
    try {
      const response = await fetch('http://localhost:9090/webapp/api/v1/public/legal-documents/privacy-policy/current');
      if (response.ok) {
        const result = await response.json();
        if (result.status === 'SUCCESS' && result.data) {
          setPrivacyDocument(result.data);
        }
      }
    } catch (error) {
      console.error('Privacy Policy document fetch error:', error);
    }
  };
  fetchPrivacyDocument();
}, []);

// Dinamik shortText göster
const getPrivacyShortText = () => {
  if (!privacyDocument?.shortText) {
    return t('callRequest.kvkk');
  }
  const lang = getCurrentLanguage();
  return privacyDocument.shortText[lang] || privacyDocument.shortText.tr || t('callRequest.kvkk');
};

// Form submit'te legal document bilgilerini gönder
body: JSON.stringify({
  customerName: `${formData.name} ${formData.surname}`.trim(),
  customerEmail: formData.email,
  customerPhone: formData.phone,
  message: formData.message,
  gdprConsent: formData.privacyAccepted,
  acceptedLegalDocumentId: privacyDocument?.id,
  acceptedLegalDocumentCode: privacyDocument?.code,
  acceptedLegalDocumentVersion: privacyDocument?.version,
})
```

#### 3. Admin Panel

**CallRequestDetail.tsx**
```typescript
{request.gdprConsent && (
  <div className="flex items-center gap-3 bg-green-50 p-3 rounded-lg">
    <Shield className="w-5 h-5 text-green-600" />
    <div className="flex-1">
      <div className="text-sm text-green-700 font-medium">
        KVKK/GDPR onayı verilmiş
      </div>
      {request.acceptedLegalDocumentVersion && (
        <div className="text-xs text-green-600 mt-1">
          Onaylanan Versiyon: {request.acceptedLegalDocumentVersion}
          {request.acceptedLegalDocumentCode && (
            <span className="ml-2 text-gray-500">({request.acceptedLegalDocumentCode})</span>
          )}
        </div>
      )}
    </div>
  </div>
)}
```

### Database

**CALL_REQUEST_LEGAL_DOCUMENT_RELATION.sql**
```sql
-- Add foreign key column
ALTER TABLE call_request 
ADD COLUMN legal_document_id BIGINT NULL;

-- Add foreign key constraint
ALTER TABLE call_request 
ADD CONSTRAINT fk_call_request_legal_document 
FOREIGN KEY (legal_document_id) 
REFERENCES legal_documents(id);

-- Add index for better query performance
CREATE INDEX idx_call_request_legal_document 
ON call_request(legal_document_id);
```

## 🚀 Kurulum Adımları

### 1. Database Migration
```bash
# SQL script'i çalıştır
mysql -u root -p btcstore < CALL_REQUEST_LEGAL_DOCUMENT_RELATION.sql
```

### 2. Backend Deploy
```bash
cd btcstore
mvn clean install
mvn spring-boot:run
```

### 3. Frontend Deploy
```bash
cd btc-store
npm install
npm run build
npm start
```

## 📊 API Endpoints

### Public Endpoints

#### Get Current Privacy Policy Document
```
GET /api/v1/public/legal-documents/privacy-policy/current
```

**Response:**
```json
{
  "status": "SUCCESS",
  "data": {
    "id": 1,
    "code": "privacy-policy-v1-0-20240219143022",
    "documentType": "PRIVACY_POLICY",
    "title": {
      "tr": "Gizlilik Politikası",
      "en": "Privacy Policy"
    },
    "content": {
      "tr": "<html>...</html>",
      "en": "<html>...</html>"
    },
    "shortText": {
      "tr": "Gizlilik Politikasını okudum, kabul ediyorum",
      "en": "I have read and accept the Privacy Policy"
    },
    "version": "1.0",
    "isCurrentVersion": true,
    "active": true
  }
}
```

#### Create Call Request
```
POST /api/v1/public/call-requests
```

**Request Body:**
```json
{
  "customerName": "Ahmet Yılmaz",
  "customerEmail": "ahmet@example.com",
  "customerPhone": "+905551234567",
  "message": "Ürün hakkında bilgi almak istiyorum",
  "gdprConsent": true,
  "acceptedLegalDocumentId": 1,
  "acceptedLegalDocumentCode": "privacy-policy-v1-0-20240219143022",
  "acceptedLegalDocumentVersion": "1.0"
}
```

## 🔍 Test Senaryoları

### Senaryo 1: Yeni Call Request Oluşturma
1. Call request formunu aç
2. Privacy Policy metni otomatik olarak yüklenmeli
3. Checkbox yanında shortText görünmeli
4. "Gizlilik Politikasını görüntüle" linkine tıkla
5. Modal'da tam içerik ve versiyon görünmeli
6. Formu doldur ve gönder
7. Backend'de legal_document_id kaydedilmeli

### Senaryo 2: Admin Panelde Görüntüleme
1. Admin panele giriş yap
2. Call request detayına git
3. Privacy Policy onay bilgisi görünmeli
4. Onaylanan versiyon ve kod görünmeli

### Senaryo 3: Privacy Policy Versiyonu Güncelleme
1. Admin panelde Privacy Policy dokümanını güncelle
2. Versiyon 1.0 → 1.1 olsun
3. Yeni call request oluştur
4. Yeni versiyon (1.1) kaydedilmeli
5. Eski call request'ler eski versiyon (1.0) ile kalmalı

## 🎯 Önemli Notlar

### 1. Versiyon Yönetimi
- Her call request hangi Privacy Policy versiyonunu onayladığını tutar
- Privacy Policy güncellendiğinde eski call request'ler etkilenmez
- Yeni call request'ler güncel versiyonu kullanır

### 2. Çok Dilli Destek
- shortText her dilde farklı olabilir
- Frontend kullanıcının dil seçimine göre doğru metni gösterir
- Fallback: TR → EN → Default text

### 3. Null Safety
- KVKK dokümanı yoksa default text gösterilir
- Legal document ilişkisi opsiyoneldir (nullable)
- Eski call request'lerde legal_document_id null olabilir

### 4. Performance
- KVKK dokümanı sayfa yüklenirken bir kez çekilir
- Lazy loading ile legal document ilişkisi
- Index ile hızlı sorgulama

## 🔐 Güvenlik

### 1. Public Endpoint
- Sadece güncel ve aktif Privacy Policy dökümanı döner
- Site bazlı filtreleme yapılır
- Hassas bilgiler expose edilmez

### 2. Data Validation
- Backend'de Privacy Policy onayı zorunlu
- Frontend'de checkbox required
- IP adresi kaydedilir (audit trail)

### 3. GDPR Uyumluluğu
- Hangi versiyonun onaylandığı kayıtlı
- Kullanıcı onayı kanıtlanabilir
- Audit trail tam

## 📞 Sorun Giderme

### Privacy Policy metni görünmüyor
- Backend'de Privacy Policy dokümanı var mı kontrol et
- `isCurrentVersion = true` ve `active = true` olmalı
- `documentType = PRIVACY_POLICY` olmalı

### Versiyon kaydedilmiyor
- `acceptedLegalDocumentCode` doğru gönderiliyor mu?
- Legal document ilişkisi kurulabildi mi?
- Log'larda hata var mı?

### Eski versiyon gösteriliyor
- Browser cache temizle
- API'den güncel versiyon dönüyor mu kontrol et
- Frontend state güncellenmiş mi?

## 📚 İlgili Dökümanlar

- [LEGAL_DOCUMENTS_GUIDE.md](LEGAL_DOCUMENTS_GUIDE.md) - Legal documents sistemi genel rehberi
- [CALL_REQUEST_SYSTEM_GUIDE.md](CALL_REQUEST_SYSTEM_GUIDE.md) - Call request sistemi rehberi
- [CALL_REQUEST_LEGAL_DOCUMENT_RELATION.sql](CALL_REQUEST_LEGAL_DOCUMENT_RELATION.sql) - Database migration

---

**Hazırlayan:** Kiro AI Assistant  
**Tarih:** 2024-02-19  
**Versiyon:** 1.0
