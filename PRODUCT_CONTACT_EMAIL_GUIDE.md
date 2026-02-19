# Ürün İletişim Email Template Kılavuzu

## Genel Bakış

Ürün iletişim sistemi, normal call request'lerden farklı olarak özel bir email template kullanır. Bu template ürün sorumlu kullanıcılarına gönderilir ve ürün bilgilerini içerir.

## Email Template Bilgileri

**Template Code:** `product_contact_request`

**Kullanım Amacı:** Bir müşteri ürün hakkında iletişime geçtiğinde ürün sorumlu kullanıcılarına bildirim göndermek

**Alıcılar:** Ürün sorumlu kullanıcılarının email adresleri

## Template Değişkenleri

### Ürün Bilgileri

| Değişken | Tip | Açıklama | Örnek |
|----------|-----|----------|-------|
| `{{productName}}` | String | Ürün adı (Türkçe) | "Akıllı Telefon X1" |
| `{{productCode}}` | String | Ürün kodu (UUID) | "a1b2c3d4-..." |
| `{{productDescription}}` | String | Ürün kısa açıklaması | "En yeni teknoloji ile donatılmış..." |

### Müşteri Bilgileri

| Değişken | Tip | Açıklama | Örnek |
|----------|-----|----------|-------|
| `{{customerName}}` | String | Müşteri adı soyadı | "Ahmet Yılmaz" |
| `{{customerEmail}}` | String | Müşteri email adresi | "ahmet@example.com" |
| `{{customerPhone}}` | String | Müşteri telefon numarası | "+90 555 123 4567" |

### Talep Bilgileri

| Değişken | Tip | Açıklama | Örnek |
|----------|-----|----------|-------|
| `{{subject}}` | String | Talep konusu | "Ürün Hakkında İletişim: Akıllı Telefon X1" |
| `{{message}}` | String | Müşteri mesajı | "Bu ürün hakkında detaylı bilgi almak istiyorum..." |
| `{{createdDate}}` | String | Talep oluşturulma tarihi | "15.01.2024 14:30" |

## Template Yapısı

Email template HTML formatında ve şu bölümlerden oluşur:

### 1. Header (Başlık)
- Mavi gradient arka plan
- "Yeni Ürün İletişim Talebi" başlığı
- Emoji ile görsel zenginlik

### 2. Ürün Bilgileri Bölümü
- Beyaz arka plan, mavi sol border
- Ürün adı, kodu ve açıklaması
- Kolay okunabilir tablo formatı

### 3. Müşteri Bilgileri Bölümü
- Beyaz arka plan
- Ad, email, telefon ve tarih bilgileri
- Email ve telefon linkleri (mailto:, tel:)

### 4. Mesaj Bölümü
- Müşteri mesajının tam metni
- Gri arka plan ile vurgu
- Pre-wrap formatı (satır sonları korunur)

### 5. Aksiyon Butonu
- "Talebi Görüntüle" butonu
- Mavi arka plan
- Admin paneline yönlendirme (opsiyonel)

### 6. Footer (Alt Bilgi)
- Gri arka plan
- Otomatik email uyarısı
- Ürün sorumlusu bilgilendirmesi

## Örnek Email Görünümü

```
┌─────────────────────────────────────────┐
│  🔔 Yeni Ürün İletişim Talebi          │ (Mavi Header)
├─────────────────────────────────────────┤
│                                         │
│  📦 Ürün Bilgileri                     │
│  ├─ Ürün Adı: Akıllı Telefon X1       │
│  ├─ Ürün Kodu: PROD-12345              │
│  └─ Açıklama: En yeni teknoloji...     │
│                                         │
│  👤 Müşteri Bilgileri                  │
│  ├─ Ad Soyad: Ahmet Yılmaz             │
│  ├─ E-posta: ahmet@example.com         │
│  ├─ Telefon: +90 555 123 4567          │
│  └─ Tarih: 15.01.2024 14:30            │
│                                         │
│  💬 Müşteri Mesajı                     │
│  │ Bu ürün hakkında detaylı bilgi      │
│  │ almak istiyorum. Fiyat ve stok      │
│  │ durumu nedir?                        │
│                                         │
│       [ Talebi Görüntüle ]             │ (Mavi Buton)
│                                         │
├─────────────────────────────────────────┤
│  Bu email otomatik oluşturulmuştur.    │ (Gri Footer)
│  Ürün sorumlusu olarak bu ürün         │
│  hakkındaki iletişim taleplerini        │
│  alıyorsunuz.                           │
└─────────────────────────────────────────┘
```

## Template Özelleştirme

### Admin Panelinden Düzenleme

1. Admin paneline giriş yapın
2. "Email Templates" menüsüne gidin
3. `product_contact_request` template'ini bulun
4. "Düzenle" butonuna tıklayın
5. Subject ve Body alanlarını düzenleyin
6. Değişkenleri kullanarak dinamik içerik ekleyin
7. Kaydedin

### Değişken Kullanımı

Değişkenler `{{variableName}}` formatında kullanılır:

```html
<p>Sayın yetkili,</p>
<p><strong>{{customerName}}</strong> isimli müşteri 
<strong>{{productName}}</strong> ürünü hakkında iletişime geçti.</p>
```

### CSS Stilleri

Template inline CSS kullanır (email uyumluluğu için):

```html
<div style="background: #f9fafb; padding: 20px; border-radius: 8px;">
    <h3 style="color: #1e3a8a;">Başlık</h3>
    <p style="color: #6b7280;">İçerik</p>
</div>
```

## Çoklu Dil Desteği

Şu an template Türkçe olarak hazırlanmıştır. Farklı diller için:

1. Yeni template oluşturun: `product_contact_request_en`
2. İngilizce içerik ekleyin
3. Backend'de dil kontrolü ekleyin
4. Site diline göre uygun template'i seçin

## Test

### Template Test Etme

1. Admin panelinde template'i açın
2. "Test Email Gönder" butonuna tıklayın (varsa)
3. Veya gerçek bir ürün iletişim talebi oluşturun
4. Email'in geldiğini ve doğru göründüğünü kontrol edin

### Değişken Test Etme

Template'de tüm değişkenlerin doğru çalıştığını test edin:

```sql
-- Test için örnek değerler
{{productName}} -> "Test Ürün"
{{productCode}} -> "TEST-001"
{{customerName}} -> "Test Kullanıcı"
{{customerEmail}} -> "test@example.com"
{{message}} -> "Test mesajı"
```

## Sorun Giderme

### Email Gönderilmiyor

1. Template'in aktif olduğunu kontrol edin:
   ```sql
   SELECT active FROM email_template WHERE code = 'product_contact_request';
   ```

2. Template'in site'a bağlı olduğunu kontrol edin:
   ```sql
   SELECT site_id FROM email_template WHERE code = 'product_contact_request';
   ```

3. RabbitMQ'nun çalıştığını kontrol edin

### Değişkenler Çalışmıyor

1. Değişken adlarının doğru yazıldığını kontrol edin (case-sensitive)
2. Çift süslü parantez kullanıldığını kontrol edin: `{{variable}}`
3. Backend'de değişkenlerin doğru set edildiğini kontrol edin

### Email Görünümü Bozuk

1. HTML syntax'ını kontrol edin
2. Inline CSS kullanıldığını kontrol edin
3. Email client uyumluluğunu test edin (Gmail, Outlook, vb.)

## Best Practices

1. **Kısa ve Öz**: Email'i kısa ve anlaşılır tutun
2. **Responsive**: Mobil cihazlarda da iyi görünsün
3. **Inline CSS**: Email client'lar için inline CSS kullanın
4. **Alt Text**: Görseller için alt text ekleyin
5. **Test**: Farklı email client'larda test edin
6. **Değişkenler**: Tüm dinamik içerik için değişken kullanın
7. **Fallback**: Değişken boşsa fallback değer gösterin

## Örnek Özelleştirmeler

### Şirket Logosu Ekleme

```html
<div class="header">
    <img src="https://yoursite.com/logo.png" alt="Logo" style="max-width: 150px;">
    <h1>Yeni Ürün İletişim Talebi</h1>
</div>
```

### Aciliyet Göstergesi

```html
<div style="background: #fef2f2; border-left: 4px solid #ef4444; padding: 15px;">
    <strong>⚠️ Acil:</strong> Bu talep yüksek öncelikli olarak işaretlenmiştir.
</div>
```

### Ürün Görseli Ekleme

```html
<div class="product-info">
    <img src="{{productImageUrl}}" alt="{{productName}}" style="max-width: 200px;">
    <h2>{{productName}}</h2>
</div>
```

## Güvenlik

- Email içeriğinde kullanıcı girdilerini XSS'e karşı temizleyin
- Email adreslerini doğrulayın
- Spam koruması ekleyin
- Rate limiting uygulayın

## Performans

- Email gönderimini asenkron yapın (RabbitMQ)
- Toplu gönderim için batch işleme kullanın
- Email template'i cache'leyin
- Gereksiz değişken işlemlerinden kaçının
