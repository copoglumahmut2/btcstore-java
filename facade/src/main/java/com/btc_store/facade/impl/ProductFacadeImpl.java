package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.ProductData;
import com.btc_store.domain.enums.MediaCategory;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.CategoryModel;
import com.btc_store.domain.model.custom.CmsCategoryModel;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.ProductModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.ProductFacade;
import com.btc_store.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductFacadeImpl implements ProductFacade {

    private final SiteService siteService;
    private final ModelService modelService;
    private final ModelMapper modelMapper;
    private final SearchService searchService;
    private final MediaService mediaService;
    private final CmsCategoryService cmsCategoryService;

    @Override
    public List<ProductData> getAllProducts() {
        var siteModel = siteService.getCurrentSite();
        var productModels = searchService.search(ProductModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(productModels, ProductData[].class));
    }

    @Override
    public ProductData getProductByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var productModel = searchService.searchByCodeAndSite(ProductModel.class, code, siteModel);
        return modelMapper.map(productModel, ProductData.class);
    }

    @Override
    public ProductData saveProduct(ProductData productData, MultipartFile mainImageFile, List<MultipartFile> imageFiles, boolean removeMainImage) {
        ProductModel productModel;
        var siteModel = siteService.getCurrentSite();
        List<MediaModel> existingImages = new ArrayList<>();

        if (productData.isNew()) {
            productModel = modelMapper.map(productData, ProductModel.class);
            productModel.setCode(UUID.randomUUID().toString());
            productModel.setSite(siteModel);
        } else {
            productModel = searchService.searchByCodeAndSite(ProductModel.class, productData.getCode(), siteModel);
            existingImages = new ArrayList<>(productModel.getImages());
            
            modelMapper.map(productData, productModel);
        }

        List<CategoryModel> categories =new ArrayList<>();
        if (CollectionUtils.isNotEmpty(productData.getCategories())) {
            for (var category : productData.getCategories()) {
                categories.add(searchService.searchByCodeAndSite(CategoryModel.class, category.getCode(), siteModel));
            }
        }
        productModel.setCategories(categories);

        List<UserModel> responsibleUsers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(productData.getResponsibleUsers())) {
            for (var user : productData.getResponsibleUsers()) {
                responsibleUsers.add(searchService.searchByCodeAndSite(UserModel.class, user.getCode(), siteModel));
            }
        }
        productModel.setResponsibleUsers(responsibleUsers);

        if (CollectionUtils.isNotEmpty(productData.getFeatures())) {
            productModel.setFeatures(productData.getFeatures());
        } else {
            productModel.setFeatures(new ArrayList<>());
        }

        handleProductImages(productData, productModel, imageFiles, existingImages, siteModel);

        var savedModel = modelService.save(productModel);
        return modelMapper.map(savedModel, ProductData.class);
    }

    @Override
    public void deleteProduct(String code) {
        var siteModel = siteService.getCurrentSite();
        var productModel = searchService.searchByCodeAndSite(ProductModel.class, code, siteModel);
        productModel.setDeleted(true);
        productModel.setActive(false);
        modelService.save(productModel);
    }

    /**
     * Ürün görsellerini işler - yeni yükleme, sıralama ve ana görsel seçimi
     */
    private void handleProductImages(ProductData productData, ProductModel productModel, 
                                     List<MultipartFile> imageFiles, List<MediaModel> existingImages,
                                     SiteModel siteModel) {
        if (Objects.nonNull(imageFiles) && !imageFiles.isEmpty()) {
            // Yeni görseller yükleniyor
            uploadAndSetNewImages(productData, productModel, imageFiles, existingImages, siteModel);
        } else {
            // Mevcut görselleri güncelle (sıralama veya silme)
            updateExistingImages(productData, productModel, existingImages);
        }
    }

    /**
     * Yeni görselleri yükler ve mevcut görsellerle birleştirir
     */
    private void uploadAndSetNewImages(ProductData productData, ProductModel productModel, 
                                       List<MultipartFile> imageFiles, List<MediaModel> existingImages, 
                                       SiteModel siteModel) {
        try {
            var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.PRODUCT.getValue(), siteModel);
            List<MediaModel> finalImages = new ArrayList<>();
            
            // Önce mevcut görselleri sıraya ekle
            addExistingImagesInOrder(productData, existingImages, finalImages);
            
            // Sonra yeni görselleri yükle ve ekle
            uploadNewImages(imageFiles, cmsCategoryModel, siteModel, finalImages);
            
            productModel.setImages(finalImages);
            
            // Ana görseli ayarla
            setMainImage(productData, productModel, finalImages);
            
            log.info("Toplam {} görsel işlendi", finalImages.size());
        } catch (Exception e) {
            log.error("Ürün görselleri yüklenirken hata: {}", e.getMessage());
            throw new RuntimeException("Ürün görselleri yüklenirken hata: " + e.getMessage());
        }
    }

    /**
     * Mevcut görselleri belirtilen sıraya göre ekler
     */
    private void addExistingImagesInOrder(ProductData productData, List<MediaModel> existingImages, List<MediaModel> finalImages) {
        if (CollectionUtils.isNotEmpty(productData.getImageCodesInOrder())) {
            for (String imageCode : productData.getImageCodesInOrder()) {
                existingImages.stream()
                        .filter(img -> img.getCode().equals(imageCode))
                        .findFirst()
                        .ifPresent(finalImages::add);
            }
            log.info("{} mevcut görsel korundu", finalImages.size());
        }
    }

    /**
     * Yeni görselleri yükler
     */
    private void uploadNewImages(List<MultipartFile> imageFiles, CmsCategoryModel cmsCategoryModel,
                                 SiteModel siteModel, List<MediaModel> finalImages) {
        for (MultipartFile imageFile : imageFiles) {
            if (!imageFile.isEmpty()) {
                var mediaModel = mediaService.storage(imageFile, false, cmsCategoryModel, siteModel);
                finalImages.add(mediaModel);
            }
        }
        log.info("{} yeni görsel yüklendi", imageFiles.size());
    }

    /**
     * Mevcut görselleri günceller (yeni yükleme olmadan)
     */
    private void updateExistingImages(ProductData productData, ProductModel productModel, List<MediaModel> existingImages) {
        if (Objects.nonNull(productData.getImageCodesInOrder())) {
            // Frontend'den gelen sıralı görsel kodlarını kullan
            List<MediaModel> orderedImages = new ArrayList<>();
            for (String imageCode : productData.getImageCodesInOrder()) {
                existingImages.stream()
                        .filter(img -> img.getCode().equals(imageCode))
                        .findFirst()
                        .ifPresent(orderedImages::add);
            }
            productModel.setImages(orderedImages);
            log.info("Görsel sırası güncellendi. Yeni: {}, Eski: {}", orderedImages.size(), existingImages.size());
            
            // Görsel kalmadıysa ana görseli temizle
            if (orderedImages.isEmpty()) {
                productModel.setMainImage(null);
                log.info("Tüm görseller ve ana görsel temizlendi");
            }
        } else {
            productModel.setImages(existingImages);
        }
        
        // Ana görseli güncelle
        if (!productModel.getImages().isEmpty()) {
            updateMainImageFromIndex(productData, productModel);
        }
    }

    /**
     * Ana görseli index'e göre ayarlar
     */
    private void setMainImage(ProductData productData, ProductModel productModel, List<MediaModel> images) {
        Integer mainImageIndex = productData.getMainImageIndex();
        if (mainImageIndex != null && mainImageIndex >= 0 && mainImageIndex < images.size()) {
            productModel.setMainImage(images.get(mainImageIndex));
        } else if (!images.isEmpty()) {
            productModel.setMainImage(images.get(0));
        }
    }

    /**
     * Mevcut görseller için ana görseli günceller
     */
    private void updateMainImageFromIndex(ProductData productData, ProductModel productModel) {
        Integer mainImageIndex = productData.getMainImageIndex();
        if (mainImageIndex != null && mainImageIndex >= 0 && mainImageIndex < productModel.getImages().size()) {
            productModel.setMainImage(productModel.getImages().get(mainImageIndex));
            log.info("Ana görsel index'e güncellendi: {}", mainImageIndex);
        } else if (Objects.isNull(productModel.getMainImage())) {
            // Ana görsel yoksa ilk görseli ayarla
            productModel.setMainImage(productModel.getImages().get(0));
        }
    }
}
