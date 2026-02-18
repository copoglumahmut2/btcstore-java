# Call Request Çoklu Atama ve Kapatma Özelliği

## Yapılan Değişiklikler

### 1. Backend Değişiklikleri

#### Domain Katmanı
- **CallRequestStatus Enum**: `CLOSED` statüsü eklendi
- **StoreCallRequestModel**: 
  - `assignedGroups` (TEXT): Çoklu grup ataması için (semicolon separated)
  - `assignedUsers` (ManyToMany): Çoklu kullanıcı ataması için junction table
  - Eski `assignedGroup` ve `assignedUser` alanları backward compatibility için korundu

#### Service Katmanı
- **CallRequestService**:
  - `assignToGroups(Long callRequestId, List<String> groupCodes)`: Çoklu grup ataması
  - `assignToUsers(Long callRequestId, List<Long> userIds)`: Çoklu kullanıcı ataması
  - `closeCallRequest(Long callRequestId, String comment)`: Çağrıyı kapatma
  - Tüm atama işlemlerinde mail gönderimi aktif

#### Facade Katmanı
- **CallRequestFacade**: Service metodlarını expose ediyor

#### Controller Katmanı
- **CallRequestController**:
  - `POST /v1/call-requests/{id}/assign-groups`: Çoklu grup ataması
  - `POST /v1/call-requests/{id}/assign-users`: Çoklu kullanıcı ataması
  - `POST /v1/call-requests/{id}/close`: Çağrıyı kapatma

- **UserController**:
  - `GET /v1/users/search?query={query}`: Kullanıcı arama (autocomplete için)

### 2. Frontend Değişiklikleri

#### Types
- **CallRequestStatus**: `CLOSED` statüsü eklendi
- **CallRequest Interface**: 
  - `assignedGroups?: string`: Çoklu grup bilgisi
  - `assignedUserIds?: number[]`: Çoklu kullanıcı ID'leri
  - `assignedUserNames?: string[]`: Çoklu kullanıcı isimleri

#### Services
- **callRequestService**:
  - `assignToGroups(id, groupCodes)`: Çoklu grup ataması
  - `assignToUsers(id, userIds)`: Çoklu kullanıcı ataması
  - `closeRequest(id, comment)`: Çağrıyı kapatma

- **userService**:
  - `search(query)`: Kullanıcı arama

#### Components
- **CallRequestDetail**:
  - Çoklu grup seçimi (checkbox)
  - Kullanıcı arama ve seçimi (autocomplete)
  - Seçili kullanıcıları görüntüleme ve kaldırma
  - Çağrıyı kapatma butonu ve modal
  - Atanan grupları ve kullanıcıları görüntüleme

### 3. Veritabanı Değişiklikleri

```sql
-- Yeni kolon
ALTER TABLE call_request ADD COLUMN assigned_groups TEXT;

-- Junction table
CREATE TABLE call_request_assigned_users (
    call_request_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (call_request_id, user_id)
);
```

## Kullanım

### Çoklu Atama Yapma

1. Call request detay sayfasında "Atama Yap" butonuna tıklayın
2. **Gruplar**: İstediğiniz kadar grubu checkbox ile seçin
3. **Kullanıcılar**: 
   - Arama kutusuna kullanıcı adı, email veya isim yazın
   - Sonuçlardan kullanıcıyı seçin
   - İstediğiniz kadar kullanıcı ekleyin
   - X butonu ile kullanıcıları kaldırabilirsiniz
4. "Ata" butonuna tıklayın

### Çağrıyı Kapatma

1. Call request detay sayfasında "Çağrıyı Kapat" butonuna tıklayın
2. Opsiyonel olarak kapanış notu ekleyin
3. "Kapat" butonuna tıklayın
4. Çağrı `CLOSED` statüsüne geçer ve tarihçeye kaydedilir

## Mail Gönderimi

### Sorun: Grup atamasında mail gitmiyordu

**Çözüm**: 
- `assignToGroup` ve `assignToGroups` metodlarında `publishCallRequestEvent` çağrısı eklendi
- Interceptor sadece yeni kayıtlarda çalışıyordu, şimdi atama işlemlerinde de mail gönderiliyor
- Grup atamasında o gruptaki tüm kullanıcılara mail gönderiliyor

### Mail Gönderim Akışı

1. **Yeni Call Request**: Interceptor otomatik mail gönderir
2. **Grup Ataması**: Service katmanında `publishCallRequestEvent` çağrılır
3. **Kullanıcı Ataması**: Service katmanında `publishCallRequestEvent` çağrılır
4. **Çağrı Kapatma**: Service katmanında `publishCallRequestEvent` çağrılır

## Backward Compatibility

- Eski `assignedGroup` ve `assignedUser` alanları korundu
- Tek grup/kullanıcı ataması yapıldığında hem eski hem yeni alanlar güncelleniyor
- Mevcut API endpoint'ler çalışmaya devam ediyor
- Yeni endpoint'ler eklendi

## Test Senaryoları

### 1. Çoklu Grup Ataması
```bash
POST /v1/call-requests/1/assign-groups
Body: ["SALES", "SUPPORT", "TECHNICAL"]
```

### 2. Çoklu Kullanıcı Ataması
```bash
POST /v1/call-requests/1/assign-users
Body: [101, 102, 103]
```

### 3. Kullanıcı Arama
```bash
GET /v1/users/search?query=ahmet
```

### 4. Çağrıyı Kapatma
```bash
POST /v1/call-requests/1/close?comment=Müşteri%20memnun%20kaldı
```

## Notlar

- Kullanıcı araması minimum 2 karakter gerektirir
- Arama sonuçları 20 ile sınırlıdır (performans)
- Debounce 300ms (gereksiz API çağrılarını önler)
- Kapatılan çağrılar tekrar açılamaz (UI'da buton disabled)
- Tüm işlemler tarihçeye kaydedilir
- Mail gönderimi RabbitMQ üzerinden asenkron çalışır

## Veritabanı Migration

SQL script'i çalıştırın:
```bash
mysql -u root -p btcstore < CALL_REQUEST_MULTI_ASSIGN_UPDATE.sql
```

## Sonraki Adımlar

1. ✅ Çoklu grup ataması
2. ✅ Çoklu kullanıcı ataması
3. ✅ Kullanıcı arama (autocomplete)
4. ✅ Çağrıyı kapatma
5. ✅ Mail gönderimi düzeltmesi
6. 🔄 Test ve QA
7. 🔄 Production deployment
