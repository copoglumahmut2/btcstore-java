package com.btc.persistence.dao;

import com.btc.domain.model.custom.ParameterModel;
import com.btc.domain.model.custom.SiteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Parameter Dao Interface-{@link JpaRepository} implementation
 *
 * @author mcatal
 * @version v1.0
 * @since  21.10.2021
 */

public interface ParameterDao extends JpaRepository<ParameterModel, Long> {

    ParameterModel getByCodeAndSite(String code, SiteModel site);
    List<ParameterModel> getBySite(SiteModel site);
    Boolean existsByCodeAndValueAndSite(String code, String value, SiteModel site);
    @Query("Select p.value from ParameterModel p where p.code = :code and p.site = :site")
    String getValueByCodeAndSite(String code, SiteModel site);
}
