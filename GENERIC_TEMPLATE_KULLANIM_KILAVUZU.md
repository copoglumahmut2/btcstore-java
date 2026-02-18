# Generic Template Sistemi - Kullanım Kılavuzu

## Özet

Email template sistemi artık tamamen generic. CallRequest, Order, User, Product veya herhangi bir entity için template oluşturabilirsiniz. Sistem reflection kullanarak otomatik olarak tüm alanları çıkarır.

## Nasıl Kullanılır?

### 1. Yeni Template Oluşturma

1. Admin panelinde **Email Templates** sayfasına gidin
2. **Yeni Template** butonuna tıklayın
3. **Entity Tipi** seçin (CallRequest, Order, User, Product)
4. Seçtiğiniz entity tipine göre kullanılabilir değişkenler otomatik güncellenir
5. Template kodunu girin (örn: `order_confirmation`)
6. Template adını girin
7. Mail konusunu girin (değişken kullanabilirsiniz: `{{orderNumber}}`)
8. Mail içeriğini yazın (HTML destekler)
9. Sağ panelden değişkenlere tıklayarak ekleyin
10. **Önizleme** ile kontrol edin
11. **Kaydet**

### 2. Değişken Kullanımı

Template içinde `{{değişkenAdı}}` formatında değişken kullanın:

```html
<h2>Merhaba {{customerName}},</h2>
<p>{{orderNumber}} numaralı siparişiniz onaylandı.</p>
<p><strong>Toplam:</strong> {{totalAmount}}</p>
<p><strong>Tarih:</strong> {{orderDate}}</p>
```

### 3. Nested (İç İçe) Değişkenler

Nested objelere nokta ile erişin:

```html
{{user.firstName}} {{user.lastName}}
{{order.customer.email}}
```

### 4. Otomatik Formatlar

- **Tarih**: `dd.MM.yyyy HH:mm` formatında
- **Boolean**: "Evet" / "Hayır" olarak
- **Sayı**: Olduğu gibi

## Yeni Entity Tipi Ekleme

### Adım 1: Frontend Tanımı

`btc-store/src/types/templateVariable.ts` dosyasını açın ve yeni entity ekleyin:

```typescript
export const ENTITY_TYPE_VARIABLES: Record<string, EntityTypeVariables> = {
  // ... mevcut tipler
  
  Invoice: {
    entityType: 'Invoice',
    entityLabel: 'Fatura',
    variables: [
      { key: 'invoiceNumber', label: 'Fatura No', example: 'INV-2024-001', type: 'STRING' },
      { key: 'customerName', label: 'Müşteri Adı', example: 'Ahmet Yılmaz', type: 'STRING' },
      { key: 'amount', label: 'Tutar', example: '5.000,00 TL', type: 'NUMBER' },
      { key: 'dueDate', label: 'Vade Tarihi', example: '01.03.2024', type: 'DATE' },
      { key: 'isPaid', label: 'Ödendi mi?', example: 'Hayır', type: 'BOOLEAN' },
    ],
  },
};
```

### Adım 2: Backend'de Kullanım

Service'inizde GenericTemplateService kullanın:

```java
@Service
public class InvoiceService {
    
    private final GenericTemplateService genericTemplateService;
    private final RabbitTemplate rabbitTemplate;
    
    public void sendInvoiceEmail(InvoiceModel invoice) {
        // Değişkenleri otomatik çıkar
        Map<String, Object> variables = genericTemplateService.extractVariables(invoice, "Invoice");
        
        // Event oluştur
        Map<String, Object> event = new HashMap<>();
        event.put("entityType", "Invoice");
        event.put("variables", variables);
        event.put("userGroupEmails", List.of(invoice.getCustomerEmail()));
        event.put("templateCode", "invoice_notification");
        
        // RabbitMQ'ya gönder
        rabbitTemplate.convertAndSend("call.request.exchange", "call.request.routing.key", event);
    }
}
```

Bu kadar! Başka bir değişiklik gerekmez.

## Mevcut Entity Tipleri

### 1. CallRequest (Call Request)
- customerName, customerEmail, customerPhone
- subject, message
- assignedGroup, status
- createdDate

### 2. Order (Sipariş)
- orderNumber, customerName, customerEmail
- totalAmount, orderDate
- status, shippingAddress, paymentMethod

### 3. User (Kullanıcı)
- username, email
- firstName, lastName
- phone, createdDate

### 4. Product (Ürün)
- productName, productCode
- price, category
- stock, description

## Örnek Template'ler

### Call Request Bildirimi
```html
<div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
  <h2 style="color: #333;">Yeni Call Request</h2>
  <p>Merhaba,</p>
  <p>Yeni bir müşteri talebiniz var:</p>
  
  <div style="background: #f5f5f5; padding: 15px; border-radius: 5px;">
    <p><strong>Müşteri:</strong> {{customerName}}</p>
    <p><strong>Email:</strong> {{customerEmail}}</p>
    <p><strong>Telefon:</strong> {{customerPhone}}</p>
    <p><strong>Konu:</strong> {{subject}}</p>
    <p><strong>Mesaj:</strong> {{message}}</p>
  </div>
  
  <p>Lütfen en kısa sürede müşteri ile iletişime geçin.</p>
  <p><strong>Atanan Grup:</strong> {{assignedGroup}}</p>
  <p><strong>Durum:</strong> {{status}}</p>
  <p><strong>Tarih:</strong> {{createdDate}}</p>
</div>
```

### Sipariş Onayı
```html
<div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
  <h2 style="color: #4CAF50;">Siparişiniz Onaylandı!</h2>
  <p>Merhaba {{customerName}},</p>
  <p>{{orderNumber}} numaralı siparişiniz başarıyla onaylanmıştır.</p>
  
  <div style="background: #f5f5f5; padding: 15px; border-radius: 5px; margin: 20px 0;">
    <h3 style="margin-top: 0;">Sipariş Detayları</h3>
    <p><strong>Sipariş No:</strong> {{orderNumber}}</p>
    <p><strong>Toplam Tutar:</strong> {{totalAmount}}</p>
    <p><strong>Sipariş Tarihi:</strong> {{orderDate}}</p>
    <p><strong>Durum:</strong> {{status}}</p>
    <p><strong>Teslimat Adresi:</strong> {{shippingAddress}}</p>
    <p><strong>Ödeme Yöntemi:</strong> {{paymentMethod}}</p>
  </div>
  
  <p>Siparişiniz en kısa sürede kargoya verilecektir.</p>
  <p>Teşekkür ederiz!</p>
</div>
```

### Kullanıcı Hoşgeldin Maili
```html
<div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
  <h2 style="color: #2196F3;">Hoşgeldiniz!</h2>
  <p>Merhaba {{firstName}} {{lastName}},</p>
  <p>Sistemimize hoşgeldiniz. Hesabınız başarıyla oluşturuldu.</p>
  
  <div style="background: #f5f5f5; padding: 15px; border-radius: 5px; margin: 20px 0;">
    <h3 style="margin-top: 0;">Hesap Bilgileriniz</h3>
    <p><strong>Kullanıcı Adı:</strong> {{username}}</p>
    <p><strong>Email:</strong> {{email}}</p>
    <p><strong>Telefon:</strong> {{phone}}</p>
    <p><strong>Kayıt Tarihi:</strong> {{createdDate}}</p>
  </div>
  
  <p>Hesabınızı kullanmaya başlayabilirsiniz.</p>
  <p>İyi günler dileriz!</p>
</div>
```

## API Endpoints

### Kullanılabilir Değişkenleri Getir
```
GET /api/v1/email-templates/variables/{entityType}

Örnek:
GET /api/v1/email-templates/variables/Order
```

### Template Kaydet
```
POST /api/v1/email-templates

Body:
{
  "code": "order_confirmation",
  "templateName": "Sipariş Onayı",
  "entityType": "Order",
  "subject": "Siparişiniz Onaylandı - {{orderNumber}}",
  "body": "<h1>Merhaba {{customerName}}</h1>...",
  "isActive": true
}
```

### Template Listele
```
GET /api/v1/email-templates
```

### Template Getir
```
GET /api/v1/email-templates/{code}
```

### Template Sil
```
DELETE /api/v1/email-templates/{code}
```

## Önemli Notlar

1. **Değişken İsimleri**: Entity'deki field isimleriyle aynı olmalı (case-sensitive)
2. **Getter Metodları**: Entity'de getter metodları olmalı
3. **Null Değerler**: Null değerler boş string olarak gösterilir
4. **HTML Desteği**: Template body'de HTML kullanabilirsiniz
5. **Inline CSS**: Email için inline CSS kullanın (external CSS çalışmaz)

## Sorun Giderme

### Değişkenler Değiştirilmiyor
- Değişken isminin entity field ismiyle aynı olduğundan emin olun
- Getter metodunun olduğunu kontrol edin
- Template syntax'ını kontrol edin: `{{variableName}}` (boşluk yok)

### Nested Değişkenler Çalışmıyor
- Nested objenin null olmadığından emin olun
- Getter metodlarının olduğunu kontrol edin
- Nokta notasyonu kullanın: `{{parent.child}}`

### Tarih Formatı Yanlış
- Tarihler `dd.MM.yyyy HH:mm` formatında gösterilir
- Özelleştirmek için `GenericTemplateServiceImpl`'deki `DATE_FORMAT`'ı değiştirin

## Test Etme

1. Backend'i başlatın: `mvn spring-boot:run` (btcstore klasöründe)
2. RabbitMQ projesini başlatın: `mvn spring-boot:run` (btcstorerabbit klasöründe)
3. Frontend'i başlatın: `npm run dev` (btc-store klasöründe)
4. http://localhost:3000/admin/email-templates adresine gidin
5. Yeni template oluşturun ve test edin

## Avantajlar

✅ **Generic**: Herhangi bir entity için çalışır
✅ **Kod Değişikliği Yok**: Yeni tipler için backend değişikliği gerekmez
✅ **Otomatik**: Reflection ile tüm alanları otomatik çıkarır
✅ **Esnek**: Nested objeler, tarihler, boolean'lar desteklenir
✅ **Kolay**: Sadece frontend'de tanım ekleyin
✅ **Ölçeklenebilir**: Kolayca genişletilebilir

## Sonuç

Template sistemi artık tamamen generic. Herhangi bir entity için template oluşturabilir, kod değişikliği yapmadan yeni entity tipleri ekleyebilirsiniz. Sistem reflection kullanarak otomatik olarak tüm alanları çıkarır ve template'leri işler.
