package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.CategoryData;
import com.btc_store.domain.data.custom.ProductData;
import com.btc_store.domain.data.custom.ProductFilterData;
import com.btc_store.domain.enums.MediaCategory;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.*;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.ProductFacade;
import com.btc_store.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        SiteModel siteModel = siteService.getCurrentSite();
        Collection<ProductModel> productModels = searchService.search(ProductModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(productModels, ProductData[].class));
    }

    @Override
    public ProductData getProductByCode(String code) {
        SiteModel siteModel = siteService.getCurrentSite();
        ProductModel productModel = searchService.searchByCodeAndSite(ProductModel.class, code, siteModel);
        return modelMapper.map(productModel, ProductData.class);
    }

    @Override
    public ProductData saveProduct(ProductData productData, MultipartFile mainImageFile, List<MultipartFile> imageFiles, boolean removeMainImage) {
        ProductModel productModel;
        SiteModel siteModel = siteService.getCurrentSite();
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

        List<CategoryModel> categories = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(productData.getCategories())) {
            for (com.btc_store.domain.data.custom.CategoryData category : productData.getCategories()) {
                categories.add(searchService.searchByCodeAndSite(CategoryModel.class, category.getCode(), siteModel));
            }
        }
        productModel.setCategories(categories);

        List<UserModel> responsibleUsers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(productData.getResponsibleUsers())) {
            for (com.btc_store.domain.data.custom.user.UserData user : productData.getResponsibleUsers()) {
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

        ProductModel savedModel = modelService.save(productModel);
        return modelMapper.map(savedModel, ProductData.class);
    }

    @Override
    public void deleteProduct(String code) {
        SiteModel siteModel = siteService.getCurrentSite();
        ProductModel productModel = searchService.searchByCodeAndSite(ProductModel.class, code, siteModel);
        productModel.setDeleted(true);
        productModel.setActive(false);
        modelService.save(productModel);
    }

    @Override
    public List<ProductData> getActiveProducts() {
        SiteModel siteModel = siteService.getCurrentSite();
        Collection<ProductModel> productModels = searchService.search(ProductModel.class,
                Map.of(
                        StoreSiteBasedItemModel.Fields.site, siteModel,
                        "active", true,
                        "deleted", false
                ),
                SearchOperator.AND);
        return List.of(modelMapper.map(productModels, ProductData[].class));
    }

    @Override
    public ProductFilterData getProductsWithFilters(String categoryCode, Integer page, Integer size) {
        SiteModel siteModel = siteService.getCurrentSite();
        ProductFilterData filterData = new ProductFilterData();
        
        // Default pagination values
        int pageNumber = (page != null && page > 0) ? page - 1 : 0;
        int pageSize = (size != null && size > 0) ? size : 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        
        CategoryModel selectedCategory = null;
        
        // Build search criteria
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put(StoreSiteBasedItemModel.Fields.site, siteModel);
        searchParams.put("active", true);
        searchParams.put("deleted", false);
        
        if (StringUtils.isNotBlank(categoryCode)) {
            selectedCategory = searchService.searchByCodeAndSite(CategoryModel.class, categoryCode, siteModel);
            filterData.setSelectedCategory(modelMapper.map(selectedCategory, CategoryData.class));
            // Don't add categories to searchParams - we'll filter manually
        }
        
        // Get all products matching criteria (without category filter)
        Collection<ProductModel> allProducts = searchService.search(ProductModel.class, searchParams, SearchOperator.AND);
        
        // Filter by category manually if needed
        List<ProductModel> allProductsList;
        if (selectedCategory != null) {
            CategoryModel finalSelectedCategory = selectedCategory;
            allProductsList = new ArrayList<>();
            for (ProductModel product : allProducts) {
                if (CollectionUtils.isNotEmpty(product.getCategories())) {
                    boolean hasCategory = product.getCategories().stream()
                            .anyMatch(cat -> cat.getId().equals(finalSelectedCategory.getId()));
                    if (hasCategory) {
                        allProductsList.add(product);
                    }
                }
            }
        } else {
            // Include ALL products, even those without categories
            allProductsList = new ArrayList<>(allProducts);
        }
        
        // Sort by ID for consistent ordering
        allProductsList.sort(Comparator.comparing(ProductModel::getId));
        
        // Manual pagination
        int totalProducts = allProductsList.size();
        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalProducts);
        
        List<ProductModel> paginatedProducts;
        if (fromIndex < totalProducts) {
            paginatedProducts = allProductsList.subList(fromIndex, toIndex);
        } else {
            paginatedProducts = new ArrayList<>();
        }
        
        // Map products to data
        List<ProductData> productDataList = paginatedProducts.stream()
                .map(product -> modelMapper.map(product, ProductData.class))
                .collect(Collectors.toList());
        
        filterData.setProducts(productDataList);
        filterData.setTotalProducts((long) totalProducts);
        filterData.setPageNumber(pageNumber + 1);
        filterData.setPageSize(pageSize);
        filterData.setTotalPages((int) Math.ceil((double) totalProducts / pageSize));
        
        // Get available categories from products in the result set (allProductsList)
        // This ensures only categories that have products are shown
        Set<CategoryModel> categorySet = new HashSet<>();
        for (ProductModel product : allProductsList) {
            if (CollectionUtils.isNotEmpty(product.getCategories())) {
                categorySet.addAll(product.getCategories().stream()
                        .filter(cat -> cat.getActive() != null && cat.getActive())
                        .collect(Collectors.toSet()));
            }
        }
        
        log.info("Category filter: {}, Products found: {}, Available categories: {}", 
                categoryCode, allProductsList.size(), categorySet.size());
        
        List<CategoryModel> sortedCategories = categorySet.stream()
                .sorted(Comparator.comparing(cat -> cat.getOrder() != null ? cat.getOrder() : Integer.MAX_VALUE))
                .collect(Collectors.toList());
        
        List<com.btc_store.domain.data.custom.CategoryData> categoryDataList = List.of(modelMapper.map(sortedCategories, com.btc_store.domain.data.custom.CategoryData[].class));
        filterData.setAvailableCategories(categoryDataList);
        
        return filterData;
    }
    
    @Override
    public ProductData getActiveProductByCode(String code) {
        SiteModel siteModel = siteService.getCurrentSite();
        Collection<ProductModel> productModel = searchService.search(ProductModel.class,
                Map.of(
                        StoreSiteBasedItemModel.Fields.site, siteModel,
                        "code", code,
                        "active", true,
                        "deleted", false
                ),
                SearchOperator.AND);
        
        if (productModel.isEmpty()) {
            throw new RuntimeException("Product not found or not active: " + code);
        }
        
        return modelMapper.map(productModel.iterator().next(), ProductData.class);
    }

    private void handleProductImages(ProductData productData, ProductModel productModel, 
                                     List<MultipartFile> imageFiles, List<MediaModel> existingImages,
                                     SiteModel siteModel) {
        if (Objects.nonNull(imageFiles) && !imageFiles.isEmpty()) {
            uploadAndSetNewImages(productData, productModel, imageFiles, existingImages, siteModel);
        } else {
            updateExistingImages(productData, productModel, existingImages);
        }
    }

    private void uploadAndSetNewImages(ProductData productData, ProductModel productModel, 
                                       List<MultipartFile> imageFiles, List<MediaModel> existingImages, 
                                       SiteModel siteModel) {
        try {
            CmsCategoryModel cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.PRODUCT.getValue(), siteModel);
            List<MediaModel> finalImages = new ArrayList<>();
            
            addExistingImagesInOrder(productData, existingImages, finalImages);
            uploadNewImages(imageFiles, cmsCategoryModel, siteModel, finalImages);
            
            productModel.setImages(finalImages);
            setMainImage(productData, productModel, finalImages);
            
            log.info("Total {} images processed", finalImages.size());
        } catch (Exception e) {
            log.error("Error uploading product images: {}", e.getMessage());
            throw new RuntimeException("Error uploading product images: " + e.getMessage());
        }
    }

    private void addExistingImagesInOrder(ProductData productData, List<MediaModel> existingImages, List<MediaModel> finalImages) {
        if (CollectionUtils.isNotEmpty(productData.getImageCodesInOrder())) {
            for (String imageCode : productData.getImageCodesInOrder()) {
                existingImages.stream()
                        .filter(img -> img.getCode().equals(imageCode))
                        .findFirst()
                        .ifPresent(finalImages::add);
            }
            log.info("{} existing images preserved", finalImages.size());
        }
    }

    private void uploadNewImages(List<MultipartFile> imageFiles, CmsCategoryModel cmsCategoryModel,
                                 SiteModel siteModel, List<MediaModel> finalImages) {
        for (MultipartFile imageFile : imageFiles) {
            if (!imageFile.isEmpty()) {
                MediaModel mediaModel = mediaService.storage(imageFile, false, cmsCategoryModel, siteModel);
                finalImages.add(mediaModel);
            }
        }
        log.info("{} new images uploaded", imageFiles.size());
    }

    private void updateExistingImages(ProductData productData, ProductModel productModel, List<MediaModel> existingImages) {
        if (Objects.nonNull(productData.getImageCodesInOrder())) {
            List<MediaModel> orderedImages = new ArrayList<>();
            for (String imageCode : productData.getImageCodesInOrder()) {
                existingImages.stream()
                        .filter(img -> img.getCode().equals(imageCode))
                        .findFirst()
                        .ifPresent(orderedImages::add);
            }
            productModel.setImages(orderedImages);
            log.info("Image order updated. New: {}, Original: {}", orderedImages.size(), existingImages.size());
            
            if (orderedImages.isEmpty()) {
                productModel.setMainImage(null);
                log.info("All images and main image cleared");
            }
        } else {
            productModel.setImages(existingImages);
        }
        
        if (!productModel.getImages().isEmpty()) {
            updateMainImageFromIndex(productData, productModel);
        }
    }

    private void setMainImage(ProductData productData, ProductModel productModel, List<MediaModel> images) {
        Integer mainImageIndex = productData.getMainImageIndex();
        if (mainImageIndex != null && mainImageIndex >= 0 && mainImageIndex < images.size()) {
            productModel.setMainImage(images.get(mainImageIndex));
        } else if (!images.isEmpty()) {
            productModel.setMainImage(images.get(0));
        }
    }

    private void updateMainImageFromIndex(ProductData productData, ProductModel productModel) {
        Integer mainImageIndex = productData.getMainImageIndex();
        if (mainImageIndex != null && mainImageIndex >= 0 && mainImageIndex < productModel.getImages().size()) {
            productModel.setMainImage(productModel.getImages().get(mainImageIndex));
            log.info("Main image updated to index: {}", mainImageIndex);
        } else if (Objects.isNull(productModel.getMainImage())) {
            productModel.setMainImage(productModel.getImages().get(0));
        }
    }
}
