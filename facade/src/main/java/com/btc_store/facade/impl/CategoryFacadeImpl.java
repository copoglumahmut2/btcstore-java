package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.CategoryData;
import com.btc_store.domain.enums.MediaCategory;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.CategoryModel;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.CategoryFacade;
import com.btc_store.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryFacadeImpl implements CategoryFacade {

    private final SiteService siteService;
    private final ModelMapper modelMapper;
    private final ModelService modelService;
    private final SearchService searchService;
    private final MediaService mediaService;
    private final CmsCategoryService cmsCategoryService;

    @Override
    public List<CategoryData> getAllCategories() {
        var siteModel = siteService.getCurrentSite();
        var categoryModels = searchService.search(CategoryModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(categoryModels, CategoryData[].class));
    }

    @Override
    public List<CategoryData> getActiveCategories() {
        var siteModel = siteService.getCurrentSite();
        var categoryModels = searchService.search(CategoryModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel,
                       "active", true),
                SearchOperator.AND);
        return List.of(modelMapper.map(categoryModels, CategoryData[].class));
    }

    @Override
    public CategoryData getCategoryByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var categoryModel = searchService.searchByCodeAndSite(CategoryModel.class, code, siteModel);
        return modelMapper.map(categoryModel, CategoryData.class);
    }

    @Override
    public CategoryData saveCategory(CategoryData categoryData, MultipartFile mediaFile, boolean removeMedia) {
        var siteModel = siteService.getCurrentSite();
        CategoryModel categoryModel;
        MediaModel oldMedia = null;

        if (categoryData.isNew()) {
            categoryModel = modelMapper.map(categoryData, CategoryModel.class);
            categoryModel.setCode(UUID.randomUUID().toString());
            categoryModel.setSite(siteModel);
        } else {
            categoryModel = searchService.searchByCodeAndSite(CategoryModel.class, categoryData.getCode(), siteModel);
            oldMedia = categoryModel.getMedia();

            MediaModel mediaToKeep = categoryModel.getMedia();
            modelMapper.map(categoryData, categoryModel);

            if ((Objects.isNull(mediaFile) || mediaFile.isEmpty()) && !removeMedia) {
                categoryModel.setMedia(mediaToKeep);
            }
        }

        boolean hasNewMedia = Objects.nonNull(mediaFile) && !mediaFile.isEmpty();

        if (hasNewMedia) {
            try {
                var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.CATEGORY.getValue(), siteModel);
                var mediaModel = mediaService.storage(mediaFile, false, cmsCategoryModel, siteModel);
                categoryModel.setMedia(mediaModel);

                if (Objects.nonNull(oldMedia)) {
                    mediaService.flagMediaForDelete(oldMedia.getCode(), siteModel);
                }
            } catch (Exception e) {
                log.error("Error storing category media: {}", e.getMessage());
                throw new RuntimeException("Error storing category media: " + e.getMessage());
            }
        } else if (removeMedia && Objects.nonNull(oldMedia)) {
            try {
                mediaService.flagMediaForDelete(oldMedia.getCode(), siteModel);
                categoryModel.setMedia(null);
            } catch (Exception e) {
                log.error("Error removing category media: {}", e.getMessage());
            }
        }

        var savedModel = modelService.save(categoryModel);
        return modelMapper.map(savedModel, CategoryData.class);
    }

    @Override
    public void deleteCategory(String code) {
        var siteModel = siteService.getCurrentSite();
        var categoryModel = searchService.searchByCodeAndSite(CategoryModel.class, code, siteModel);

        if (Objects.nonNull(categoryModel.getMedia())) {
            try {
                mediaService.flagMediaForDelete(categoryModel.getMedia().getCode(), siteModel);
            } catch (Exception e) {
                log.error("Error occurred while flagging category media for delete: {}", e.getMessage());
            }
        }
        
        modelService.remove(categoryModel);
    }
}
