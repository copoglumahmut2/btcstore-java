package com.btc_store.persistence.dao;

import com.btc_store.domain.model.custom.CmsCategoryTypeModel;
import com.btc_store.domain.model.custom.SiteModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CmsCategoryTypeDao extends JpaRepository<CmsCategoryTypeModel, Long> {

    CmsCategoryTypeModel getCmsCategoryTypeByCodeAndSite(String code, SiteModel siteModel);

    Set<CmsCategoryTypeModel> getAllBySite(SiteModel siteModel);

}
