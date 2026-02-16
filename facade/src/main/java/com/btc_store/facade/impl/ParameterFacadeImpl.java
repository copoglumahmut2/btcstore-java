package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.ParameterData;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.ParameterModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.ParameterFacade;
import com.btc_store.service.ModelService;
import com.btc_store.service.ParameterService;
import com.btc_store.service.SearchService;
import com.btc_store.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParameterFacadeImpl implements ParameterFacade {

    private final ParameterService parameterService;
    private final SiteService siteService;
    private final ModelService modelService;
    private final ModelMapper modelMapper;
    private final SearchService searchService;

    @Override
    public List<ParameterData> getAllParameters() {
        var siteModel = siteService.getCurrentSite();
        var parameterModels =searchService.search(ParameterModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(parameterModels, ParameterData[].class));
    }

    @Override
    public ParameterData getParameterByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var parameterModel = searchService.searchByCodeAndSite(ParameterModel.class, code, siteModel);
        return modelMapper.map(parameterModel, ParameterData.class);
    }

    @Override
    public ParameterData saveParameter(ParameterData parameterData) {
        var siteModel = siteService.getCurrentSite();
        ParameterModel parameterModel;
        boolean isNew = parameterData.isNew();

        if (isNew) {
            parameterModel = modelMapper.map(parameterData, ParameterModel.class);
            parameterModel.setCode(UUID.randomUUID().toString());
            parameterModel.setSite(siteModel);
        } else {
            parameterModel = searchService.searchByCodeAndSite(ParameterModel.class, parameterData.getCode(), siteModel);
            modelMapper.map(parameterData, parameterModel);
        }

        var savedModel = modelService.save(parameterModel);
        return modelMapper.map(savedModel, ParameterData.class);
    }

    @Override
    public void deleteParameter(String code) {
        var siteModel = siteService.getCurrentSite();
        modelService.remove(searchService.searchByCodeAndSite(ParameterModel.class, code, siteModel));
    }
}
