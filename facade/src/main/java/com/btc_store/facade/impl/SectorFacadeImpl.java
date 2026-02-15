package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.SectorData;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.SectorModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.SectorFacade;
import com.btc_store.service.*;
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
public class SectorFacadeImpl implements SectorFacade {

    private final SiteService siteService;
    private final ModelMapper modelMapper;
    private final ModelService modelService;
    private final SearchService searchService;

    @Override
    public List<SectorData> getAllSectors() {
        var siteModel = siteService.getCurrentSite();
        var sectorModels = searchService.search(SectorModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(sectorModels, SectorData[].class));
    }

    @Override
    public List<SectorData> getActiveSectors() {
        var siteModel = siteService.getCurrentSite();
        var sectorModels = searchService.search(SectorModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel,
                       "active", true),
                SearchOperator.AND);
        return List.of(modelMapper.map(sectorModels, SectorData[].class));
    }

    @Override
    public SectorData getSectorByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var sectorModel = searchService.searchByCodeAndSite(SectorModel.class, code, siteModel);
        return modelMapper.map(sectorModel, SectorData.class);
    }

    @Override
    public SectorData saveSector(SectorData sectorData) {
        var siteModel = siteService.getCurrentSite();
        SectorModel sectorModel;

        if (sectorData.isNew()) {
            sectorModel = modelMapper.map(sectorData, SectorModel.class);
            sectorModel.setCode(UUID.randomUUID().toString());
            sectorModel.setSite(siteModel);
        } else {
            sectorModel = searchService.searchByCodeAndSite(SectorModel.class, sectorData.getCode(), siteModel);
            modelMapper.map(sectorData, sectorModel);
        }

        var savedModel = modelService.save(sectorModel);
        return modelMapper.map(savedModel, SectorData.class);
    }

    @Override
    public void deleteSector(String code) {
        var siteModel = siteService.getCurrentSite();
        var sectorModel = searchService.searchByCodeAndSite(SectorModel.class, code, siteModel);
        modelService.remove(sectorModel);
    }
}
