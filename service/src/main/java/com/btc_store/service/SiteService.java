package com.btc_store.service;

import com.btc_store.domain.model.custom.SiteModel;

import java.util.List;

/**
 * Site Service Interface
 *
 * @author mcatal
 * @version v1.0
 * @since 26.10.2021
 */

public interface SiteService {
    SiteModel getSiteModel(String code);

    SiteModel getSiteModelByDomain(String domain);

    List<SiteModel> getSiteModels();
    SiteModel getCurrentSite();

    SiteModel getSiteModelForPessimisticLock(String code);

}
