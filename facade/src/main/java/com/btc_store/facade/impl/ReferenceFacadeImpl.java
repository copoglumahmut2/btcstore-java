package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.ReferenceData;
import com.btc_store.domain.enums.MediaCategory;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.ReferenceModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.ReferenceFacade;
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
public class ReferenceFacadeImpl implements ReferenceFacade {

    private final SiteService siteService;
    private final ModelMapper modelMapper;
    private final ModelService modelService;
    private final SearchService searchService;
    private final MediaService mediaService;
    private final CmsCategoryService cmsCategoryService;

    @Override
    public List<ReferenceData> getAllReferences() {
        var siteModel = siteService.getCurrentSite();
        var referenceModels = searchService.search(ReferenceModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(referenceModels, ReferenceData[].class));
    }

    @Override
    public List<ReferenceData> getActiveReferences() {
        var siteModel = siteService.getCurrentSite();
        var referenceModels = searchService.search(ReferenceModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel,
                       "active", true),
                SearchOperator.AND);
        return List.of(modelMapper.map(referenceModels, ReferenceData[].class));
    }

    @Override
    public List<ReferenceData> getHomePageReferences() {
        var siteModel = siteService.getCurrentSite();
        var referenceModels = searchService.search(ReferenceModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel,
                       "active", true,
                       "showOnHome", true),
                SearchOperator.AND);
        return List.of(modelMapper.map(referenceModels, ReferenceData[].class));
    }

    @Override
    public ReferenceData getReferenceByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var referenceModel = searchService.searchByCodeAndSite(ReferenceModel.class, code, siteModel);
        return modelMapper.map(referenceModel, ReferenceData.class);
    }

    @Override
    public ReferenceData saveReference(ReferenceData referenceData, MultipartFile mediaFile, boolean removeMedia) {
        var siteModel = siteService.getCurrentSite();
        ReferenceModel referenceModel;
        MediaModel oldMedia = null;

        if (referenceData.isNew()) {
            referenceModel = modelMapper.map(referenceData, ReferenceModel.class);
            referenceModel.setCode(UUID.randomUUID().toString());
            referenceModel.setSite(siteModel);
        } else {
            referenceModel = searchService.searchByCodeAndSite(ReferenceModel.class, referenceData.getCode(), siteModel);
            oldMedia = referenceModel.getMedia();

            MediaModel mediaToKeep = referenceModel.getMedia();
            modelMapper.map(referenceData, referenceModel);

            if ((Objects.isNull(mediaFile) || mediaFile.isEmpty()) && !removeMedia) {
                referenceModel.setMedia(mediaToKeep);
            }
        }

        boolean hasNewMedia = Objects.nonNull(mediaFile) && !mediaFile.isEmpty();

        if (hasNewMedia) {
            try {
                var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.REFERENCE.getValue(), siteModel);
                var mediaModel = mediaService.storage(mediaFile, false, cmsCategoryModel, siteModel);
                referenceModel.setMedia(mediaModel);

                if (Objects.nonNull(oldMedia)) {
                    mediaService.flagMediaForDelete(oldMedia.getCode(), siteModel);
                }
            } catch (Exception e) {
                log.error("Error storing reference media: {}", e.getMessage());
                throw new RuntimeException("Error storing reference media: " + e.getMessage());
            }
        } else if (removeMedia && Objects.nonNull(oldMedia)) {
            try {
                mediaService.flagMediaForDelete(oldMedia.getCode(), siteModel);
                referenceModel.setMedia(null);
            } catch (Exception e) {
                log.error("Error removing reference media: {}", e.getMessage());
            }
        }

        var savedModel = modelService.save(referenceModel);
        return modelMapper.map(savedModel, ReferenceData.class);
    }

    @Override
    public void deleteReference(String code) {
        var siteModel = siteService.getCurrentSite();
        var referenceModel = searchService.searchByCodeAndSite(ReferenceModel.class, code, siteModel);

        if (Objects.nonNull(referenceModel.getMedia())) {
            try {
                mediaService.flagMediaForDelete(referenceModel.getMedia().getCode(), siteModel);
            } catch (Exception e) {
                log.error("Error occurred while flagging reference media for delete: {}", e.getMessage());
            }
        }
        
        modelService.remove(referenceModel);
    }
}
