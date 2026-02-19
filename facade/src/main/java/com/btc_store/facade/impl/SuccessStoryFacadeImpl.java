package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.SuccessStoryData;
import com.btc_store.domain.enums.MediaCategory;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.SectorModel;
import com.btc_store.domain.model.custom.SuccessStoryModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.SuccessStoryFacade;
import com.btc_store.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class SuccessStoryFacadeImpl implements SuccessStoryFacade {

    private final SiteService siteService;
    private final ModelMapper modelMapper;
    private final ModelService modelService;
    private final SearchService searchService;
    private final MediaService mediaService;
    private final CmsCategoryService cmsCategoryService;

    @Override
    public List<SuccessStoryData> getAllSuccessStories() {
        var siteModel = siteService.getCurrentSite();
        var successStoryModels = searchService.search(SuccessStoryModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(successStoryModels, SuccessStoryData[].class));
    }

    @Override
    public List<SuccessStoryData> getActiveSuccessStories() {
        var siteModel = siteService.getCurrentSite();
        var successStoryModels = searchService.search(SuccessStoryModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel,
                       "active", true),
                SearchOperator.AND);
        return List.of(modelMapper.map(successStoryModels, SuccessStoryData[].class));
    }

    @Override
    public SuccessStoryData getSuccessStoryByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var successStoryModel = searchService.searchByCodeAndSite(SuccessStoryModel.class, code, siteModel);
        return modelMapper.map(successStoryModel, SuccessStoryData.class);
    }

    @Override
    public SuccessStoryData saveSuccessStory(SuccessStoryData successStoryData, MultipartFile mediaFile, boolean removeMedia) {
        var siteModel = siteService.getCurrentSite();
        SuccessStoryModel successStoryModel;
        MediaModel oldMedia = null;

        if (successStoryData.isNew()) {
            successStoryModel = modelMapper.map(successStoryData, SuccessStoryModel.class);
            successStoryModel.setCode(UUID.randomUUID().toString());
            successStoryModel.setSite(siteModel);
        } else {
            successStoryModel = searchService.searchByCodeAndSite(SuccessStoryModel.class, successStoryData.getCode(), siteModel);
            oldMedia = successStoryModel.getMedia();
            
            MediaModel mediaToKeep = successStoryModel.getMedia();

            // Update basic fields
            if (StringUtils.isNotEmpty(successStoryData.getCompany())) {
                successStoryModel.setCompany(successStoryData.getCompany());
            }
            
            if (Objects.nonNull(successStoryData.getTitle())) {
                successStoryModel.setTitle(successStoryData.getTitle());
            }
            
            if (Objects.nonNull(successStoryData.getHtmlContent())) {
                successStoryModel.setHtmlContent(successStoryData.getHtmlContent());
            }
            
            if (StringUtils.isNotEmpty(successStoryData.getVideoUrl())) {
                successStoryModel.setVideoUrl(successStoryData.getVideoUrl());
            }
            
            successStoryModel.setOrder(successStoryData.getOrder());
            successStoryModel.setActive(successStoryData.isActive());

            // Update results
            successStoryModel.getResults().clear();
            if (successStoryData.getResults() != null && !successStoryData.getResults().isEmpty()) {
                successStoryModel.getResults().addAll(successStoryData.getResults());
            }
            
            if ((Objects.isNull(mediaFile) || mediaFile.isEmpty()) && !removeMedia) {
                successStoryModel.setMedia(mediaToKeep);
            }
        }

        SectorModel sectorModel = null;
        if (Objects.nonNull(successStoryData.getSector()) && StringUtils.isNotEmpty(successStoryData.getSector().getCode())) {
            sectorModel = searchService.searchByCodeAndSite(SectorModel.class, successStoryData.getSector().getCode(), siteModel);
        }
        successStoryModel.setSector(sectorModel);

        boolean hasNewMedia = Objects.nonNull(mediaFile) && !mediaFile.isEmpty();

        if (hasNewMedia) {
            try {
                var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.SUCCESS_STORY.getValue(), siteModel);
                var mediaModel = mediaService.storage(mediaFile, false, cmsCategoryModel, siteModel);
                successStoryModel.setMedia(mediaModel);

                if (Objects.nonNull(oldMedia)) {
                    mediaService.flagMediaForDelete(oldMedia.getCode(), siteModel);
                }
            } catch (Exception e) {
                log.error("Error storing success story media: {}", e.getMessage());
                throw new RuntimeException("Error storing success story media: " + e.getMessage());
            }
        } else if (removeMedia && Objects.nonNull(oldMedia)) {
            try {
                mediaService.flagMediaForDelete(oldMedia.getCode(), siteModel);
                successStoryModel.setMedia(null);
            } catch (Exception e) {
                log.error("Error removing success story media: {}", e.getMessage());
            }
        }

        var savedModel = modelService.save(successStoryModel);
        return modelMapper.map(savedModel, SuccessStoryData.class);
    }

    @Override
    public void deleteSuccessStory(String code) {
        var siteModel = siteService.getCurrentSite();
        var successStoryModel = searchService.searchByCodeAndSite(SuccessStoryModel.class, code, siteModel);

        if (Objects.nonNull(successStoryModel.getMedia())) {
            try {
                mediaService.flagMediaForDelete(successStoryModel.getMedia().getCode(), siteModel);
            } catch (Exception e) {
                log.error("Error occurred while flagging success story media for delete: {}", e.getMessage());
            }
        }
        
        modelService.remove(successStoryModel);
    }
}
