package com.btc_store.service;

import com.btc_store.domain.model.custom.SiteConfigurationModel;
import com.btc_store.domain.model.custom.SiteModel;

public interface SiteConfigurationService {

    SiteConfigurationModel getSiteConfiguration(SiteModel siteModel);

    SiteConfigurationModel saveSiteConfiguration(SiteConfigurationModel siteConfigurationModel);
}
