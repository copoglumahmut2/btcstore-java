package com.btc_store.facade;

import com.btc_store.domain.data.custom.DashboardModuleData;
import com.btc_store.domain.enums.DashboardModuleType;

import java.util.List;

public interface DashboardModuleFacade {

    List<DashboardModuleData> getAllDashboardModules();

    List<DashboardModuleData> getActiveDashboardModules();

    List<DashboardModuleData> getAuthorizedDashboardModules();

    List<DashboardModuleData> getAuthorizedDashboardModulesByType(DashboardModuleType moduleType);
    
    List<DashboardModuleData> getAuthorizedDashboardModulesWithCounts(DashboardModuleType moduleType);

    DashboardModuleData getDashboardModuleByCode(String code);

    DashboardModuleData saveDashboardModule(DashboardModuleData dashboardModuleData);

    void deleteDashboardModule(String code);
}
