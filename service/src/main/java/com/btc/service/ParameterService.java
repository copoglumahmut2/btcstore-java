package com.btc.service;

import com.btc.domain.model.custom.ParameterModel;
import com.btc.domain.model.custom.SiteModel;

import java.util.List;

/**
 * Parameter Service Interface
 *
 * @author mcatal
 * @version v1.0
 * @since 21.10.2021
 */


public interface ParameterService {
    ParameterModel getParameterModel(String code, SiteModel siteModel);

    List<ParameterModel> getParameterModels(SiteModel siteModel);

    Boolean existsByCodeAndValueAndSite(String code, String value, SiteModel siteModel);

    String getValueByCode(String code, SiteModel siteModel);
}
