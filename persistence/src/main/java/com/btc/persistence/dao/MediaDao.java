package com.btc.persistence.dao;

import com.btc.domain.model.custom.CmsCategoryModel;
import com.btc.domain.model.custom.MediaModel;
import com.btc.domain.model.custom.SiteModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MediaDao extends JpaRepository<MediaModel, Long> {

    Page<MediaModel> getMediaModelsByCmsCategoryAndSiteOrderByLastModifiedDateDesc(Pageable pageable, CmsCategoryModel cmsCategoryModel, SiteModel siteModel);

    MediaModel getMediaModelByCodeAndSite(String code,SiteModel siteModel);

    Set<MediaModel> getMediaModelsByDeletedAndSite(boolean deleted, SiteModel siteModel);
}
