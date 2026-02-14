package com.btc.service;

import com.btc.domain.model.custom.CmsCategoryModel;
import com.btc.domain.model.custom.CmsCategoryTypeModel;
import com.btc.domain.model.custom.SiteModel;

import java.util.Set;

public interface CmsCategoryService {

    CmsCategoryTypeModel getCmsCategoryTypeByCode(String code, SiteModel siteModel);

    Set<CmsCategoryModel> getCmsCategoryModels(SiteModel siteModel);

    Set<CmsCategoryTypeModel> getCmsCategoryTypeModels(SiteModel siteModel);

    CmsCategoryModel getCmsCategoryByCode(String code,SiteModel siteModel);

    Set<CmsCategoryModel> getCmsCategoryModelsByType(SiteModel siteModel,CmsCategoryTypeModel cmsCategoryType);


}
