package com.btc.persistence.dao;

import com.btc.domain.model.custom.CmsCategoryModel;
import com.btc.domain.model.custom.CmsCategoryTypeModel;
import com.btc.domain.model.custom.SiteModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CmsCategoryDao extends JpaRepository<CmsCategoryModel, Long> {

    Set<CmsCategoryModel> findCmsCategoryModelByCmsCategoryType(CmsCategoryTypeModel cmsCategoryTypeModel);

    CmsCategoryModel getByCodeAndSite(String code, SiteModel siteModel);

    Set<CmsCategoryModel> getAllBySite(SiteModel siteModel);

    Set<CmsCategoryModel> findCmsCategoryModelsBySiteAndCmsCategoryType(SiteModel siteModel,CmsCategoryTypeModel cmsCategoryTypeModel);
}
