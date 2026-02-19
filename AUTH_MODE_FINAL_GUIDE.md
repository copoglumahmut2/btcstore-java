# Auth Mode - Final Implementation Guide

## Genel Bakış

Dinamik auth sistemi: Herhangi bir sayfaya `?authMode=true` ekleyerek login sayfasına yönlendir, login sonrası geri dön.

## SON GÜNCELLEMELER (Düzeltmeler)

### 1. Token İsimleri Düzeltildi
- `useAuthStore.ts` içindeki `initializeAuth` fonksiyonu artık doğru token isimlerini kullanıyor
- `salesAccessToken` ❌ → `authAccessToken` ✅
- `salesRefreshToken` ❌ → `authRefreshToken` ✅

### 2. Redirect Mekanizması Düzeltildi (Next.js 14)
- `products/[id]/page.tsx` artık Suspense boundary kullanıyor
- `useSearchParams()` hook'u Suspense içinde çalışıyor (Next.js 14 gereksinimi)
- Redirect logic `useEffect` içinde çalışıyor
- Query string de returnUrl'e dahil ediliyor (`window.location.pathname + window.location.search`)

### 3. Kod Yapısı
```typescript
// products/[id]/page.tsx
function ProductDetailContent() {
  const searchParams = useSearchParams();
  const { isAuthenticated } = useAuthStore();
  
  useEffect(() => {
    const authMode = searchParams.get('authMode') === 'true';
    
    if (authMode && !isAuthenticated) {
      const currentUrl = window.location.pathname + window.location.search;
      const redirectUrl = `/auth?returnUrl=${encodeURIComponent(currentUrl)}`;
      window.location.href = redirectUrl;
    }
  }, [searchParams, isAuthenticated]);

  return <ProductDetail />;
}

export default function ProductDetailPage() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <ProductDetailContent />
    </Suspense>
  );
}
```

## Kullanım

### Senaryo: Ürün Dokümanlarını Görmek İstiyorum

```
1. Normal URL:
   http://localhost:3000/products/88e6b5d2-0d72-490d-9a47-8ec4d577047c

2. Auth Mode ile:
   http://localhost:3000/products/88e6b5d2-0d72-490d-9a47-8ec4d577047c?authMode=true

3. Akış:
   - authMode=true görür
   - Login değilse → /auth?returnUrl=/products/88e6b5d2... yönlendirir
   - Login ol
   - Otomatik olarak /products/88e6b5d2... sayfasına geri döner
   - Dokümanları görürsün
```

## Özellikler

### ✅ Dinamik Yönlendirme
- Herhangi bir sayfaya `?authMode=true` ekle
- Login değilse auth sayfasına yönlendir
- Login sonrası geldiği sayfaya geri dön

### ✅ Temiz URL'ler
- `/sales-login` ❌ → `/auth` ✅
- "Satış Girişi" ❌ → "Yetkili Girişi" ✅
- Daha genel ve profesyonel

### ✅ Token Yönetimi
- `salesAccessToken` ❌ → `authAccessToken` ✅
- `salesRefreshToken` ❌ → `authRefreshToken` ✅
- Admin token'larından ayrı

### ✅ Kompakt Banner
- Gri, minimal tasarım
- "Asistan Modu" kısa text
- Küçük font ve iconlar

## Endpoint Yapısı

### Auth Page
```
GET /auth?returnUrl=/products/123
```

### Login Flow
```
1. User: /products/123?authMode=true
2. System: Redirect to /auth?returnUrl=/products/123
3. User: Login
4. System: Redirect to /products/123
5. User: See documents
```

## Token Hierarchy

```
Priority: authAccessToken > accessToken

Auth Mode:
- authAccessToken (7 days)
- authRefreshToken (30 days)

Admin Mode:
- accessToken (7 days)
- refreshToken (30 days)
```

## API Request Flow

```typescript
// 1. Get token
const authToken = getCookie('authAccessToken');
const adminToken = getCookie('accessToken');
const token = authToken || adminToken;

// 2. Make request
headers['Authorization'] = `Bearer ${token}`;

// 3. If 401, try refresh
if (response.status === 401) {
  const refreshed = await refreshAccessToken();
  if (refreshed) {
    // Retry request
  } else {
    // Logout
  }
}
```

## Component Integration

### ProductDetail.tsx
```typescript
// Check for authMode parameter
const authMode = searchParams.get('authMode') === 'true';

// Redirect if not authenticated
useEffect(() => {
  if (authMode && !isAuthenticated) {
    const currentUrl = window.location.pathname;
    router.push(`/auth?returnUrl=${encodeURIComponent(currentUrl)}`);
  }
}, [authMode, isAuthenticated]);
```

### Auth Page
```typescript
// Get return URL
const returnUrl = searchParams.get('returnUrl') || '/';

// After login
window.location.href = returnUrl;
```

## Kullanıcı Deneyimi

### Normal Kullanıcı
```
1. /products/123 → Ürünü görür
2. Doküman bölümü yok
3. Banner yok
```

### Yetkili Kullanıcı (Auth Mode)
```
1. /products/123?authMode=true → Login sayfasına yönlendirilir
2. Login olur
3. /products/123 sayfasına geri döner
4. Banner görünür: "Asistan Modu • Ahmet Yılmaz"
5. Dokümanları görür
```

## Test Senaryoları

### Test 1: İlk Giriş
```
1. http://localhost:3000/products/123?authMode=true
2. /auth?returnUrl=/products/123 sayfasına yönlendirilir
3. Login ol
4. /products/123 sayfasına geri dön
5. Banner görün ✅
6. Dokümanları gör ✅
```

### Test 2: Zaten Login
```
1. Login olmuş kullanıcı
2. http://localhost:3000/products/456?authMode=true
3. Direkt /products/456 sayfasını görür
4. Banner var ✅
5. Dokümanlar var ✅
```

### Test 3: Logout
```
1. Banner'da "Çıkış" butonuna tıkla
2. Anasayfaya yönlendirilir
3. Banner kaybolur ✅
4. Token'lar temizlenir ✅
```

### Test 4: Token Expire
```
1. Login ol
2. Token'ı manuel sil (DevTools)
3. API isteği yap
4. Otomatik refresh ✅
5. İstek başarılı ✅
```

## Avantajlar

### Eski Sistem (salesMode)
```
❌ /sales-login?salesMode=true
❌ Sadece login sayfasında çalışır
❌ "Satış Girişi" spesifik
❌ salesAccessToken prefix
```

### Yeni Sistem (authMode)
```
✅ Herhangi bir sayfada ?authMode=true
✅ Dinamik return URL
✅ "Yetkili Girişi" genel
✅ authAccessToken prefix
✅ Daha esnek ve ölçeklenebilir
```

## Güvenlik

- ✅ Token-based authentication
- ✅ Auto-refresh mechanism
- ✅ Secure cookies
- ✅ Authorization header
- ✅ isAuthenticated() kontrolü
- ✅ Return URL validation

## Örnek Kullanım Senaryoları

### Senaryo 1: Email ile Link Gönderme
```
Satış ekibine email:

"Ürün dokümanlarını görmek için:
https://yoursite.com/products/123?authMode=true

Kullanıcı adı: [username]
Şifre: [password]"
```

### Senaryo 2: QR Code
```
QR Code içeriği:
https://yoursite.com/products/123?authMode=true

Kullanıcı QR'ı okutunca:
1. Login sayfasına gider
2. Login olur
3. Ürün sayfasına döner
4. Dokümanları görür
```

### Senaryo 3: Bookmark
```
Kullanıcı bookmark'a ekler:
https://yoursite.com/products/123?authMode=true

Her tıkladığında:
- Login ise direkt sayfa
- Değilse login sonrası sayfa
```

## Sonuç

Artık sistem:
- 🎯 Daha esnek (herhangi bir sayfada çalışır)
- 🔄 Dinamik (return URL ile geri dönüş)
- 🎨 Daha sade (kompakt banner)
- 🔐 Güvenli (token yönetimi)
- 📱 Kullanıcı dostu (kolay akış)
