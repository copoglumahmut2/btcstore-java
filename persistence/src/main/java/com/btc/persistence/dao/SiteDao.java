package com.btc.persistence.dao;

import com.btc.domain.model.custom.SiteModel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Site Dao Interface-{@link JpaRepository} implementation
 *
 * @author mcatal
 * @version v1.0
 * @since 26.10.2021
 */

public interface SiteDao extends JpaRepository<SiteModel, Long> {

    SiteModel getByCode(String code);

    SiteModel getSiteModelByDomainsEquals(String domain);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SiteModel s WHERE s.code = :code")
    SiteModel getSiteModelForPessimisticLock(@Param("code") String code);
}
