package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.BannerData;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.BannerModel;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.BannerFacade;
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
public class BannerFacadeImpl implements BannerFacade {

    private final BannerService bannerService;
    private final SiteService siteService;
    private final ModelMapper modelMapper;
    private final ModelService modelService;
    private final SearchService searchService;
    private final MediaService mediaService;
    private final CmsCategoryService cmsCategoryService;

    @Override
    public List<BannerData> getAllBanners() {
        var siteModel = siteService.getCurrentSite();
        var bannerModels = searchService.search(BannerModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(bannerModels, BannerData[].class));
    }

    @Override
    public List<BannerData> getActiveBanners() {
        var siteModel = siteService.getCurrentSite();
        var bannerModels = searchService.search(BannerModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel,
                       "active", true),
                SearchOperator.AND);
        return List.of(modelMapper.map(bannerModels, BannerData[].class));
    }

    @Override
    public BannerData getBannerByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var bannerModel = searchService.searchByCodeAndSite(BannerModel.class, code, siteModel);
        return modelMapper.map(bannerModel, BannerData.class);
    }

    @Override
    public BannerData saveBanner(BannerData bannerData, MultipartFile mediaFile) {
        var siteModel = siteService.getCurrentSite();
        BannerModel bannerModel;
        MediaModel oldMedia = null;

        if (bannerData.isNew()) {
            bannerModel = modelMapper.map(bannerData, BannerModel.class);
            bannerModel.setCode(UUID.randomUUID().toString());
            bannerModel.setSite(siteModel);
        } else {
            bannerModel = searchService.searchByCodeAndSite(BannerModel.class, bannerData.getCode(), siteModel);
            oldMedia = bannerModel.getMedia();
            modelMapper.map(bannerData, bannerModel);
        }

        // Media dosyası geldiyse kaydet ve banner'a bağla
        if (Objects.nonNull(mediaFile) && !mediaFile.isEmpty()) {
            try {
                var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode("banner", siteModel);
                var mediaModel = mediaService.storage(mediaFile, false, cmsCategoryModel, siteModel);
                bannerModel.setMedia(mediaModel);

                // Eski media varsa silme işareti koy
                if (Objects.nonNull(oldMedia)) {
                    mediaService.flagMediaForDelete(oldMedia.getCode(), siteModel);
                }
            } catch (Exception e) {
                log.error("Error occurred while storing banner media: {}", e.getMessage());
                throw new RuntimeException("Error occurred while storing banner media: " + e.getMessage());
            }
        }

        var savedModel = modelService.save(bannerModel);
        return modelMapper.map(savedModel, BannerData.class);
    }

    @Override
    public void deleteBanner(String code) {
        var siteModel = siteService.getCurrentSite();
        var bannerModel = searchService.searchByCodeAndSite(BannerModel.class, code, siteModel);
        
        // Banner silinirken media'yı da silme işareti koy
        if (Objects.nonNull(bannerModel.getMedia())) {
            try {
                mediaService.flagMediaForDelete(bannerModel.getMedia().getCode(), siteModel);
            } catch (Exception e) {
                log.error("Error occurred while flagging banner media for delete: {}", e.getMessage());
            }
        }
        
        modelService.remove(bannerModel);
    }
}
