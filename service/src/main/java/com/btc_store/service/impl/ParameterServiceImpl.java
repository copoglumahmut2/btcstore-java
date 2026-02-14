package com.btc_store.service.impl;

import com.btc_store.domain.model.custom.ParameterModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.persistence.dao.ParameterDao;
import com.btc_store.service.ParameterService;
import com.btc_store.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Parameter Service Class
 *
 * @author mcatal
 * @version v1.0
 * @since 21.10.2021
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ParameterServiceImpl implements ParameterService {

    protected final ParameterDao parameterDao;

    @Value("${aes.encrypt.key}")
    protected String key;
    @Value("${aes.encrypt.initVector}")
    protected String initVector;
    @Value("${aes.encrypt.algo}")
    protected String algo;

    /**
     * getParameterModel method returns {@link ParameterModel} with parameter code and site model.
     *
     * @param code      parameter code for finding parameter model
     * @param siteModel site model for finding parameter model
     * @return {@link ParameterModel} by parameter code and site
     */
    @Override
    public ParameterModel getParameterModel(String code, SiteModel siteModel) {
        ParameterModel parameterModel = parameterDao.getByCodeAndSite(code, siteModel);
        ServiceUtils.checkItemModelIsExist(parameterModel, ParameterModel.class, siteModel, code);
        return parameterModel;
    }

    /**
     * GetParameterModels method returns {@link List<ParameterModel>} list with site model.
     *
     * @param siteModel site model for finding parameter model
     * @return {@link List<ParameterModel>} by site
     */
    @Override
    public List<ParameterModel> getParameterModels(SiteModel siteModel) {
        return parameterDao.getBySite(siteModel);
    }

    @Override
    public Boolean existsByCodeAndValueAndSite(String code, String value, SiteModel siteModel) {
        return parameterDao.existsByCodeAndValueAndSite(code, value, siteModel);
    }

    @Override
    public String getValueByCode(String code, SiteModel siteModel) {
        return parameterDao.getValueByCodeAndSite(code,siteModel);
    }


}
