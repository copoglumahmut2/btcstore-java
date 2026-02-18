# Call Request Yönetim Sistemi - Kurulum Rehberi

## 📋 Sistem Özeti

Bu sistem, müşterilerin call request formu doldurmasından başlayarak, ilgili user group'lara atama, mail gönderimi ve süreç takibini içeren tam bir workflow yönetim sistemidir.

## 🏗️ Mimari Yapı

### 1. **Domain Katmanı** (btcstore/domain)
- **Enum'lar:**
  - `CallRequestStatus`: PENDING, ASSIGNED, IN_PROGRESS, CUSTOMER_INFORMED, COMPLETED, CANCELLED
  - `CallRequestActionType`: CREATED, ASSIGNED_TO_GROUP, ASSIGNED_TO_USER, STATUS_CHANGED, EMAIL_SENT, etc.

- **Store Models** (Base Entity'ler):
  - `StoreCallRequestModel`: Ana call request entity
  - `StoreCallRequestHistoryModel`: Süreç tarihçesi
  - `StoreEmailTemplateModel`: Mail template'leri

- **Custom Models** (Extend edilen):
  - `CallRequestModel`
  - `CallRequestHistoryModel`
  - `EmailTemplateModel`

- **Data (DTO) Sınıfları:**
  - `CallRequestData`
  - `CallRequestHistoryData`
  - `EmailTemplateData`

### 2. **Persistence Katmanı** (btcstore/persistence)
- **DAO'lar:**
  - `CallRequestDao`: Call request CRUD işlemleri
  - `CallRequestHistoryDao`: Tarihçe sorguları
  - `EmailTemplateDao`: Template yönetimi

### 3. **Service Katmanı** (btcstore/service)
- **Service Interface'leri:**
  - `CallRequestService`: Ana iş mantığı
  - `CallRequestHistoryService`: Tarihçe yönetimi
  - `EmailTemplateService`: Template işlemleri

- **Service Implementation'ları:**
  - `CallRequestServiceImpl`: RabbitMQ event publishing dahil
  - `CallRequestHistoryServiceImpl`
  - `EmailTemplateServiceImpl`

### 4. **RabbitMQ Projesi** (btcstorerabbit)
- **Config:**
  - `RabbitMQConfig`: Queue, Exchange, Binding tanımları

- **DTO'lar:**
  - `CallRequestEventDto`: Event data transfer
  - `EmailRequestDto`: Mail gönderim data

- **Listener:**
  - `CallRequestListener`: Event'leri dinler ve mail gönderir

- **Service:**
  - `EmailService`: Mail gönderim servisi

## 🔄 İş Akışı (Workflow)

### Adım 1: Form Doldurma
```java
// Kullanıcı formu doldurur
CallRequestModel request = new CallRequestModel();
request.setCustomerName("Ahmet Yılmaz");
request.setCustomerEmail("ahmet@example.com");
request.setCustomerPhone("+905551234567");
request.setSubject("Ürün Bilgisi");
request.setMessage("X ürünü hakkında bilgi almak istiyorum");
request.setGdprConsent(true); // KVKK/GDPR onayı
request.setSite(siteModel);

callRequestService.createCallRequest(request);
```

### Adım 2: Parametre Okuma ve Gruba Atama
```java
// Parameter'dan user group'ları oku
// Parameter Code: "call.center.group"
// Parameter Value: "super_admin;sales_employee_group"

callRequestService.assignToGroup(requestId, "sales_employee_group");
```

### Adım 3: RabbitMQ Event Gönderimi
```java
// CallRequestServiceImpl otomatik olarak event gönderir
// Event RabbitMQ'ya düşer
publishCallRequestEvent(callRequest, "ASSIGNED_TO_GROUP");
```

### Adım 4: Mail Gönderimi
```java
// RabbitMQ Listener event'i yakalar
// İlgili gruptaki tüm kullanıcılara mail gönderir
// Mail template'i EmailTemplateModel'den alınır
```

### Adım 5: Yönetim Panelinde Takip
```java
// Tarihçe görüntüleme
List<CallRequestHistoryModel> history = 
    callRequestHistoryService.getHistoryByCallRequestId(requestId);

// Her adım kaydedilir:
// - Oluşturuldu
// - Gruba atandı
// - Mail gönderildi
// - Kullanıcıya atandı
// - Durum değişti
// - Tamamlandı
```

### Adım 6: Durum Güncelleme
```java
// İlgili kullanıcı durumu günceller
callRequestService.updateStatus(
    requestId, 
    CallRequestStatus.CUSTOMER_INFORMED,
    "Müşteri ile görüşüldü, bilgilendirme yapıldı"
);
```

## 🔧 Gerekli Parametreler

### Database'e Eklenecek Parametreler:

```sql
-- Call Center Group Tanımı
INSERT INTO parameters (code, value, site_id, parameter_type, created_date) 
VALUES ('call.center.group', 'super_admin;sales_employee_group', 1, 'STRING', NOW());

-- Mail SMTP Ayarları (RabbitMQ application.yml'de)
-- mail.smtp.host
-- mail.smtp.port
-- mail.smtp.username
-- mail.smtp.password
```

### Email Template Örneği:

```sql
INSERT INTO email_templates (code, template_name, subject, body, is_active, site_id, created_date)
VALUES (
    'call_request_notification',
    'Call Request Bildirimi',
    'Yeni Call Request: {{subject}}',
    '<html>
        <body>
            <h2>Sayın {{customerName}},</h2>
            <p>Bir müşteri bilgilendirilmek istiyor.</p>
            <p><strong>Email:</strong> {{customerEmail}}</p>
            <p><strong>Telefon:</strong> {{customerPhone}}</p>
            <p><strong>Konu:</strong> {{subject}}</p>
            <p><strong>Mesaj:</strong> {{message}}</p>
            <p><strong>Atanan Grup:</strong> {{assignedGroup}}</p>
        </body>
    </html>',
    true,
    1,
    NOW()
);
```

## 🚀 Kurulum Adımları

### 1. Database Migration
```sql
-- Tabloları oluştur (JPA otomatik oluşturacak)
-- Veya Liquibase/Flyway kullan
```

### 2. RabbitMQ Kurulumu
```bash
# Docker ile RabbitMQ başlat
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# Management Console: http://localhost:15672
# Username: guest
# Password: guest
```

### 3. RabbitMQ Projesi Konfigürasyonu
```yaml
# btcstorerabbit/src/main/resources/application.yml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
```

### 4. Main Projede RabbitMQ Dependency Ekle
```xml
<!-- btcstore/service/pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

### 5. Projeleri Başlat
```bash
# 1. Main btcstore projesini başlat
cd btcstore
mvn clean install
mvn spring-boot:run

# 2. RabbitMQ projesini başlat
cd btcstorerabbit
mvn clean install
mvn spring-boot:run
```

## 📊 Yönetim Paneli Özellikleri

### 1. Call Request Listesi
- Tüm request'leri görüntüleme
- Statüye göre filtreleme
- Gruba göre filtreleme
- Kullanıcıya göre filtreleme

### 2. Call Request Detay
- Müşteri bilgileri
- Request içeriği
- Atama bilgileri
- Durum bilgisi
- Tarihçe (Timeline)

### 3. Tarihçe Görünümü
```
[2024-02-18 10:00] System: Call request oluşturuldu
[2024-02-18 10:01] System: Gruba atandı: sales_employee_group
[2024-02-18 10:01] System: Email gönderildi: 3 alıcı
[2024-02-18 10:15] Ahmet Y.: Kullanıcıya atandı
[2024-02-18 10:30] Ahmet Y.: Durum değişti: IN_PROGRESS -> CUSTOMER_INFORMED
                            Yorum: "Müşteri ile görüşüldü"
[2024-02-18 11:00] Ahmet Y.: Tamamlandı
```

### 4. Email Template Yönetimi
- Template listesi
- Template oluşturma/düzenleme
- Template önizleme
- Aktif/Pasif durumu

## 🔐 GDPR/KVKK Uyumluluğu

### Veri Koruma Özellikleri:
1. **Açık Rıza:** `gdprConsent` alanı ile kullanıcı onayı
2. **IP Kaydı:** `ipAddress` alanı ile işlem kaydı
3. **Veri Minimizasyonu:** Sadece gerekli bilgiler toplanır
4. **Şeffaflık:** Tüm işlemler tarihçede kayıtlı
5. **Silme Hakkı:** Kullanıcı verilerini silme özelliği eklenebilir

## 📝 Sonraki Adımlar

### Facade Katmanı (Devam Edecek)
- `CallRequestFacade`: Controller ile Service arası köprü
- `EmailTemplateFacade`: Template yönetimi facade
- Data <-> Model converter'lar

### Controller Katmanı (Devam Edecek)
- REST API endpoint'leri
- Admin panel endpoint'leri
- Public form endpoint'i

### Frontend (Next.js)
- Call request formu
- Admin yönetim paneli
- Tarihçe görünümü
- Email template editörü

## 🎯 Önemli Notlar

1. **RabbitMQ Bağlantısı:** Main proje ve RabbitMQ projesi aynı RabbitMQ instance'ına bağlanmalı
2. **Transaction Yönetimi:** Service katmanında `@Transactional` kullanıldı
3. **Error Handling:** RabbitMQ listener'da try-catch ile hata yönetimi var
4. **Logging:** Tüm önemli işlemler loglanıyor
5. **Async İşlemler:** RabbitMQ sayesinde mail gönderimi async

## 🔍 Test Senaryosu

```java
// 1. Call Request Oluştur
CallRequestModel request = callRequestService.createCallRequest(newRequest);
// ✓ DB'ye kaydedildi
// ✓ History oluşturuldu
// ✓ RabbitMQ'ya event gönderildi

// 2. RabbitMQ Event İşlendi
// ✓ Listener event'i yakaladı
// ✓ User group'tan email'ler alındı
// ✓ Her kullanıcıya mail gönderildi

// 3. Durum Güncelleme
callRequestService.updateStatus(request.getId(), CUSTOMER_INFORMED, "Bilgilendirme yapıldı");
// ✓ Durum güncellendi
// ✓ History'ye eklendi

// 4. Tarihçe Görüntüleme
List<CallRequestHistoryModel> history = callRequestHistoryService.getHistoryByCallRequestId(request.getId());
// ✓ Tüm adımlar görüntülendi
```

---

**Hazırlayan:** Kiro AI Assistant  
**Tarih:** 2024-02-18  
**Versiyon:** 1.0
