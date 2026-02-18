# Email Template Sistemi - Hızlı Başlangıç

## Nasıl Çalışır?

Email template sistemi 3 bileşenden oluşur:

1. **Veritabanı Template'leri** - HTML email template'leri `email_templates` tablosunda
2. **GenericTemplateService** - Template'leri işler, değişkenleri değerlerle değiştirir
3. **CallRequestServiceImpl** - Email gönderim mantığı

## Değişken Sistemi

### Otomatik Değişkenler (Model'den)
GenericTemplateService, reflection kullanarak model'deki tüm field'ları otomatik çıkarır:

```java
Map<String, Object> variables = genericTemplateService.extractVariables(callRequestModel, "CallRequestModel");
```

Bu şunları otomatik ekler:
- `id`, `customerName`, `customerEmail`, `customerPhone`
- `subject`, `message`, `status`
- `createdDate`, `modifiedDate` (parent class'tan)

### Manuel Değişkenler (Kod'dan)
Model'de olmayan değişkenler manuel eklenir:

```java
// Grup/kullanıcı bilgisi
variables.put("groupName", groupCode);
variables.put("assignedUserName", user.getUsername());
variables.put("assignedBy", currentUser.getUsername());

// Öncelik (TODO: Model'e eklenecek)
variables.put("priority", "MEDIUM");
variables.put("priorityClass", "medium");

// Formatlanmış tarih
variables.put("createdDate", new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date));

// URL
variables.put("callRequestUrl", baseUrl + "/admin/call-requests/" + id);
```

## Yeni Template Ekleme

### 1. HTML Template Oluştur
```html
<!DOCTYPE html>
<html>
<body>
    <h1>Merhaba {{customerName}}</h1>
    <p>Talebiniz: {{subject}}</p>
</body>
</html>
```

### 2. Veritabanına Ekle
```sql
INSERT INTO email_templates (
    code, template_name, subject, body, active, site_id
) VALUES (
    'my_template',
    'Benim Template',
    'Konu: {{subject}}',
    '<html>...</html>',
    true,
    (SELECT id FROM sites WHERE code = 'btcstore')
);
```

### 3. Kod'da Kullan
```java
// Template'i al
var template = emailTemplateService.getEmailTemplateByCode("my_template", site);

// Değişkenleri hazırla
Map<String, Object> variables = genericTemplateService.extractVariables(model, "ModelType");
variables.put("customVar", "değer");

// İşle
String subject = genericTemplateService.processTemplate(template.getSubject(), variables);
String body = genericTemplateService.processTemplate(template.getBody(), variables);

// Gönder
rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, emailEvent);
```

## Mevcut Template'ler

| Code | Kullanım | Gönderim Zamanı |
|------|----------|-----------------|
| `call_request_notification` | Genel bildirim | Talep tamamlandığında |
| `call_request_assigned_to_group` | Gruba atama | Gruba atandığında |
| `call_request_assigned_to_user` | Kullanıcıya atama | Kullanıcıya atandığında |

## Kurulum

```bash
# SQL script'i çalıştır
psql -U user -d db -f CALL_REQUEST_EMAIL_TEMPLATES.sql

# Frontend URL'ini ayarla
UPDATE parameters SET value = 'https://your-domain.com' WHERE code = 'frontend.base.url';
```

## Sorun Giderme

**Template bulunamıyor:**
```sql
SELECT * FROM email_templates WHERE code = 'template_code';
```

**Değişken görünmüyor:**
- Model'de field var mı? → Otomatik gelir
- Model'de field yok mu? → Manuel ekle: `variables.put("key", value)`

**Email gitmiyor:**
- RabbitMQ çalışıyor mu?
- Email servisi çalışıyor mu?
- Kullanıcı email'i var mı?

## Detaylı Dokümantasyon

- [CALL_REQUEST_EMAIL_TEMPLATES_GUIDE.md](./CALL_REQUEST_EMAIL_TEMPLATES_GUIDE.md) - Detaylı kullanım kılavuzu
- [GENERIC_TEMPLATE_SYSTEM_GUIDE.md](./GENERIC_TEMPLATE_SYSTEM_GUIDE.md) - Template sistemi mimarisi
