package com.btc_store.facade;

import com.btc_store.domain.data.custom.SiteConfigurationData;
import org.springframework.web.multipart.MultipartFile;

public interface SiteConfigurationFacade {

    SiteConfigurationData getSiteConfiguration();

    SiteConfigurationData saveSiteConfiguration(SiteConfigurationData siteConfigurationData, 
                                                MultipartFile headerLogoFile, 
                                                MultipartFile footerLogoFile,
                                                boolean removeHeaderLogo,
                                                boolean removeFooterLogo);
}
