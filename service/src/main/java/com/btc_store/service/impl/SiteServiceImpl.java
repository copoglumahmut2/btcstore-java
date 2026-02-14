package com.btc_store.service.impl;

import com.btc_store.domain.data.custom.login.JwtUserData;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.persistence.dao.SiteDao;
import com.btc_store.service.ModelService;
import com.btc_store.service.SearchService;
import com.btc_store.service.SiteService;
import com.btc_store.service.exception.model.ModelNotFoundException;
import com.btc_store.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import util.StoreWebUtils;

import java.util.List;
import java.util.Objects;

/**
 * Site Service Class
 *
 * @author mcatal
 * @version v1.0
 * @since 26.10.2021
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    protected final SiteDao siteDao;
    protected final ModelService modelService;
    protected final SearchService searchService;

    /**
     * GetSiteModel method returns {@link SiteModel} with site code.
     *
     * @param code site code for finding site model
     * @return {@link SiteModel} by site code.
     * It throws {@link ModelNotFoundException} when site model was null
     */
    @Override
    public SiteModel getSiteModel(String code) {
        Assert.notNull(code, "Code must not be null");
        var siteModel = siteDao.getByCode(code);
        ServiceUtils.checkItemModelIsExist(siteModel, SiteModel.class, siteModel, code);
        return siteModel;
    }

    @Override
    public SiteModel getSiteModelByDomain(String domain) {
        var siteModel = siteDao.getSiteModelByDomainsEquals(domain);
        ServiceUtils.checkItemModelIsExist(siteModel, SiteModel.class, siteModel, domain);
        return siteModel;
    }

    /**
     * GetSiteModels method returns all {@link List<SiteModel>}.
     *
     * @return {@link List<SiteModel>}.
     * It throws {@link ModelNotFoundException} when site model was null
     */
    @Override
    public List<SiteModel> getSiteModels() {
        var siteModels = siteDao.findAll();
        return siteModels;
    }

    @Override
    public SiteModel getCurrentSite() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) ||
                StringUtils.equalsIgnoreCase("anonymousUser", authentication.getName())) {
            var host = StoreWebUtils.getCurrentHttpRequest().getHeader(HttpHeaders.HOST);
            return this.getSiteModelByDomain(StringUtils.split(host, ":")[0]);
        }
        var jwtUserData = (JwtUserData) authentication.getDetails();
        return jwtUserData.getSite();
    }

    @Override
    public SiteModel getSiteModelForPessimisticLock(String code) {
        return siteDao.getSiteModelForPessimisticLock(code);
    }
}
