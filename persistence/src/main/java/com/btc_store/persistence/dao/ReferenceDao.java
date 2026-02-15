package com.btc_store.persistence.dao;

import com.btc_store.domain.model.custom.ReferenceModel;
import com.btc_store.domain.model.custom.SiteModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenceDao extends JpaRepository<ReferenceModel, Long> {

    ReferenceModel getByCodeAndSite(String code, SiteModel siteModel);

    List<ReferenceModel> getAllBySite(SiteModel siteModel);

    List<ReferenceModel> getAllBySiteAndActiveTrue(SiteModel siteModel);

    List<ReferenceModel> getAllBySiteOrderByOrderAsc(SiteModel siteModel);

    List<ReferenceModel> getAllBySiteAndActiveTrueOrderByOrderAsc(SiteModel siteModel);

    List<ReferenceModel> getAllBySiteAndActiveTrueAndShowOnHomeTrueOrderByOrderAsc(SiteModel siteModel);
}
