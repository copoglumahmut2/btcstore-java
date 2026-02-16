package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.ProductData;
import com.btc_store.domain.enums.MediaCategory;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.CategoryModel;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.ProductModel;
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

        // Handle categories
        if (CollectionUtils.isNotEmpty(productData.getCategories())) {
            List<CategoryModel> categories = productData.getCategories().stream()
                    .map(categoryData -> searchService.searchByCodeAndSite(CategoryModel.class, categoryData.getCode(), siteModel))
                    .collect(Collectors.toList());
            productModel.setCategories(categories);
        } else {
            productModel.setCategories(new ArrayList<>());
        }

        // Handle responsible users (multiple)
        if (CollectionUtils.isNotEmpty(productData.getResponsibleUsers())) {
            List<UserModel> users = productData.getResponsibleUsers().stream()
                    .map(userData -> searchService.searchByCodeAndSite(UserModel.class, userData.getCode(), siteModel))
                    .collect(Collectors.toList());
            productModel.setResponsibleUsers(users);
        } else {
            productModel.setResponsibleUsers(new ArrayList<>());
        }

        // Handle features
        if (CollectionUtils.isNotEmpty(productData.getFeatures())) {
            productModel.setFeatures(productData.getFeatures());
        } else {
            productModel.setFeatures(new ArrayList<>());
        }

        // Handle images upload - preserve order and existing images
        List<MediaModel> finalImages = new ArrayList<>();
        
        if (Objects.nonNull(imageFiles) && !imageFiles.isEmpty()) {
            try {
                var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.PRODUCT.getValue(), siteModel);
                
                // First, add existing images in order (if imageCodesInOrder is provided)
                if (CollectionUtils.isNotEmpty(productData.getImageCodesInOrder())) {
                    for (String imageCode : productData.getImageCodesInOrder()) {
                        existingImages.stream()
                                .filter(img -> img.getCode().equals(imageCode))
                                .findFirst()
                                .ifPresent(finalImages::add);
                    }
                    log.info("Kept {} existing images", finalImages.size());
                }
                
                // Then, upload and add new images
                for (MultipartFile imageFile : imageFiles) {
                    if (!imageFile.isEmpty()) {
                        var mediaModel = mediaService.storage(imageFile, false, cmsCategoryModel, siteModel);
                        finalImages.add(mediaModel);
                    }
                }
                log.info("Added {} new images. Total: {}", imageFiles.size(), finalImages.size());
                
                productModel.setImages(finalImages);
                
                // Set main image based on index
                Integer mainImageIndex = productData.getMainImageIndex();
                if (mainImageIndex != null && mainImageIndex >= 0 && mainImageIndex < finalImages.size()) {
                    productModel.setMainImage(finalImages.get(mainImageIndex));
                } else if (!finalImages.isEmpty()) {
                    productModel.setMainImage(finalImages.get(0));
                }
            } catch (Exception e) {
                log.error("Error storing product images: {}", e.getMessage());
                throw new RuntimeException("Error storing product images: " + e.getMessage());
            }
        } else {
            // Keep existing images if no new images uploaded
            // But respect the order and deletions from frontend
            if (Objects.nonNull(productData.getImageCodesInOrder())) {
                // Frontend sent ordered list of image codes (can be empty to clear all images)
                List<MediaModel> orderedImages = new ArrayList<>();
                for (String imageCode : productData.getImageCodesInOrder()) {
                    existingImages.stream()
                            .filter(img -> img.getCode().equals(imageCode))
                            .findFirst()
                            .ifPresent(orderedImages::add);
                }
                productModel.setImages(orderedImages);
                log.info("Updated image order. New count: {}, Original count: {}", orderedImages.size(), existingImages.size());
                
                // Clear main image if no images left
                if (orderedImages.isEmpty()) {
                    productModel.setMainImage(null);
                    log.info("Cleared all images and main image");
                }
            } else {
                productModel.setImages(existingImages);
            }
            
            // Update main image if provided and images exist
            if (!productModel.getImages().isEmpty()) {
                Integer mainImageIndex = productData.getMainImageIndex();
                if (mainImageIndex != null && mainImageIndex >= 0 && mainImageIndex < productModel.getImages().size()) {
                    productModel.setMainImage(productModel.getImages().get(mainImageIndex));
                    log.info("Updated main image to index: {}", mainImageIndex);
                } else if (Objects.isNull(productModel.getMainImage())) {
                    // Set first image as main if not set
                    productModel.setMainImage(productModel.getImages().get(0));
                }
            }
        }

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
}
