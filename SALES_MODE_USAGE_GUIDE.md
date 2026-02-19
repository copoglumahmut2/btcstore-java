# Satış Modu Kullanım Kılavuzu

## Genel Bakış

Satış ekibi için gizli bir login sistemi. Normal kullanıcılar bu özelliğin varlığından haberdar olmaz.

## Nasıl Kullanılır?

### 1. Login Sayfasına Erişim

Satış ekibine özel URL'i paylaş:

```
https://yoursite.com/sales-login?salesMode=true
```

**Önemli:** `?salesMode=true` parametresi olmadan sayfa 404 döner!

### 2. Login Olma

- Kullanıcı adı ve şifre ile giriş yap
- Başarılı login sonrası anasayfaya yönlendirilir
- Üstte mavi bir banner belirir: "Asistan Kullanıcı Modu Aktif"

### 3. Dokümanları Görme

- Herhangi bir ürün detay sayfasına git
- Sayfanın altında "Ürün Dokümanları" bölümü görünür
- Normal ziyaretçiler bu bölümü göremez

### 4. Çıkış Yapma

Üstteki mavi banner'da "Çıkış Yap" butonuna tıkla

## Özellikler

### ✅ Tamamen Gizli
- Header'da login/logout butonu yok
- Normal kullanıcılar varlığından haberdar olmaz
- Sadece özel URL ile erişilebilir

### ✅ Görsel Feedback
- Login olduktan sonra üstte mavi banner
- Kullanıcı adı ve soyadı gösterilir
- "Asistan Kullanıcı Modu Aktif" mesajı

### ✅ Ayrı Token Yönetimi
- Sales token'ları: `salesAccessToken`, `salesRefreshToken`
- Admin token'ları: `accessToken`, `refreshToken`
- Birbirine karışmaz

### ✅ Güvenli
- URL parametresi olmadan 404
- Token-based authentication
- Auto-refresh token

## Teknik Detaylar

### Token Storage

```javascript
// Sales tokens (cookie)
salesAccessToken  // 7 gün
salesRefreshToken // 30 gün

// Admin tokens (cookie) - karışmaz
accessToken
refreshToken
```

### API Request Priority

```javascript
// API client önce sales token'ı kontrol eder
const salesToken = getCookie('salesAccessToken');
const adminToken = getCookie('accessToken');
const token = salesToken || adminToken;
```

### Banner Component

```tsx
<SalesBanner />
// Sadece isAuthenticated === true ise görünür
// Üstte mavi gradient banner
// Kullanıcı bilgisi + Çıkış butonu
```

## Kullanıcı Senaryoları

### Senaryo 1: İlk Giriş
1. Satış temsilcisi özel URL'i alır
2. `/sales-login?salesMode=true` adresine gider
3. Kullanıcı adı/şifre ile login olur
4. Anasayfaya yönlendirilir
5. Üstte mavi banner görünür

### Senaryo 2: Doküman Görüntüleme
1. Login olmuş kullanıcı ürün detay sayfasına gider
2. Sayfanın altında "Ürün Dokümanları" bölümünü görür
3. Dokümanları indirebilir

### Senaryo 3: Çıkış
1. Üstteki banner'da "Çıkış Yap" butonuna tıklar
2. Token'lar silinir
3. Banner kaybolur
4. Doküman bölümleri artık görünmez

### Senaryo 4: Yanlış URL
1. Kullanıcı `/sales-login` adresine gider (parametre yok)
2. 404 sayfası görür
3. Özelliğin varlığından haberdar olmaz

## Güvenlik Notları

- ✅ URL parametresi kontrolü
- ✅ Ayrı token namespace
- ✅ Token expiration
- ✅ Auto-refresh
- ✅ Secure cookies
- ✅ Authorization header

## Test

```bash
# 1. Login sayfasına git (parametre ile)
http://localhost:3000/sales-login?salesMode=true

# 2. Login ol
Username: [sales_user]
Password: [password]

# 3. Anasayfada banner'ı gör
# 4. Ürün detay sayfasında dokümanları gör
# 5. Çıkış yap
```

## Satış Ekibine Talimatlar

Satış ekibine şu bilgileri ver:

```
Merhaba,

Ürün dokümanlarına erişmek için:

1. Bu linke tıklayın: https://yoursite.com/sales-login?salesMode=true
2. Kullanıcı adı ve şifrenizle giriş yapın
3. Herhangi bir ürün sayfasına gidin
4. Sayfanın altında dokümanları göreceksiniz

Çıkış yapmak için üstteki mavi alandaki "Çıkış Yap" butonuna tıklayın.

Not: Bu link gizlidir, lütfen paylaşmayın.
```

## Sorun Giderme

### Banner Görünmüyor
- Cookie'leri kontrol et: `salesAccessToken` var mı?
- Console'da hata var mı?
- `initializeAuth()` çağrıldı mı?

### Dokümanlar Görünmüyor
- Login olmuş musun?
- Ürüne doküman eklenmiş mi?
- Backend çalışıyor mu?
- Network tab'da `/documents` isteği 200 dönüyor mu?

### 404 Hatası
- URL'de `?salesMode=true` var mı?
- Doğru URL: `/sales-login?salesMode=true`
- Yanlış URL: `/sales-login`
