# Görüşme Talebi Email Template'leri Kullanım Kılavuzu

## Genel Bakış

Görüşme talebi sistemi için 3 farklı email template'i bulunmaktadır:

1. **call_request_notification** - Genel bildirimler için
2. **call_request_assigned_to_group** - Gruba atama bildirimleri için
3. **call_request_assigned_to_user** - Kullanıcıya atama bildirimleri için

## Template'lerin Özellikleri

### 1. Gruba Atama Template'i (call_request_assigned_to_group)

**Kullanım Amacı:** Bir görüşme talebi bir gruba atandığında, o gruptaki tüm kullanıcılara gönderilir.

**Özellikler:**
- 🎨 Yeşil tonlarda gradient tasarım
- 👥 Grup odaklı mesajlaşma
- ⚠️ Ekip içi atama yapılması gerektiğini vurgular
- 📋 Detaylı talep bilgileri
- 🎯 Grup badge'i ile görsel vurgu

**Değişkenler:**
```
{{id}} - Talep numarası
{{groupName}} - Atanan grup adı (manuel eklenir)
{{customerName}} - Müşteri adı
{{customerPhone}} - Müşteri telefonu
{{customerEmail}} - Müşteri e-postası
{{priority}} - Öncelik seviyesi (şu an sabit: MEDIUM, gelecekte model'e eklenecek)
{{priorityClass}} - CSS class için öncelik (high, medium, low)
{{subject}} - Talep konusu
{{message}} - Talep mesajı
{{createdDate}} - Oluşturma tarihi (formatlanmış: dd.MM.yyyy HH:mm)
{{assignedBy}} - Atamayı yapan kişi (manuel eklenir)
{{callRequestUrl}} - Talep detay sayfası URL'i (frontend.base.url parametresinden)
```

**Not:** `groupName`, `assignedBy`, `priority`, `priorityClass`, `createdDate` ve `callRequestUrl` değişkenleri kod tarafında manuel olarak eklenir.

**Gönderim Zamanı:**
- Yeni talep oluşturulduğunda otomatik atama yapıldıysa
- Manuel olarak gruba atama yapıldığında
- Çoklu grup ataması yapıldığında (her grup için ayrı mail)

### 2. Kullanıcıya Atama Template'i (call_request_assigned_to_user)

**Kullanım Amacı:** Bir görüşme talebi belirli bir kullanıcıya atandığında, o kullanıcıya gönderilir.

**Özellikler:**
- 🎨 Mor tonlarda gradient tasarım
- 👤 Kişisel mesajlaşma
- ✅ Yapılması gereken aksiyonları listeler
- 📋 Detaylı talep bilgileri
- 🎯 Direkt aksiyon odaklı

**Değişkenler:**
```
{{id}} - Talep numarası
{{assignedUserName}} - Atanan kullanıcı adı (manuel eklenir)
{{customerName}} - Müşteri adı
{{customerPhone}} - Müşteri telefonu
{{customerEmail}} - Müşteri e-postası
{{priority}} - Öncelik seviyesi (şu an sabit: MEDIUM, gelecekte model'e eklenecek)
{{priorityClass}} - CSS class için öncelik (high, medium, low)
{{subject}} - Talep konusu
{{message}} - Talep mesajı
{{createdDate}} - Oluşturma tarihi (formatlanmış: dd.MM.yyyy HH:mm)
{{assignedBy}} - Atamayı yapan kişi (manuel eklenir)
{{callRequestUrl}} - Talep detay sayfası URL'i (frontend.base.url parametresinden)
```

**Not:** `assignedUserName`, `assignedBy`, `priority`, `priorityClass`, `createdDate` ve `callRequestUrl` değişkenleri kod tarafında manuel olarak eklenir.

**Gönderim Zamanı:**
- Manuel olarak kullanıcıya atama yapıldığında
- Çoklu kullanıcı ataması yapıldığında (her kullanıcı için ayrı mail)

### 3. Genel Bildirim Template'i (call_request_notification)

**Kullanım Amacı:** Genel bildirimler ve diğer durum değişiklikleri için.

**Gönderim Zamanı:**
- Talep tamamlandığında
- Özel bildirim gerektiren durumlarda

## Kurulum

### 1. SQL Script'i Çalıştırma

```sql
-- CALL_REQUEST_EMAIL_TEMPLATES.sql dosyasını çalıştırın
psql -U your_user -d your_database -f CALL_REQUEST_EMAIL_TEMPLATES.sql
```

Bu script:
- İki yeni email template'i ekler
- Mevcut template'i günceller
- Tüm template'leri aktif hale getirir
- `frontend.base.url` parametresini ekler (email'lerdeki link için)

### 2. Frontend Base URL Parametresi

Email'lerdeki "Görüşmeyi Görüntüle" butonu için frontend URL'ini ayarlayın:

```sql
UPDATE parameters 
SET value = 'https://your-domain.com'
WHERE code = 'frontend.base.url';
```

### 3. Template Doğrulama

Template'lerin doğru yüklendiğini kontrol edin:

```sql
SELECT code, template_name, active 
FROM email_templates 
WHERE code LIKE 'call_request%';
```

Beklenen sonuç:
```
code                              | template_name                    | active
----------------------------------|----------------------------------|-------
call_request_notification         | Genel Görüşme Bildirimi         | true
call_request_assigned_to_group    | Görüşme Gruba Atandı            | true
call_request_assigned_to_user     | Görüşme Kullanıcıya Atandı      | true
```

## Kod Değişiklikleri

### CallRequestServiceImpl Güncellemeleri

1. **publishCallRequestEventToGroup** metodu:
   - Artık `call_request_assigned_to_group` template'ini kullanır
   - `groupName` ve `assignedBy` değişkenlerini ekler
   - Daha açıklayıcı log mesajları

2. **publishCallRequestEventToUser** metodu:
   - Artık `call_request_assigned_to_user` template'ini kullanır
   - `assignedUserName` ve `assignedBy` değişkenlerini ekler
   - Daha açıklayıcı log mesajları

## Template Özelleştirme

### Değişken Ekleme

Yeni bir değişken eklemek için:

1. **Java tarafında (CallRequestServiceImpl):**
```java
// Extract variables from model
Map<String, Object> variables = genericTemplateService.extractVariables(callRequestModel, "CallRequestModel");

// Add custom variables
variables.put("yeniDegisken", deger);
variables.put("priority", "HIGH");
variables.put("priorityClass", "high");
```

2. **Template'de kullanım:**
```html
<div>{{yeniDegisken}}</div>
<div class="priority-{{priorityClass}}">{{priority}}</div>
```

**Önemli:** Model'de olmayan değişkenler (örn: `assignedBy`, `groupName`, `priority`) manuel olarak eklenmeli.

### Stil Değişiklikleri

Template'lerin stil özellikleri `<style>` tag'i içinde tanımlıdır:

- **Gruba atama:** Yeşil gradient (#11998e → #38ef7d)
- **Kullanıcıya atama:** Mor gradient (#667eea → #764ba2)

Renkleri değiştirmek için gradient değerlerini güncelleyin.

### Öncelik Renkleri

```css
.priority-high { color: #d32f2f; }    /* Kırmızı */
.priority-medium { color: #f57c00; }  /* Turuncu */
.priority-low { color: #388e3c; }     /* Yeşil */
```

## Test Etme

### 1. Gruba Atama Testi

```bash
curl -X POST http://localhost:8080/api/v1/call-requests/{id}/assign-to-group \
  -H "Content-Type: application/json" \
  -d '{"groupCode": "SALES_TEAM"}'
```

### 2. Kullanıcıya Atama Testi

```bash
curl -X POST http://localhost:8080/api/v1/call-requests/{id}/assign-to-user \
  -H "Content-Type: application/json" \
  -d '{"userId": 123}'
```

### 3. Email Kontrolü

- RabbitMQ Management Console'dan email queue'sunu kontrol edin
- Email servisinin loglarını inceleyin
- Test email hesabınızı kontrol edin

## Sorun Giderme

### Template Bulunamıyor Hatası

```
EmailTemplateModel not found with code: call_request_assigned_to_group
```

**Çözüm:** SQL script'ini çalıştırın ve template'lerin eklendiğini doğrulayın.

### Email Gönderilmiyor

1. RabbitMQ bağlantısını kontrol edin
2. Email servisinin çalıştığını doğrulayın
3. Kullanıcı/grup email adreslerini kontrol edin
4. Log dosyalarını inceleyin

### Değişkenler Görünmüyor

Template'de kullanılan değişkenlerin Java kodunda tanımlandığından emin olun:

```java
Map<String, Object> variables = genericTemplateService.extractVariables(callRequestModel, "CallRequestModel");
variables.put("groupName", groupCode);
variables.put("assignedBy", currentUser.getUsername());
```

## En İyi Uygulamalar

1. **Template Yedekleme:** Template'leri değiştirmeden önce yedek alın
2. **Test Ortamı:** Önce test ortamında deneyin
3. **Değişken Kontrolü:** Tüm değişkenlerin dolu olduğundan emin olun
4. **Responsive Tasarım:** Email'ler mobil cihazlarda da düzgün görünmelidir
5. **Spam Kontrolü:** Email içeriğinin spam filtrelerine takılmamasına dikkat edin

## Gelecek Geliştirmeler

- [ ] Email template'leri için admin paneli
- [ ] Template önizleme özelliği
- [ ] Çoklu dil desteği
- [ ] Email gönderim istatistikleri
- [ ] Template versiyonlama sistemi

## İletişim

Sorularınız için:
- Backend Team Lead
- DevOps Team
