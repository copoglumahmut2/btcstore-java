package com.btc_store.persistence.dao;

import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.SuccessStoryModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuccessStoryDao extends JpaRepository<SuccessStoryModel, Long> {

    SuccessStoryModel getByCodeAndSite(String code, SiteModel siteModel);

    List<SuccessStoryModel> getAllBySite(SiteModel siteModel);

    List<SuccessStoryModel> getAllBySiteAndActiveTrue(SiteModel siteModel);

    List<SuccessStoryModel> getAllBySiteOrderByOrderAsc(SiteModel siteModel);

    List<SuccessStoryModel> getAllBySiteAndActiveTrueOrderByOrderAsc(SiteModel siteModel);
}
