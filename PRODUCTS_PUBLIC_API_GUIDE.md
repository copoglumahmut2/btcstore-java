# Ürünler Public API Kullanım Kılavuzu

Bu dokümantasyon, ürünler sayfası için oluşturulan public API endpoint'lerinin kullanımını açıklar.

## Backend Endpoint'ler

### 1. Tüm Ürünleri Getir (Filtreleme ve Pagination ile)

**Endpoint:** `GET /v1/public/products`

**Query Parameters:**
- `category` (optional): Kategori kodu ile filtreleme
- `page` (optional, default: 1): Sayfa numarası (1'den başlar)
- `size` (optional, default: 20): Sayfa başına ürün sayısı

**Response:**
```json
{
  "status": "SUCCESS",
  "data": {
    "products": [
      {
        "code": "product-uuid",
        "name": {
          "tr": "Ürün Adı",
          "en": "Product Name"
        },
        "description": {
          "tr": "Detaylı açıklama",
          "en": "Detailed description"
        },
        "shortDescription": {
          "tr": "Kısa açıklama",
          "en": "Short description"
        },
        "categories": [
          {
            "code": "category-uuid",
            "name": {
              "tr": "Kategori Adı"
            }
          }
        ],
        "mainImage": {
          "code": "image-uuid",
          "absolutePath": "http://localhost:8080/media/..."
        },
        "images": [...],
        "active": true
      }
    ],
    "availableCategories": [
      {
        "code": "category-uuid",
        "name": {
          "tr": "Kategori Adı",
          "en": "Category Name"
        },
        "description": {
          "tr": "Kategori açıklaması"
        },
        "order": 1,
        "active": true
      }
    ],
    "selectedCategory": {
      "code": "category-uuid",
      "name": {
        "tr": "Seçili Kategori"
      }
    },
    "totalProducts": 150,
    "pageNumber": 1,
    "pageSize": 20,
    "totalPages": 8
  }
}
```

**Kullanım Örnekleri:**

```bash
# Tüm ürünleri getir (ilk sayfa, 20 ürün)
curl http://localhost:8080/v1/public/products

# İkinci sayfayı getir
curl http://localhost:8080/v1/public/products?page=2

# Sayfa başına 50 ürün
curl http://localhost:8080/v1/public/products?size=50

# Belirli bir kategoriye ait ürünleri getir (pagination ile)
curl "http://localhost:8080/v1/public/products?category=036a2307-9a94-4777-87a0-e743fd660977&page=1&size=20"
```

### 2. Tek Ürün Detayı Getir

**Endpoint:** `GET /v1/public/products/{code}`

**Path Parameters:**
- `code`: Ürün kodu (UUID)

**Response:**
```json
{
  "status": "SUCCESS",
  "data": {
    "code": "product-uuid",
    "name": {
      "tr": "Ürün Adı",
      "en": "Product Name"
    },
    "description": {
      "tr": "Detaylı açıklama"
    },
    "shortDescription": {
      "tr": "Kısa açıklama"
    },
    "categories": [...],
    "mainImage": {...},
    "images": [...],
    "features": ["Özellik 1", "Özellik 2"],
    "active": true
  }
}
```

**Kullanım Örneği:**

```bash
curl http://localhost:8080/v1/public/products/036a2307-9a94-4777-87a0-e743fd660977
```

## Frontend Kullanımı

### Service Kullanımı

```typescript
import { productService } from '@/services/product.service';

// Tüm ürünleri getir (ilk sayfa)
const filterData = await productService.getPublicProducts();

// İkinci sayfayı getir
const page2Data = await productService.getPublicProducts(undefined, 2, 20);

// Kategoriye göre filtrele (ilk sayfa)
const filteredData = await productService.getPublicProducts('category-uuid');

// Kategoriye göre filtrele (ikinci sayfa, 50 ürün)
const filteredPage2 = await productService.getPublicProducts('category-uuid', 2, 50);

// Tek ürün detayı
const product = await productService.getPublicProductByCode('product-uuid');
```

### Component Örneği

```typescript
'use client';

import { useState, useEffect } from 'react';
import { productService, ProductFilterData } from '@/services/product.service';

export default function ProductsPage() {
  const [filterData, setFilterData] = useState<ProductFilterData | null>(null);
  const [selectedCategory, setSelectedCategory] = useState<string>('');
  const [currentPage, setCurrentPage] = useState<number>(1);

  useEffect(() => {
    loadProducts(selectedCategory, currentPage);
  }, [selectedCategory, currentPage]);

  const loadProducts = async (categoryCode: string, page: number) => {
    const data = await productService.getPublicProducts(
      categoryCode || undefined, 
      page, 
      20
    );
    setFilterData(data);
  };

  const handleCategoryChange = (categoryCode: string) => {
    setSelectedCategory(categoryCode);
    setCurrentPage(1); // Reset to first page
  };

  return (
    <div>
      {/* Kategori filtreleme butonları */}
      <button onClick={() => handleCategoryChange('')}>Tümü</button>
      {filterData?.availableCategories.map(cat => (
        <button 
          key={cat.code} 
          onClick={() => handleCategoryChange(cat.code)}
        >
          {cat.name?.tr}
        </button>
      ))}

      {/* Ürün listesi */}
      {filterData?.products.map(product => (
        <div key={product.code}>
          <h3>{product.name?.tr}</h3>
          <p>{product.shortDescription?.tr}</p>
        </div>
      ))}

      {/* Pagination */}
      <div>
        <button 
          onClick={() => setCurrentPage(p => p - 1)}
          disabled={currentPage === 1}
        >
          Previous
        </button>
        
        <span>Page {filterData?.pageNumber} of {filterData?.totalPages}</span>
        
        <button 
          onClick={() => setCurrentPage(p => p + 1)}
          disabled={currentPage === filterData?.totalPages}
        >
          Next
        </button>
      </div>
    </div>
  );
}
```

## Özellikler

### 1. Akıllı Kategori Filtreleme
- API, getirilen ürünlerin sahip olduğu kategorileri otomatik olarak toplar
- Sadece aktif kategoriler listelenir
- Kategoriler `order` alanına göre sıralanır
- Boş kategoriler (ürünü olmayan) otomatik olarak filtrelenir

### 2. Pagination (Sayfalama)
- Default: 20 ürün per sayfa
- Özelleştirilebilir sayfa boyutu
- Toplam sayfa sayısı ve ürün sayısı bilgisi
- Sayfa numarası 1'den başlar (user-friendly)

### 3. Seçili Kategori Bilgisi
- Kategori seçildiğinde, o kategorinin detay bilgileri de döner
- Kategori açıklaması sayfa başlığı altında gösterilebilir

### 4. Performans
- Sadece aktif ve silinmemiş ürünler getirilir
- Pagination ile büyük veri setleri optimize edilir
- Kategori listesi tüm ürünlerden hesaplanır (filtreleme için)

### 5. Çoklu Kategori Desteği
- Bir ürün birden fazla kategoriye ait olabilir
- Kategori filtrelemesi bu yapıyı destekler

## URL Yapısı

```
/products                                    -> Tüm ürünler (sayfa 1)
/products?page=2                             -> İkinci sayfa
/products?size=50                            -> Sayfa başına 50 ürün
/products?category=036a2307-9a94-4777-87a0  -> Kategoriye göre filtrelenmiş
/products?category=xxx&page=2&size=30        -> Kategori + pagination
/products/{product-code}                     -> Ürün detay sayfası
```

## Güvenlik

- Bu endpoint'ler public'tir, authentication gerektirmez
- Sadece aktif (`active=true`) ve silinmemiş (`deleted=false`) ürünler döner
- Site bazlı filtreleme otomatik olarak yapılır

## Test

Backend'i başlattıktan sonra:

```bash
# Swagger UI
http://localhost:8080/swagger-ui.html

# Public Products endpoint'ini test et
curl http://localhost:8080/v1/public/products

# Kategori ile test et
curl "http://localhost:8080/v1/public/products?category=YOUR-CATEGORY-CODE"
```

## Notlar

- `availableCategories` listesi, o anda görüntülenen ürünlerin sahip olduğu kategorileri içerir
- Eğer hiç kategori seçilmemişse, tüm aktif ürünlerin kategorileri listelenir
- Bir kategori seçildiğinde, sadece o kategorideki ürünlerin diğer kategorileri listelenir (cross-filtering)
