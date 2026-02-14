package com.btc.service.impl;

import com.btc.domain.model.custom.CmsCategoryModel;
import com.btc.domain.model.custom.CmsCategoryTypeModel;
import com.btc.domain.model.custom.SiteModel;
import com.btc.persistence.dao.CmsCategoryDao;
import com.btc.persistence.dao.CmsCategoryTypeDao;
import com.btc.service.CmsCategoryService;
import com.btc.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CmsCategoryServiceImpl implements CmsCategoryService {

    private final CmsCategoryDao cmsCategoryDao;
    private final CmsCategoryTypeDao cmsCategoryTypeDao;

    @Override
    public CmsCategoryTypeModel getCmsCategoryTypeByCode(String code, SiteModel siteModel) {
        Assert.notNull(code, "Code must not be null");
        Assert.notNull(siteModel, "Site must not be null");
        var cmsCategoryTypeModel = cmsCategoryTypeDao.getCmsCategoryTypeByCodeAndSite(code, siteModel);
        ServiceUtils.checkItemModelIsExist(cmsCategoryTypeModel, CmsCategoryTypeModel.class, siteModel, code);
        return cmsCategoryTypeModel;
    }

    @Override
    public Set<CmsCategoryModel> getCmsCategoryModels(SiteModel siteModel) {
        return cmsCategoryDao.getAllBySite(siteModel);
    }

    @Override
    public Set<CmsCategoryTypeModel> getCmsCategoryTypeModels(SiteModel siteModel) {
        return cmsCategoryTypeDao.getAllBySite(siteModel);
    }

    @Override
    public CmsCategoryModel getCmsCategoryByCode(String code, SiteModel siteModel) {
        Assert.notNull(code, "Code must not be null");
        var cmsCategoryModel = cmsCategoryDao.getByCodeAndSite(code, siteModel);
        ServiceUtils.checkItemModelIsExist(cmsCategoryModel, CmsCategoryModel.class, siteModel, code);
        return cmsCategoryModel;
    }

    @Override
    public Set<CmsCategoryModel> getCmsCategoryModelsByType(SiteModel siteModel, CmsCategoryTypeModel cmsCategoryType) {
        return cmsCategoryDao.findCmsCategoryModelsBySiteAndCmsCategoryType(siteModel, cmsCategoryType);
    }

}
