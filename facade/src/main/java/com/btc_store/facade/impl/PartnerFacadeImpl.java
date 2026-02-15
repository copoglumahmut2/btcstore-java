package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.PartnerData;
import com.btc_store.domain.enums.MediaCategory;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.PartnerModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.PartnerFacade;
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
public class PartnerFacadeImpl implements PartnerFacade {

    private final SiteService siteService;
    private final ModelMapper modelMapper;
    private final ModelService modelService;
    private final SearchService searchService;
    private final MediaService mediaService;
    private final CmsCategoryService cmsCategoryService;

    @Override
    public List<PartnerData> getAllPartners() {
        var siteModel = siteService.getCurrentSite();
        var partnerModels = searchService.search(PartnerModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(partnerModels, PartnerData[].class));
    }

    @Override
    public List<PartnerData> getActivePartners() {
        var siteModel = siteService.getCurrentSite();
        var partnerModels = searchService.search(PartnerModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel,
                       "active", true),
                SearchOperator.AND);
        return List.of(modelMapper.map(partnerModels, PartnerData[].class));
    }

    @Override
    public List<PartnerData> getHomePagePartners() {
        var siteModel = siteService.getCurrentSite();
        var partnerModels = searchService.search(PartnerModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel,
                       "active", true,
                       "showOnHome", true),
                SearchOperator.AND);
        return List.of(modelMapper.map(partnerModels, PartnerData[].class));
    }

    @Override
    public PartnerData getPartnerByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var partnerModel = searchService.searchByCodeAndSite(PartnerModel.class, code, siteModel);
        return modelMapper.map(partnerModel, PartnerData.class);
    }

    @Override
    public PartnerData savePartner(PartnerData partnerData, MultipartFile mediaFile, boolean removeMedia) {
        var siteModel = siteService.getCurrentSite();
        PartnerModel partnerModel;
        MediaModel oldMedia = null;

        if (partnerData.isNew()) {
            partnerModel = modelMapper.map(partnerData, PartnerModel.class);
            partnerModel.setCode(UUID.randomUUID().toString());
            partnerModel.setSite(siteModel);
        } else {
            partnerModel = searchService.searchByCodeAndSite(PartnerModel.class, partnerData.getCode(), siteModel);
            oldMedia = partnerModel.getMedia();
            
            MediaModel mediaToKeep = partnerModel.getMedia();
            modelMapper.map(partnerData, partnerModel);
            
            if ((Objects.isNull(mediaFile) || mediaFile.isEmpty()) && !removeMedia) {
                partnerModel.setMedia(mediaToKeep);
            }
        }

        boolean hasNewMedia = Objects.nonNull(mediaFile) && !mediaFile.isEmpty();

        if (hasNewMedia) {
            try {
                var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.PARTNER.getValue(), siteModel);
                var mediaModel = mediaService.storage(mediaFile, false, cmsCategoryModel, siteModel);
                partnerModel.setMedia(mediaModel);

                if (Objects.nonNull(oldMedia)) {
                    mediaService.flagMediaForDelete(oldMedia.getCode(), siteModel);
                }
            } catch (Exception e) {
                log.error("Error storing partner media: {}", e.getMessage());
                throw new RuntimeException("Error storing partner media: " + e.getMessage());
            }
        } else if (removeMedia && Objects.nonNull(oldMedia)) {
            try {
                mediaService.flagMediaForDelete(oldMedia.getCode(), siteModel);
                partnerModel.setMedia(null);
            } catch (Exception e) {
                log.error("Error removing partner media: {}", e.getMessage());
            }
        }

        var savedModel = modelService.save(partnerModel);
        return modelMapper.map(savedModel, PartnerData.class);
    }

    @Override
    public void deletePartner(String code) {
        var siteModel = siteService.getCurrentSite();
        var partnerModel = searchService.searchByCodeAndSite(PartnerModel.class, code, siteModel);

        if (Objects.nonNull(partnerModel.getMedia())) {
            try {
                mediaService.flagMediaForDelete(partnerModel.getMedia().getCode(), siteModel);
            } catch (Exception e) {
                log.error("Error occurred while flagging partner media for delete: {}", e.getMessage());
            }
        }
        
        modelService.remove(partnerModel);
    }
}
