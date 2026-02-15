package com.btc_store.service.impl;

import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.SuccessStoryModel;
import com.btc_store.domain.model.store.StoreSuccessStoryModel;
import com.btc_store.persistence.dao.SuccessStoryDao;
import com.btc_store.service.SuccessStoryService;
import com.btc_store.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuccessStoryServiceImpl implements SuccessStoryService {

    private final SuccessStoryDao successStoryDao;

    @Override
    public SuccessStoryModel getSuccessStoryByCode(String code, SiteModel siteModel) {
        Assert.notNull(code, "Code must not be null");
        Assert.notNull(siteModel, "Site must not be null");
        var successStoryModel = successStoryDao.getByCodeAndSite(code, siteModel);
        ServiceUtils.checkItemModelIsExist(successStoryModel, SuccessStoryModel.class, siteModel, code);
        return successStoryModel;
    }

    @Override
    public List<SuccessStoryModel> getAllSuccessStories(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return successStoryDao.getAllBySite(siteModel);
    }

    @Override
    public List<SuccessStoryModel> getActiveSuccessStories(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return successStoryDao.getAllBySiteAndActiveTrue(siteModel);
    }

    @Override
    public List<SuccessStoryModel> getAllSuccessStoriesOrdered(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return successStoryDao.getAllBySiteOrderByOrderAsc(siteModel);
    }

    @Override
    public List<SuccessStoryModel> getActiveSuccessStoriesOrdered(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return successStoryDao.getAllBySiteAndActiveTrueOrderByOrderAsc(siteModel);
    }

    @Override
    public SuccessStoryModel saveSuccessStory(SuccessStoryModel successStoryModel) {
        Assert.notNull(successStoryModel, "Success story model must not be null");
        return successStoryDao.save(successStoryModel);
    }

    @Override
    public void deleteSuccessStory(SuccessStoryModel successStoryModel) {
        Assert.notNull(successStoryModel, "Success story model must not be null");
        successStoryDao.delete(successStoryModel);
    }
}
