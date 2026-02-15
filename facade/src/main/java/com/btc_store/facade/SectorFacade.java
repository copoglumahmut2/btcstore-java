package com.btc_store.facade;

import com.btc_store.domain.data.custom.SectorData;

import java.util.List;

public interface SectorFacade {

    List<SectorData> getAllSectors();

    List<SectorData> getActiveSectors();

    SectorData getSectorByCode(String code);

    SectorData saveSector(SectorData sectorData);

    void deleteSector(String code);
}
