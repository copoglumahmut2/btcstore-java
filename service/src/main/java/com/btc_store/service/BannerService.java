package com.btc_store.service;

import com.btc_store.domain.model.custom.BannerModel;
import com.btc_store.domain.model.custom.SiteModel;

import java.util.List;

public interface BannerService {

    BannerModel getBannerByCode(String code, SiteModel siteModel);

    List<BannerModel> getAllBanners(SiteModel siteModel);

    List<BannerModel> getActiveBanners(SiteModel siteModel);

    List<BannerModel> getAllBannersOrdered(SiteModel siteModel);

    List<BannerModel> getActiveBannersOrdered(SiteModel siteModel);

    BannerModel saveBanner(BannerModel bannerModel);

    void deleteBanner(BannerModel bannerModel);
}
