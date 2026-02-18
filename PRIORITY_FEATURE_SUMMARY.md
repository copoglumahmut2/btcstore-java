# Call Request Priority Özelliği - Özet

## Backend Değişiklikleri ✅

### 1. Enum Oluşturuldu
- `CallRequestPriority.java` - LOW, MEDIUM, HIGH, URGENT

### 2. Model Güncellendi
- `StoreCallRequestModel.java` - priority field eklendi (default: MEDIUM)
- `StoreCallRequestData.java` - priority field eklendi

### 3. Service Katmanı
- `CallRequestService.java` - `updatePriority()` metodu eklendi
- `CallRequestServiceImpl.java` - Implementation eklendi
- Email template'lerinde priority bilgisi dinamik olarak ekleniyor

### 4. Facade Katmanı
- `CallRequestFacade.java` - `updatePriority()` metodu eklendi
- `CallRequestFacadeImpl.java` - Implementation eklendi

### 5. Controller
- `CallRequestController.java` - `POST /api/v1/call-requests/{id}/update-priority` endpoint eklendi

### 6. Database Migration
- `CALL_REQUEST_ADD_PRIORITY.sql` - Priority kolonu ekleyen script

## Frontend Değişiklikleri ✅

### 1. TypeScript Types
- `CallRequestPriority` enum eklendi
- `PRIORITY_LABELS` ve `PRIORITY_COLORS` constant'ları eklendi
- `CallRequest` interface'ine `priority` field'ı eklendi

### 2. Yapılması Gerekenler (Devam Edecek)

#### CallRequestDetail.tsx
- Priority gösterimi eklenecek
- Priority güncelleme modal'ı eklenecek

#### CallRequestsAdmin.tsx
- Liste'de priority badge'i gösterilecek
- Priority'ye göre filtreleme eklenecek

#### MyCallRequests.tsx
- Priority badge'i gösterilecek

#### Service
- `callRequestService.updatePriority()` metodu eklenecek

## Kullanım

### Backend API
```bash
# Priority güncelleme
POST /api/v1/call-requests/123/update-priority?priority=HIGH
```

### Priority Değerleri
- `LOW` - Düşük (Gri)
- `MEDIUM` - Orta (Mavi) - Default
- `HIGH` - Yüksek (Turuncu)
- `URGENT` - Acil (Kırmızı)

## Database Migration

```sql
-- Çalıştırılması gereken script
psql -U user -d db -f CALL_REQUEST_ADD_PRIORITY.sql
```

## Sonraki Adımlar

1. SQL script'ini çalıştır
2. Backend'i derle ve başlat
3. Frontend component'lerini güncelle (devam edecek)
4. Test et
