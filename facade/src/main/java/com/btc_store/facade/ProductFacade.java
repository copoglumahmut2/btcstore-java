package com.btc_store.facade;

import com.btc_store.domain.data.custom.DocumentData;
import com.btc_store.domain.data.custom.ProductData;
import com.btc_store.domain.data.custom.ProductFilterData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductFacade {
    List<ProductData> getAllProducts();
    ProductData getProductByCode(String code);
    ProductData saveProduct(ProductData productData, MultipartFile mainImageFile, List<MultipartFile> imageFiles, boolean removeMainImage);
    void deleteProduct(String code);
    
    // Public endpoints
    List<ProductData> getActiveProducts();
    ProductFilterData getProductsWithFilters(String categoryCode, Integer page, Integer size);
    ProductData getActiveProductByCode(String code);
    
    // Document endpoints
    List<DocumentData> getProductDocuments(String productCode);
}
