# Ürün İletişim Sistemi - Test Checklist

## 📋 Kurulum Checklist

### Database
- [ ] `PRODUCT_CONTACT_MIGRATION.sql` çalıştırıldı
- [ ] `call_request` tablosunda `product_id` kolonu var
- [ ] Foreign key constraint oluşturuldu
- [ ] Index oluşturuldu
- [ ] `PRODUCT_CONTACT_EMAIL_TEMPLATE.sql` çalıştırıldı
- [ ] Email template database'de var
- [ ] Email template aktif

### Backend
- [ ] Backend başarıyla compile oldu
- [ ] Hiç syntax hatası yok
- [ ] `StoreCallRequestModel` product field'ı var
- [ ] `StoreCallRequestData` product field'ı var
- [ ] `CallRequestFacade` yeni metod var
- [ ] `CallRequestFacadeImpl` yeni metod implement edildi
- [ ] `PublicController` yeni endpoint var
- [ ] Backend başarıyla başladı
- [ ] Swagger UI açılıyor

### Frontend
- [ ] `public.service.ts` yeni metodlar var
- [ ] `ProductContact.tsx` güncellendi
- [ ] `ProductDetail.tsx` iletişim butonu var
- [ ] Frontend başarıyla compile oldu
- [ ] Hiç TypeScript hatası yok
- [ ] Frontend başarıyla başladı

## 🧪 Fonksiyonel Test Checklist

### Ürün Detay Sayfası
- [ ] Ürün detay sayfası açılıyor
- [ ] Sağ tarafta (desktop) iletişim bölümü görünüyor
- [ ] Üstte (mobil) iletişim bölümü görünüyor
- [ ] "İletişime Geç" butonu görünüyor
- [ ] Buton tıklanabiliyor
- [ ] İletişim sayfasına yönlendiriyor

### İletişim Formu
- [ ] Form sayfası açılıyor
- [ ] Ürün adı gösteriliyor
- [ ] Tüm form alanları görünüyor:
  - [ ] Ad
  - [ ] Soyad
  - [ ] Email
  - [ ] Telefon
  - [ ] Mesaj
  - [ ] KVKK checkbox
- [ ] KVKK metni gösteriliyor
- [ ] KVKK modal açılıyor
- [ ] Form validation çalışıyor
- [ ] Zorunlu alanlar kontrol ediliyor

### Form Gönderimi
- [ ] Form başarıyla gönderilebiliyor
- [ ] Loading state gösteriliyor
- [ ] Buton disable oluyor
- [ ] Başarı mesajı gösteriliyor
- [ ] Ürün detay sayfasına yönlendiriyor
- [ ] Hata durumunda uyarı gösteriliyor

### Backend İşlemleri
- [ ] Call request oluşturuluyor
- [ ] Ürün bilgisi bağlanıyor
- [ ] Subject otomatik oluşturuluyor
- [ ] KVKK dokümanı bağlanıyor
- [ ] Ürün sorumlu kullanıcıları atanıyor (varsa)
- [ ] Status ASSIGNED oluyor (sorumlu varsa)
- [ ] Status PENDING oluyor (sorumlu yoksa)

### Email Gönderimi
- [ ] Email template bulunuyor
- [ ] Ürün sorumlu kullanıcıları bulunuyor
- [ ] Email adresleri alınıyor
- [ ] Template değişkenleri dolduruluyor
- [ ] Email gönderiliyor (veya log'a yazılıyor)
- [ ] Email alıcılara ulaşıyor
- [ ] Email doğru görünüyor

## 🎨 UI/UX Test Checklist

### Desktop
- [ ] Layout düzgün görünüyor
- [ ] İletişim bölümü sağda
- [ ] Butonlar tıklanabiliyor
- [ ] Form düzgün görünüyor
- [ ] Modal düzgün açılıyor

### Mobil
- [ ] Layout responsive
- [ ] İletişim bölümü üstte
- [ ] Butonlar dokunulabiliyor
- [ ] Form mobilde düzgün
- [ ] Klavye açılınca sorun yok

### Tarayıcı Uyumluluğu
- [ ] Chrome
- [ ] Firefox
- [ ] Safari
- [ ] Edge
- [ ] Mobil Chrome
- [ ] Mobil Safari

## 📧 Email Test Checklist

### Email İçeriği
- [ ] Subject doğru
- [ ] Ürün adı gösteriliyor
- [ ] Ürün kodu gösteriliyor
- [ ] Ürün açıklaması gösteriliyor
- [ ] Müşteri adı gösteriliyor
- [ ] Müşteri email gösteriliyor
- [ ] Müşteri telefon gösteriliyor
- [ ] Müşteri mesajı gösteriliyor
- [ ] Tarih gösteriliyor

### Email Görünümü
- [ ] Header düzgün
- [ ] Ürün bilgileri bölümü düzgün
- [ ] Müşteri bilgileri bölümü düzgün
- [ ] Mesaj bölümü düzgün
- [ ] Buton düzgün
- [ ] Footer düzgün
- [ ] Renkler doğru
- [ ] Font'lar okunabilir

### Email Client Uyumluluğu
- [ ] Gmail (web)
- [ ] Gmail (mobil)
- [ ] Outlook (web)
- [ ] Outlook (desktop)
- [ ] Apple Mail
- [ ] Thunderbird

## 🔒 Güvenlik Test Checklist

### Input Validation
- [ ] Email formatı kontrol ediliyor
- [ ] Telefon formatı kontrol ediliyor
- [ ] XSS koruması var
- [ ] SQL injection koruması var
- [ ] KVKK onayı zorunlu

### Authorization
- [ ] Public endpoint authentication gerektirmiyor
- [ ] Admin endpoint'leri korumalı
- [ ] Site bazlı filtreleme çalışıyor

## 🚀 Performance Test Checklist

### Response Time
- [ ] Form gönderimi < 2 saniye
- [ ] Sayfa yükleme < 3 saniye
- [ ] Email gönderimi asenkron

### Database
- [ ] Index'ler çalışıyor
- [ ] Query'ler optimize
- [ ] N+1 problemi yok

## 🐛 Hata Senaryoları Test Checklist

### Ürün Bulunamadı
- [ ] Hata mesajı gösteriliyor
- [ ] 404 veya uygun hata kodu
- [ ] Kullanıcı bilgilendiriliyor

### Email Template Bulunamadı
- [ ] Call request yine de oluşturuluyor
- [ ] Log'a yazılıyor
- [ ] Sistem çökmüyor

### Ürün Sorumlusu Yok
- [ ] Call request oluşturuluyor
- [ ] Status PENDING
- [ ] Normal süreç işliyor

### Email Adresi Yok
- [ ] O kullanıcıya email gönderilmiyor
- [ ] Diğer kullanıcılara gönderiliyor
- [ ] Log'a yazılıyor

### KVKK Dokümanı Yok
- [ ] Form yine de çalışıyor
- [ ] Checkbox gösterilmiyor veya genel metin
- [ ] Call request oluşturuluyor

## 📊 Admin Panel Test Checklist

### Call Request Listesi
- [ ] Ürün iletişim talepleri görünüyor
- [ ] Ürün bilgisi gösteriliyor
- [ ] Status doğru gösteriliyor
- [ ] Atanan kullanıcılar gösteriliyor

### Call Request Detayı
- [ ] Ürün bilgisi detaylı gösteriliyor
- [ ] Müşteri bilgileri gösteriliyor
- [ ] Mesaj gösteriliyor
- [ ] History gösteriliyor
- [ ] Yeniden atama yapılabiliyor

## 🔄 Integration Test Checklist

### Call Request Sistemi
- [ ] Normal call request'ler etkilenmiyor
- [ ] Ürün iletişimi ayrı çalışıyor
- [ ] Email template'ler karışmıyor
- [ ] History doğru kaydediliyor

### Product Sistemi
- [ ] Ürün CRUD etkilenmiyor
- [ ] Sorumlu kullanıcı ataması çalışıyor
- [ ] Ürün silme/güncelleme sorun çıkarmıyor

### User Sistemi
- [ ] Kullanıcı CRUD etkilenmiyor
- [ ] Email adresi değişikliği yansıyor
- [ ] Kullanıcı silme/deaktif etme çalışıyor

## 📝 Dokümantasyon Checklist

- [ ] README.md okunaklı
- [ ] Implementation Guide detaylı
- [ ] Email Guide açıklayıcı
- [ ] SQL script'ler çalışıyor
- [ ] Örnekler doğru
- [ ] Değişkenler listelenmiş

## ✅ Final Checklist

- [ ] Tüm testler geçti
- [ ] Hiç kritik bug yok
- [ ] Performance kabul edilebilir
- [ ] Dokümantasyon tamamlandı
- [ ] Code review yapıldı
- [ ] Production'a hazır

## 🎯 Test Sonuçları

### Başarılı Testler
- Toplam: ___
- Başarılı: ___
- Başarısız: ___

### Bulunan Buglar
1. 
2. 
3. 

### Notlar
- 
- 
- 

## 📅 Test Tarihi

- Test Eden: ___________
- Tarih: ___________
- Versiyon: ___________
- Ortam: ___________ (Dev/Test/Prod)
