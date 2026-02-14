package com.btc.persistence.dao;

import com.btc.domain.model.custom.CmsCategoryTypeModel;
import com.btc.domain.model.custom.SiteModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CmsCategoryTypeDao extends JpaRepository<CmsCategoryTypeModel, Long> {

    CmsCategoryTypeModel getCmsCategoryTypeByCodeAndSite(String code, SiteModel siteModel);

    Set<CmsCategoryTypeModel> getAllBySite(SiteModel siteModel);

}
