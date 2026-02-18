# Call Request Priority Özelliği - Tamamlandı ✅

## Özet
Call Request sistemine öncelik (priority) özelliği eklendi. Kullanıcılar artık her talebin önceliğini görebilir ve güncelleyebilir.

## Backend Değişiklikleri ✅

### 1. Yeni Dosyalar
- `CallRequestPriority.java` - Enum (LOW, MEDIUM, HIGH, URGENT)
- `CALL_REQUEST_ADD_PRIORITY.sql` - Database migration script

### 2. Güncellenen Dosyalar
- `StoreCallRequestModel.java` - priority field eklendi
- `StoreCallRequestData.java` - priority field eklendi
- `CallRequestService.java` - updatePriority() metodu
- `CallRequestServiceImpl.java` - Implementation + email template desteği
- `CallRequestFacade.java` - updatePriority() metodu
- `CallRequestFacadeImpl.java` - Implementation
- `CallRequestController.java` - POST /update-priority endpoint

### 3. Email Template Desteği
Email template'lerinde priority bilgisi dinamik olarak ekleniyor:
- `{{priority}}` - Türkçe label (Düşük, Orta, Yüksek, Acil)
- `{{priorityClass}}` - CSS class (low, medium, high, urgent)

## Frontend Değişiklikleri ✅

### 1. Types (callRequest.ts)
```typescript
export enum CallRequestPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  URGENT = 'URGENT',
}

export const PRIORITY_LABELS: Record<CallRequestPriority, string> = {
  [CallRequestPriority.LOW]: 'Düşük',
  [CallRequestPriority.MEDIUM]: 'Orta',
  [CallRequestPriority.HIGH]: 'Yüksek',
  [CallRequestPriority.URGENT]: 'Acil',
};

export const PRIORITY_COLORS: Record<CallRequestPriority, string> = {
  [CallRequestPriority.LOW]: 'bg-gray-100 text-gray-700',
  [CallRequestPriority.MEDIUM]: 'bg-blue-100 text-blue-700',
  [CallRequestPriority.HIGH]: 'bg-orange-100 text-orange-700',
  [CallRequestPriority.URGENT]: 'bg-red-100 text-red-700',
};
```

### 2. Service (admin.service.ts)
```typescript
updatePriority: (id: number, priority: string) => 
  apiClient.post<any>(`/v1/call-requests/${id}/update-priority?priority=${priority}`)
```

### 3. Components

#### CallRequestDetail.tsx
- ✅ Priority badge gösterimi (başlıkta)
- ✅ Priority bilgisi Quick Info'da
- ✅ "Öncelik Güncelle" butonu
- ✅ Priority güncelleme modal'ı
- ✅ Öncelik seviyeleri açıklaması

#### CallRequestsAdmin.tsx
- ✅ Liste'de priority badge gösterimi
- ✅ Durum ve öncelik yan yana

#### MyCallRequests.tsx
- ✅ Priority badge gösterimi
- ✅ Durum, öncelik ve zaman badge'leri yan yana

## Kullanım

### 1. Database Migration
```bash
cd btcstore
psql -U your_user -d your_database -f CALL_REQUEST_ADD_PRIORITY.sql
```

### 2. Backend Derleme
```bash
cd btcstore
mvn clean install
```

### 3. Frontend
```bash
cd btc-store
npm run dev
```

### 4. Öncelik Güncelleme
1. Call Request detay sayfasına git
2. "Öncelik Güncelle" butonuna tıkla
3. Yeni önceliği seç
4. "Güncelle" butonuna tıkla

## Öncelik Seviyeleri

| Seviye | Label | Renk | Kullanım |
|--------|-------|------|----------|
| LOW | Düşük | Gri | Standart takip |
| MEDIUM | Orta | Mavi | Normal öncelik (varsayılan) |
| HIGH | Yüksek | Turuncu | Hızlı yanıt gerekli |
| URGENT | Acil | Kırmızı | Anında müdahale gerekli |

## API Endpoints

### Öncelik Güncelleme
```
POST /api/v1/call-requests/{id}/update-priority?priority={PRIORITY}
```

**Parametreler:**
- `id` (path) - Call Request ID
- `priority` (query) - LOW, MEDIUM, HIGH, URGENT

**Örnek:**
```bash
curl -X POST "http://localhost:8080/api/v1/call-requests/123/update-priority?priority=HIGH" \
  -H "Authorization: Bearer {token}"
```

## Özellikler

✅ Öncelik gösterimi (badge)
✅ Öncelik güncelleme (modal)
✅ Öncelik history kaydı
✅ Email template'lerde priority
✅ Responsive tasarım
✅ Renk kodlaması
✅ Türkçe label'lar
✅ Validation (kapalı çağrılarda güncelleme yapılamaz)

## Test Senaryoları

1. ✅ Yeni call request oluştur → Default MEDIUM olmalı
2. ✅ Önceliği HIGH'a güncelle → Badge turuncu olmalı
3. ✅ Önceliği URGENT'e güncelle → Badge kırmızı olmalı
4. ✅ History'de öncelik değişikliği görünmeli
5. ✅ Email'de doğru öncelik gösterilmeli
6. ✅ Kapalı çağrıda öncelik güncellenemez

## Notlar

- Default priority: MEDIUM
- Kapalı (CLOSED) çağrılarda öncelik güncellenemez
- Öncelik değişiklikleri history'de kaydedilir
- Email template'lerinde dinamik olarak gösterilir
- Frontend'de renk kodlaması ile görsel ayrım yapılır

## Gelecek Geliştirmeler

- [ ] Önceliğe göre filtreleme
- [ ] Önceliğe göre sıralama
- [ ] Otomatik öncelik ataması (VIP müşteriler için)
- [ ] Öncelik bazlı SLA takibi
- [ ] Dashboard'da öncelik istatistikleri
