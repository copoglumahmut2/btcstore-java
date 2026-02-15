package com.btc_store.service.impl;

import com.btc_store.domain.model.custom.ReferenceModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.persistence.dao.ReferenceDao;
import com.btc_store.service.ReferenceService;
import com.btc_store.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReferenceServiceImpl implements ReferenceService {

    private final ReferenceDao referenceDao;

    @Override
    public ReferenceModel getReferenceByCode(String code, SiteModel siteModel) {
        Assert.notNull(code, "Code must not be null");
        Assert.notNull(siteModel, "Site must not be null");
        var referenceModel = referenceDao.getByCodeAndSite(code, siteModel);
        ServiceUtils.checkItemModelIsExist(referenceModel, ReferenceModel.class, siteModel, code);
        return referenceModel;
    }

    @Override
    public List<ReferenceModel> getAllReferences(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return referenceDao.getAllBySite(siteModel);
    }

    @Override
    public List<ReferenceModel> getActiveReferences(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return referenceDao.getAllBySiteAndActiveTrue(siteModel);
    }

    @Override
    public List<ReferenceModel> getAllReferencesOrdered(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return referenceDao.getAllBySiteOrderByOrderAsc(siteModel);
    }

    @Override
    public List<ReferenceModel> getActiveReferencesOrdered(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return referenceDao.getAllBySiteAndActiveTrueOrderByOrderAsc(siteModel);
    }

    @Override
    public List<ReferenceModel> getHomePageReferences(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return referenceDao.getAllBySiteAndActiveTrueAndShowOnHomeTrueOrderByOrderAsc(siteModel);
    }

    @Override
    public ReferenceModel saveReference(ReferenceModel referenceModel) {
        Assert.notNull(referenceModel, "Reference model must not be null");
        return referenceDao.save(referenceModel);
    }

    @Override
    public void deleteReference(ReferenceModel referenceModel) {
        Assert.notNull(referenceModel, "Reference model must not be null");
        referenceDao.delete(referenceModel);
    }
}
