# Call Request Sistemi - Test Rehberi

## 🚀 Hızlı Başlangıç

### 1. Database Setup

```sql
-- 1. Parametreleri ekle
INSERT INTO parameters (code, value, site_id, parameter_type, data_type, is_active, created_date, created_by) 
VALUES 
('call.center.group', 'super_admin;sales_employee_group', 1, 'STRING', 'STRING', true, NOW(), 'system');

-- 2. Email Template ekle
INSERT INTO email_templates (
    code, 
    template_name, 
    subject, 
    body, 
    description,
    is_active, 
    site_id, 
    created_date,
    created_by
)
VALUES (
    'call_request_notification',
    'Call Request Bildirimi',
    'Yeni Call Request: {{subject}}',
    '<html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                .content { background-color: #f9f9f9; padding: 20px; border: 1px solid #ddd; }
                .info-row { margin: 10px 0; padding: 10px; background-color: white; border-left: 4px solid #4CAF50; }
                .label { font-weight: bold; color: #4CAF50; }
                .footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h2>🔔 Yeni Call Request Bildirimi</h2>
                </div>
                <div class="content">
                    <p>Merhaba,</p>
                    <p>Bir müşteri sizinle iletişime geçmek istiyor.</p>
                    
                    <div class="info-row">
                        <span class="label">👤 Müşteri Adı:</span> {{customerName}}
                    </div>
                    <div class="info-row">
                        <span class="label">📧 Email:</span> {{customerEmail}}
                    </div>
                    <div class="info-row">
                        <span class="label">📱 Telefon:</span> {{customerPhone}}
                    </div>
                    <div class="info-row">
                        <span class="label">📋 Konu:</span> {{subject}}
                    </div>
                    <div class="info-row">
                        <span class="label">💬 Mesaj:</span><br>{{message}}
                    </div>
                    <div class="info-row">
                        <span class="label">👥 Atanan Grup:</span> {{assignedGroup}}
                    </div>
                    
                    <p style="margin-top: 20px;">
                        Lütfen en kısa sürede müşteri ile iletişime geçiniz.
                    </p>
                </div>
                <div class="footer">
                    <p>Bu otomatik bir bildirimdir. Lütfen yanıtlamayınız.</p>
                    <p>&copy; 2024 BTC Store. Tüm hakları saklıdır.</p>
                </div>
            </div>
        </body>
    </html>',
    'Call request bildirimi için kullanılan email template',
    true,
    1,
    NOW(),
    'system'
);
```

### 2. RabbitMQ Konfigürasyonu

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
    password: your-app-password  # Gmail için App Password kullanın
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### 3. Main Proje Konfigürasyonu

```yaml
# btcstore/webapp/src/main/resources/application.yml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

## 📝 API Test Senaryoları

### Senaryo 1: Public Form Üzerinden Call Request Oluşturma

```bash
# Public endpoint - Authentication gerekmez
curl -X POST http://localhost:8080/v1/public/call-requests \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Ahmet Yılmaz",
    "customerEmail": "ahmet@example.com",
    "customerPhone": "+905551234567",
    "subject": "Ürün Bilgisi",
    "message": "X ürünü hakkında detaylı bilgi almak istiyorum.",
    "gdprConsent": true
  }'
```

**Beklenen Sonuç:**
```json
{
  "status": "SUCCESS",
  "message": "Talebiniz başarıyla alındı. En kısa sürede size dönüş yapılacaktır.",
  "data": {
    "id": 1,
    "customerName": "Ahmet Yılmaz",
    "customerEmail": "ahmet@example.com",
    "customerPhone": "+905551234567",
    "subject": "Ürün Bilgisi",
    "message": "X ürünü hakkında detaylı bilgi almak istiyorum.",
    "status": "PENDING",
    "gdprConsent": true,
    "ipAddress": "127.0.0.1"
  }
}
```

**Arka Planda Olan İşlemler:**
1. ✅ Call request DB'ye kaydedildi
2. ✅ History kaydı oluşturuldu (CREATED)
3. ✅ RabbitMQ'ya event gönderildi
4. ✅ Parameter'dan user group'lar okundu (super_admin;sales_employee_group)
5. ✅ İlgili gruplardaki kullanıcılara mail gönderildi
6. ✅ History'ye EMAIL_SENT kaydı eklendi

### Senaryo 2: Tüm Call Request'leri Listeleme

```bash
# Admin endpoint - Authentication gerekli
curl -X GET http://localhost:8080/v1/call-requests \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Senaryo 3: Call Request Detayı Görüntüleme

```bash
curl -X GET http://localhost:8080/v1/call-requests/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Senaryo 4: Gruba Atama

```bash
curl -X POST "http://localhost:8080/v1/call-requests/1/assign-group?groupCode=sales_employee_group" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Arka Planda Olan İşlemler:**
1. ✅ Call request durumu ASSIGNED olarak güncellendi
2. ✅ assignedGroup alanı set edildi
3. ✅ History kaydı oluşturuldu (ASSIGNED_TO_GROUP)
4. ✅ RabbitMQ'ya event gönderildi
5. ✅ Gruba mail gönderildi

### Senaryo 5: Kullanıcıya Atama

```bash
curl -X POST "http://localhost:8080/v1/call-requests/1/assign-user?userId=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Arka Planda Olan İşlemler:**
1. ✅ Call request durumu IN_PROGRESS olarak güncellendi
2. ✅ assignedUser alanı set edildi
3. ✅ History kaydı oluşturuldu (ASSIGNED_TO_USER)

### Senaryo 6: Durum Güncelleme

```bash
curl -X POST "http://localhost:8080/v1/call-requests/1/update-status?status=CUSTOMER_INFORMED&comment=Müşteri%20ile%20görüşüldü" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Arka Planda Olan İşlemler:**
1. ✅ Call request durumu CUSTOMER_INFORMED olarak güncellendi
2. ✅ History kaydı oluşturuldu (STATUS_CHANGED)
3. ✅ Comment kaydedildi

### Senaryo 7: Tarihçe Görüntüleme

```bash
curl -X GET http://localhost:8080/v1/call-requests/1/history \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Beklenen Sonuç:**
```json
{
  "status": "SUCCESS",
  "data": [
    {
      "id": 5,
      "callRequestId": 1,
      "actionType": "STATUS_CHANGED",
      "description": "Durum değişti: IN_PROGRESS -> CUSTOMER_INFORMED",
      "performedByUsername": "System",
      "oldStatus": "IN_PROGRESS",
      "newStatus": "CUSTOMER_INFORMED",
      "comment": "Müşteri ile görüşüldü",
      "createdDate": "2024-02-18T11:30:00"
    },
    {
      "id": 4,
      "callRequestId": 1,
      "actionType": "ASSIGNED_TO_USER",
      "description": "Kullanıcıya atandı: ahmet.y",
      "performedByUserId": 5,
      "performedByUsername": "ahmet.y",
      "oldStatus": "ASSIGNED",
      "newStatus": "IN_PROGRESS",
      "createdDate": "2024-02-18T10:15:00"
    },
    {
      "id": 3,
      "callRequestId": 1,
      "actionType": "EMAIL_SENT",
      "description": "Email gönderildi: 3 alıcı",
      "performedByUsername": "System",
      "createdDate": "2024-02-18T10:01:00"
    },
    {
      "id": 2,
      "callRequestId": 1,
      "actionType": "ASSIGNED_TO_GROUP",
      "description": "Gruba atandı: sales_employee_group",
      "performedByUsername": "System",
      "oldStatus": "PENDING",
      "newStatus": "ASSIGNED",
      "createdDate": "2024-02-18T10:01:00"
    },
    {
      "id": 1,
      "callRequestId": 1,
      "actionType": "CREATED",
      "description": "Call request oluşturuldu",
      "performedByUsername": "System",
      "newStatus": "PENDING",
      "createdDate": "2024-02-18T10:00:00"
    }
  ]
}
```

### Senaryo 8: Benim Atanmış İşlerim

```bash
curl -X GET http://localhost:8080/v1/call-requests/my-requests \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Senaryo 9: Email Template Yönetimi

```bash
# Template listesi
curl -X GET http://localhost:8080/v1/email-templates \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Template detayı
curl -X GET http://localhost:8080/v1/email-templates/call_request_notification \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Template güncelleme
curl -X POST http://localhost:8080/v1/email-templates \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "call_request_notification",
    "templateName": "Call Request Bildirimi (Güncel)",
    "subject": "🔔 Yeni Call Request: {{subject}}",
    "body": "<html>...</html>",
    "isActive": true
  }'
```

## 🧪 Postman Collection

```json
{
  "info": {
    "name": "Call Request API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Public - Create Call Request",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"customerName\": \"Ahmet Yılmaz\",\n  \"customerEmail\": \"ahmet@example.com\",\n  \"customerPhone\": \"+905551234567\",\n  \"subject\": \"Ürün Bilgisi\",\n  \"message\": \"X ürünü hakkında bilgi almak istiyorum.\",\n  \"gdprConsent\": true\n}"
        },
        "url": {
          "raw": "http://localhost:8080/v1/public/call-requests",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["v1", "public", "call-requests"]
        }
      }
    },
    {
      "name": "Get All Call Requests",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "http://localhost:8080/v1/call-requests",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["v1", "call-requests"]
        }
      }
    },
    {
      "name": "Assign to Group",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "http://localhost:8080/v1/call-requests/1/assign-group?groupCode=sales_employee_group",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["v1", "call-requests", "1", "assign-group"],
          "query": [
            {
              "key": "groupCode",
              "value": "sales_employee_group"
            }
          ]
        }
      }
    }
  ]
}
```

## 🔍 Debugging

### RabbitMQ Management Console
```
URL: http://localhost:15672
Username: guest
Password: guest

Kontrol Edilecekler:
- Queues: call.request.queue, email.queue
- Exchanges: call.request.exchange, email.exchange
- Bindings: Routing key'ler doğru mu?
- Messages: Queue'da bekleyen mesaj var mı?
```

### Log Kontrolleri

```bash
# Main proje logları
tail -f btcstore/logs/application.log | grep "CallRequest"

# RabbitMQ proje logları
tail -f btcstorerabbit/logs/application.log | grep "Email"
```

### Database Kontrolleri

```sql
-- Call requests
SELECT * FROM call_requests ORDER BY created_date DESC;

-- History
SELECT * FROM call_request_histories WHERE call_request_id = 1 ORDER BY created_date DESC;

-- Email templates
SELECT * FROM email_templates WHERE is_active = true;

-- Parameters
SELECT * FROM parameters WHERE code = 'call.center.group';
```

## ⚠️ Yaygın Hatalar ve Çözümleri

### 1. Mail Gönderilmiyor
```
Hata: Authentication failed
Çözüm: Gmail için App Password kullanın
- Google Account > Security > 2-Step Verification > App Passwords
```

### 2. RabbitMQ Bağlantı Hatası
```
Hata: Connection refused
Çözüm: RabbitMQ çalışıyor mu kontrol edin
docker ps | grep rabbitmq
```

### 3. User Group Bulunamıyor
```
Hata: No users found in group
Çözüm: Parameter değerini kontrol edin
SELECT * FROM parameters WHERE code = 'call.center.group';
```

### 4. GDPR Consent Hatası
```
Hata: GDPR/KVKK onayı gereklidir
Çözüm: Request body'de gdprConsent: true olmalı
```

## 📊 Performans Metrikleri

```sql
-- Toplam call request sayısı
SELECT COUNT(*) FROM call_requests;

-- Statüye göre dağılım
SELECT status, COUNT(*) FROM call_requests GROUP BY status;

-- Ortalama yanıt süresi
SELECT AVG(TIMESTAMPDIFF(HOUR, created_date, completed_at)) as avg_hours
FROM call_requests 
WHERE status = 'COMPLETED';

-- En çok iş alan kullanıcılar
SELECT assigned_user_id, COUNT(*) as total
FROM call_requests 
WHERE assigned_user_id IS NOT NULL
GROUP BY assigned_user_id
ORDER BY total DESC;
```

---

**Test Başarı Kriterleri:**
- ✅ Public endpoint'ten form gönderilebiliyor
- ✅ RabbitMQ'ya event düşüyor
- ✅ Mail gönderimi çalışıyor
- ✅ History kaydı tutuluyor
- ✅ Durum güncellemeleri yapılabiliyor
- ✅ Admin panelinden yönetilebiliyor

**Hazırlayan:** Kiro AI Assistant  
**Tarih:** 2024-02-18
