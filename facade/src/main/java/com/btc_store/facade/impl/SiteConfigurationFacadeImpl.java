package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.SiteConfigurationData;
import com.btc_store.domain.enums.MediaCategory;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.MenuLinkItemModel;
import com.btc_store.domain.model.custom.SiteConfigurationModel;
import com.btc_store.domain.model.custom.localize.Localized;
import com.btc_store.facade.SiteConfigurationFacade;
import com.btc_store.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SiteConfigurationFacadeImpl implements SiteConfigurationFacade {

    private final SiteConfigurationService siteConfigurationService;
    private final SiteService siteService;
    private final ModelMapper modelMapper;
    private final ModelService modelService;
    private final MediaService mediaService;
    private final CmsCategoryService cmsCategoryService;
    private final MenuLinkItemService menuLinkItemService;

    @Override
    public SiteConfigurationData getSiteConfiguration() {
        var siteModel = siteService.getCurrentSite();
        var configModel = siteConfigurationService.getSiteConfiguration(siteModel);
        
        if (Objects.isNull(configModel)) {
            log.info("SiteConfiguration model is null for site: {}", siteModel.getCode());
            return new SiteConfigurationData();
        }
        
        log.info("SiteConfiguration model found - topBannerEnabled: {}, topBannerText: {}", 
                configModel.getTopBannerEnabled(), 
                configModel.getTopBannerText());
        
        var data = modelMapper.map(configModel, SiteConfigurationData.class);
        
        log.info("SiteConfiguration data mapped - topBannerEnabled: {}, topBannerText: {}", 
                data.getTopBannerEnabled(), 
                data.getTopBannerText());
        
        return data;
    }

    @Override
    public SiteConfigurationData saveSiteConfiguration(SiteConfigurationData siteConfigurationData,
                                                       MultipartFile headerLogoFile,
                                                       MultipartFile footerLogoFile,
                                                       boolean removeHeaderLogo,
                                                       boolean removeFooterLogo) {
        var siteModel = siteService.getCurrentSite();
        SiteConfigurationModel configModel = siteConfigurationService.getSiteConfiguration(siteModel);
        
        MediaModel oldHeaderLogo = null;
        MediaModel oldFooterLogo = null;

        if (Objects.isNull(configModel)) {
            configModel = new SiteConfigurationModel();
            configModel.setSite(siteModel);
        } else {
            oldHeaderLogo = configModel.getHeaderLogo();
            oldFooterLogo = configModel.getFooterLogo();
        }

        configModel.setContactPhone(siteConfigurationData.getContactPhone());
        configModel.setShowContactPhone(siteConfigurationData.getShowContactPhone());
        configModel.setFooterEmail(siteConfigurationData.getFooterEmail());
        configModel.setFooterPhone(siteConfigurationData.getFooterPhone());
        configModel.setFooterAddress(siteConfigurationData.getFooterAddress());

        // Top Banner fields
        configModel.setTopBannerEnabled(siteConfigurationData.getTopBannerEnabled());
        configModel.setTopBannerBgColor(siteConfigurationData.getTopBannerBgColor());
        configModel.setTopBannerTextColor(siteConfigurationData.getTopBannerTextColor());
        configModel.setTopBannerLink(siteConfigurationData.getTopBannerLink());

        // ModelMapper LocalizeData -> Localized mapping
        if (Objects.nonNull(siteConfigurationData.getTopBannerText())) {
            var localizedText = modelMapper.map(siteConfigurationData.getTopBannerText(),
                    Localized.class);
            configModel.setTopBannerText(localizedText);
        } else {
            configModel.setTopBannerText(null);
        }

        boolean hasNewHeaderLogo = Objects.nonNull(headerLogoFile) && !headerLogoFile.isEmpty();
        if (hasNewHeaderLogo) {
            try {
                var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.LOGO.getValue(), siteModel);
                var mediaModel = mediaService.storage(headerLogoFile, false, cmsCategoryModel, siteModel);
                configModel.setHeaderLogo(mediaModel);

                if (Objects.nonNull(oldHeaderLogo)) {
                    mediaService.flagMediaForDelete(oldHeaderLogo.getCode(), siteModel);
                }
            } catch (Exception e) {
                log.error("Error storing header logo: {}", e.getMessage());
                throw new RuntimeException("Error storing header logo: " + e.getMessage());
            }
        } else if (removeHeaderLogo && Objects.nonNull(oldHeaderLogo)) {
            try {
                mediaService.flagMediaForDelete(oldHeaderLogo.getCode(), siteModel);
                configModel.setHeaderLogo(null);
            } catch (Exception e) {
                log.error("Error removing header logo: {}", e.getMessage());
            }
        }

        boolean hasNewFooterLogo = Objects.nonNull(footerLogoFile) && !footerLogoFile.isEmpty();
        if (hasNewFooterLogo) {
            try {
                var cmsCategoryModel = cmsCategoryService.getCmsCategoryByCode(MediaCategory.LOGO.getValue(), siteModel);
                var mediaModel = mediaService.storage(footerLogoFile, false, cmsCategoryModel, siteModel);
                configModel.setFooterLogo(mediaModel);

                if (Objects.nonNull(oldFooterLogo)) {
                    mediaService.flagMediaForDelete(oldFooterLogo.getCode(), siteModel);
                }
            } catch (Exception e) {
                log.error("Error storing footer logo: {}", e.getMessage());
                throw new RuntimeException("Error storing footer logo: " + e.getMessage());
            }
        } else if (removeFooterLogo && Objects.nonNull(oldFooterLogo)) {
            try {
                mediaService.flagMediaForDelete(oldFooterLogo.getCode(), siteModel);
                configModel.setFooterLogo(null);
            } catch (Exception e) {
                log.error("Error removing footer logo: {}", e.getMessage());
            }
        }

        if (Objects.nonNull(siteConfigurationData.getFooterMenus()) && !siteConfigurationData.getFooterMenus().isEmpty()) {
            Set<MenuLinkItemModel> footerMenuModels = siteConfigurationData.getFooterMenus().stream()
                    .map(menuData -> menuLinkItemService.getMenuByCode(menuData.getCode(), siteModel))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            configModel.setFooterMenus(footerMenuModels);
        } else {
            configModel.setFooterMenus(new HashSet<>());
        }

        var savedModel = modelService.save(configModel);
        return modelMapper.map(savedModel, SiteConfigurationData.class);
    }
}
