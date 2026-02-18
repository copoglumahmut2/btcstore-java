package com.btc_store.persistence.dao;

import com.btc_store.domain.model.custom.EmailTemplateModel;
import com.btc_store.domain.model.custom.SiteModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailTemplateDao extends JpaRepository<EmailTemplateModel, Long> {
    
    Optional<EmailTemplateModel> findByCodeAndSite(String code, SiteModel site);
    
    List<EmailTemplateModel> findBySiteAndActive(SiteModel site, Boolean isActive);
    
    List<EmailTemplateModel> findBySite(SiteModel site);
    
    Boolean existsByCodeAndSite(String code, SiteModel site);
}
