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
        MediaModel oldMainImage = null;

        if (productData.isNew()) {
            productModel = modelMapper.map(productData, ProductModel.class);
            productModel.setCode(UUID.randomUUID().toString());
            productModel.setSite(siteModel);
        } else {
            productModel = searchService.searchByCodeAndSite(ProductModel.class, productData.getCode(), siteModel);
            oldMainImage = productModel.getMainImage();
            MediaModel mainImageToKeep = productModel.getMainImage();
            List<MediaModel> imagesToKeep = new ArrayList<>(productModel.getImages());
            
            modelMapper.map(productData, productModel);

            if ((Objects.isNull(mainImageFile) || mainImageFile.isEmpty()) && !removeMainImage) {
                productModel.setMainImage(mainImageToKeep);
            }
            
            if (Objects.isNull(imageFiles) || imageFiles.isEmpty()) {
                productModel.setImages(imagesToKeep);
            }
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

        // Handle responsible user
        if (Objects.nonNull(productData.getResponsibleUser()) && Objects.nonNull(productData.getResponsibleUser().getCode())) {
            var userModel = searchService.searchByCodeAndSite(UserModel.class, productData.getResponsibleUser().getCode(), siteModel);
            productModel.setResponsibleUser(userModel);
        } else {
            productModel.setResponsibleUser(null);
        }

        // Handle main image upload
        boolean hasNewMainImage = Objects.nonNull(mainImageFile) && !mainImageFile.isEmpty();

        if (hasNewMainImage) {
            try {
                var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.PRODUCT.getValue(), siteModel);
                var mediaModel = mediaService.storage(mainImageFile, false, cmsCategoryModel, siteModel);
                productModel.setMainImage(mediaModel);

                if (Objects.nonNull(oldMainImage)) {
                    mediaService.flagMediaForDelete(oldMainImage.getCode(), siteModel);
                }
            } catch (Exception e) {
                log.error("Error storing product main image: {}", e.getMessage());
                throw new RuntimeException("Error storing product main image: " + e.getMessage());
            }
        } else if (removeMainImage && Objects.nonNull(oldMainImage)) {
            try {
                mediaService.flagMediaForDelete(oldMainImage.getCode(), siteModel);
                productModel.setMainImage(null);
            } catch (Exception e) {
                log.error("Error removing product main image: {}", e.getMessage());
            }
        }

        // Handle additional images upload
        if (Objects.nonNull(imageFiles) && !imageFiles.isEmpty()) {
            try {
                var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.PRODUCT.getValue(), siteModel);
                List<MediaModel> newImages = new ArrayList<>();
                
                for (MultipartFile imageFile : imageFiles) {
                    if (!imageFile.isEmpty()) {
                        var mediaModel = mediaService.storage(imageFile, false, cmsCategoryModel, siteModel);
                        newImages.add(mediaModel);
                    }
                }
                
                productModel.setImages(newImages);
            } catch (Exception e) {
                log.error("Error storing product images: {}", e.getMessage());
                throw new RuntimeException("Error storing product images: " + e.getMessage());
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
