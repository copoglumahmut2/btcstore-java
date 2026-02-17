package com.btc_store.service.impl;

import com.btc_store.domain.model.custom.SiteConfigurationModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.persistence.dao.SiteConfigurationDao;
import com.btc_store.service.SiteConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
@RequiredArgsConstructor
public class SiteConfigurationServiceImpl implements SiteConfigurationService {

    private final SiteConfigurationDao siteConfigurationDao;

    @Override
    public SiteConfigurationModel getSiteConfiguration(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return siteConfigurationDao.findBySite(siteModel).orElse(null);
    }

    @Override
    public SiteConfigurationModel saveSiteConfiguration(SiteConfigurationModel siteConfigurationModel) {
        Assert.notNull(siteConfigurationModel, "Site configuration model must not be null");
        return siteConfigurationDao.save(siteConfigurationModel);
    }
}
