package com.btc_store.persistence.dao;

import com.btc_store.domain.model.custom.BannerModel;
import com.btc_store.domain.model.custom.SiteModel;;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerDao extends JpaRepository<BannerModel, Long> {

    BannerModel getByCodeAndSite(String code, SiteModel siteModel);

    List<BannerModel> getAllBySite(SiteModel siteModel);

    List<BannerModel> getAllBySiteAndActiveTrue(SiteModel siteModel);

    List<BannerModel> getAllBySiteOrderByOrderAsc(SiteModel siteModel);

    List<BannerModel> getAllBySiteAndActiveTrueOrderByOrderAsc(SiteModel siteModel);
}
