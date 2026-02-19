# Ürün İletişim - Privacy Policy Entegrasyonu

## 📋 Genel Bakış

ProductContact formu, CallRequest formu ile aynı şekilde Privacy Policy (KVKK) entegrasyonuna sahiptir.

## ✅ Mevcut Özellikler

### 1. Privacy Policy Yükleme
```typescript
const loadPrivacyPolicy = async () => {
  try {
    const response = await publicService.getCurrentPrivacyPolicy();
    if (response.status === 'SUCCESS' && response.data) {
      setPrivacyDocument(response.data);
    }
  } catch (error) {
    console.error('Error loading privacy policy:', error);
  }
};
```

### 2. Privacy Policy Gösterimi
- Checkbox ile onay alınır
- Short text gösterilir (veya fallback)
- "Görüntüle" butonu ile modal açılır
- Full content modal'da gösterilir

### 3. Backend'e Gönderim
```typescript
const requestData = {
  customerName: `${formData.name} ${formData.surname}`,
  customerEmail: formData.email,
  customerPhone: formData.phone,
  message: formData.message,
  acceptedLegalDocument: privacyDocument ? { 
    code: privacyDocument.code 
  } : undefined
};
```

## 🔄 İş Akışı

1. **Sayfa Yüklendiğinde:**
   - Privacy Policy dokümanı API'den çekilir
   - `GET /v1/public/legal-documents/privacy-policy/current`

2. **Form Gösterilir:**
   - Privacy document varsa short text gösterilir
   - Privacy document yoksa genel metin gösterilir
   - Checkbox her durumda gösterilir

3. **Kullanıcı Onaylar:**
   - Checkbox işaretlenir
   - "Görüntüle" butonuna tıklanabilir
   - Modal açılır ve full content gösterilir

4. **Form Gönderilir:**
   - Privacy document varsa code backend'e gönderilir
   - Backend call request'e bağlar
   - Database'de ilişki saklanır

## 📊 Veri Yapısı

### LegalDocument Interface
```typescript
interface LegalDocument {
  code: string;
  title: { tr?: string; en?: string };
  shortText: { tr?: string; en?: string };
  content: { tr?: string; en?: string };
}
```

### Backend Request
```json
{
  "customerName": "Ahmet Yılmaz",
  "customerEmail": "ahmet@example.com",
  "customerPhone": "+90 555 123 4567",
  "message": "Ürün hakkında bilgi almak istiyorum.",
  "acceptedLegalDocument": {
    "code": "privacy-policy-uuid"
  }
}
```

### Backend Response
```json
{
  "status": "SUCCESS",
  "data": {
    "id": 123,
    "code": "call-request-uuid",
    "acceptedLegalDocument": {
      "code": "privacy-policy-uuid",
      "title": { "tr": "Gizlilik Politikası" }
    }
  }
}
```

## 🎨 UI Bileşenleri

### Checkbox Bölümü
```tsx
<div className="flex items-start gap-3 bg-blue-50 p-4 rounded-xl">
  <input
    type="checkbox"
    id="kvkk"
    checked={formData.kvkkAccepted}
    onChange={(e) => setFormData({ ...formData, kvkkAccepted: e.target.checked })}
    required
    className="mt-1 w-5 h-5 text-blue-900 rounded focus:ring-2 focus:ring-blue-900"
  />
  <label htmlFor="kvkk" className="text-sm text-gray-700 flex-1">
    {getPrivacyShortText()}
    {privacyDocument && (
      <button
        type="button"
        onClick={() => setShowKvkkModal(true)}
        className="text-blue-900 hover:underline ml-2 font-semibold"
      >
        ({t('callRequest.viewKvkk')})
      </button>
    )}
  </label>
</div>
```

### Modal
```tsx
{privacyDocument && (
  <Modal
    isOpen={showKvkkModal}
    onClose={() => setShowKvkkModal(false)}
    title={privacyDocument.title?.tr || t('kvkk.title')}
    size="lg"
  >
    <div className="p-6">
      <RichContentRenderer htmlContent={getPrivacyContent()} />
    </div>
  </Modal>
)}
```

## 🔒 Güvenlik

### Frontend Validasyonu
- Checkbox zorunlu (required attribute)
- Form submit öncesi kontrol
- Alert gösterimi

### Backend Validasyonu
- Legal document code kontrolü
- Site bazlı filtreleme
- Active document kontrolü

## 🌍 Çoklu Dil Desteği

### Short Text
```typescript
const getPrivacyShortText = () => {
  if (!privacyDocument?.shortText) {
    return t('callRequest.kvkk');
  }
  const lang = getCurrentLanguage();
  return privacyDocument.shortText[lang] || 
         privacyDocument.shortText.tr || 
         t('callRequest.kvkk');
};
```

### Full Content
```typescript
const getPrivacyContent = () => {
  if (!privacyDocument?.content) return '';
  const lang = getCurrentLanguage();
  return privacyDocument.content[lang] || 
         privacyDocument.content.tr || '';
};
```

## 📝 Translation Keys

```json
{
  "callRequest": {
    "kvkk": "KVKK kapsamında kişisel verilerimin işlenmesini kabul ediyorum.",
    "viewKvkk": "Metni Görüntüle"
  },
  "kvkk": {
    "title": "Gizlilik Politikası"
  }
}
```

## 🧪 Test Senaryoları

### 1. Privacy Document Var
- ✅ Short text gösterilir
- ✅ "Görüntüle" butonu çalışır
- ✅ Modal açılır
- ✅ Full content gösterilir
- ✅ Code backend'e gönderilir

### 2. Privacy Document Yok
- ✅ Fallback text gösterilir
- ✅ "Görüntüle" butonu gösterilmez
- ✅ Checkbox yine de çalışır
- ✅ Code gönderilmez (undefined)

### 3. Checkbox İşaretlenmemiş
- ✅ Form submit edilmez
- ✅ Alert gösterilir
- ✅ Kullanıcı bilgilendirilir

### 4. API Hatası
- ✅ Console'a log yazılır
- ✅ Fallback text gösterilir
- ✅ Form yine de çalışır

## 🔄 CallRequest ile Karşılaştırma

| Özellik | CallRequest | ProductContact |
|---------|-------------|----------------|
| Privacy API | ✅ Aynı | ✅ Aynı |
| Short Text | ✅ Var | ✅ Var |
| Full Content Modal | ✅ Var | ✅ Var |
| Code Gönderimi | ✅ Var | ✅ Var |
| Fallback | ✅ Var | ✅ Var |
| Çoklu Dil | ✅ Var | ✅ Var |

## 📚 İlgili Dosyalar

### Frontend
- `btc-store/src/views/ProductContact.tsx`
- `btc-store/src/views/CallRequest.tsx`
- `btc-store/src/services/public.service.ts`

### Backend
- `btcstore/webapp/src/main/java/com/btc_store/controller/v1/PublicController.java`
- `btcstore/facade/src/main/java/com/btc_store/facade/impl/CallRequestFacadeImpl.java`
- `btcstore/domain/src/main/java/com/btc_store/domain/model/store/StoreCallRequestModel.java`

## ✅ Sonuç

ProductContact formu, CallRequest formu ile tamamen aynı Privacy Policy entegrasyonuna sahiptir:

1. ✅ Public API'den privacy policy çekiliyor
2. ✅ Short text gösteriliyor
3. ✅ Modal ile full content gösteriliyor
4. ✅ Onaylanırsa code backend'e gönderiliyor
5. ✅ Backend'de legal document ilişkisi kuruluyor
6. ✅ Çoklu dil desteği var
7. ✅ Fallback mekanizması var
8. ✅ Hata yönetimi var

Sistem tamamen hazır ve çalışıyor! 🎉
