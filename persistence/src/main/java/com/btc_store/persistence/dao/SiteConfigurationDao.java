package com.btc_store.persistence.dao;

import com.btc_store.domain.model.custom.SiteConfigurationModel;
import com.btc_store.domain.model.custom.SiteModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteConfigurationDao extends JpaRepository<SiteConfigurationModel, Long> {

    Optional<SiteConfigurationModel> findBySite(SiteModel site);
}
