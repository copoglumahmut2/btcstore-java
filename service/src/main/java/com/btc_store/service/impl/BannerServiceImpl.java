package com.btc_store.service.impl;

import com.btc_store.domain.model.custom.BannerModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.persistence.dao.BannerDao;
import com.btc_store.service.BannerService;
import com.btc_store.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerDao bannerDao;

    @Override
    public BannerModel getBannerByCode(String code, SiteModel siteModel) {
        Assert.notNull(code, "Code must not be null");
        Assert.notNull(siteModel, "Site must not be null");
        var bannerModel = bannerDao.getByCodeAndSite(code, siteModel);
        ServiceUtils.checkItemModelIsExist(bannerModel, BannerModel.class, siteModel, code);
        return bannerModel;
    }

    @Override
    public List<BannerModel> getAllBanners(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return bannerDao.getAllBySite(siteModel);
    }

    @Override
    public List<BannerModel> getActiveBanners(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return bannerDao.getAllBySiteAndActiveTrue(siteModel);
    }

    @Override
    public List<BannerModel> getAllBannersOrdered(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return bannerDao.getAllBySiteOrderByOrderAsc(siteModel);
    }

    @Override
    public List<BannerModel> getActiveBannersOrdered(SiteModel siteModel) {
        Assert.notNull(siteModel, "Site must not be null");
        return bannerDao.getAllBySiteAndActiveTrueOrderByOrderAsc(siteModel);
    }

    @Override
    public BannerModel saveBanner(BannerModel bannerModel) {
        Assert.notNull(bannerModel, "Banner model must not be null");
        return bannerDao.save(bannerModel);
    }

    @Override
    public void deleteBanner(BannerModel bannerModel) {
        Assert.notNull(bannerModel, "Banner model must not be null");
        bannerDao.delete(bannerModel);
    }
}
