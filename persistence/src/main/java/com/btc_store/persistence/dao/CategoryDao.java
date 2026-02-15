package com.btc_store.persistence.dao;

import com.btc_store.domain.model.custom.CategoryModel;
import com.btc_store.domain.model.custom.SiteModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryDao extends JpaRepository<CategoryModel, Long> {

    CategoryModel getByCodeAndSite(String code, SiteModel siteModel);

    List<CategoryModel> getAllBySite(SiteModel siteModel);

    List<CategoryModel> getAllBySiteAndActiveTrue(SiteModel siteModel);

    List<CategoryModel> getAllBySiteOrderByOrderAsc(SiteModel siteModel);

    List<CategoryModel> getAllBySiteAndActiveTrueOrderByOrderAsc(SiteModel siteModel);
}
