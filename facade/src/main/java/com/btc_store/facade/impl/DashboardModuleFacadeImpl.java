package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.DashboardModuleData;
import com.btc_store.domain.enums.DashboardModuleType;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.DashboardModuleModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.DashboardModuleFacade;
import com.btc_store.service.ModelService;
import com.btc_store.service.SearchService;
import com.btc_store.service.SiteService;
import com.btc_store.service.user.UserService;
import com.btc_store.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DashboardModuleFacadeImpl implements DashboardModuleFacade {

    private final SearchService searchService;
    private final ModelService modelService;
    private final SiteService siteService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public List<DashboardModuleData> getAllDashboardModules() {
        var siteModel = siteService.getCurrentSite();
        var dashboardModules = searchService.search(DashboardModuleModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(dashboardModules, DashboardModuleData[].class));
    }

    @Override
    public List<DashboardModuleData> getActiveDashboardModules() {
        var siteModel = siteService.getCurrentSite();
        var dashboardModules = searchService.search(DashboardModuleModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel,
                       "active", true),
                SearchOperator.AND);
        
        // displayOrder'a göre sırala
        var sortedModules = dashboardModules.stream()
                .sorted(Comparator.comparing(DashboardModuleModel::getDisplayOrder))
                .collect(Collectors.toList());
        
        return List.of(modelMapper.map(sortedModules, DashboardModuleData[].class));
    }

    @Override
    public List<DashboardModuleData> getAuthorizedDashboardModules() {
        var currentUser = userService.getCurrentUser();
        var authorities = userService.getCurrentUserAuthorities();
        var activeModules = getActiveDashboardModules();
        
        log.debug("User: {}, authorities: {}", currentUser.getUsername(), authorities);
        
        // SUPER_ADMIN tüm modülleri görebilir
        if (authorities.contains("SUPER_ADMIN")) {
            return activeModules;
        }
        
        // Kullanıcının gruplarını al
        var userGroupCodes = currentUser.getUserGroups().stream()
                .map(ug -> ug.getCode())
                .collect(Collectors.toSet());
        
        log.debug("User groups: {}", userGroupCodes);
        
        // UserGroup kontrolü ile yetkili modülleri filtrele
        return activeModules.stream()
                .filter(module -> {
                    // UserGroup tanımlı değilse herkese açık
                    if (module.getUserGroups() == null || module.getUserGroups().isEmpty()) {
                        return true;
                    }
                    
                    // Kullanıcının grubu modülün gruplarıyla eşleşmeli
                    return module.getUserGroups().stream()
                            .anyMatch(ug -> userGroupCodes.contains(ug.getCode()));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardModuleData> getAuthorizedDashboardModulesByType(DashboardModuleType moduleType) {
        var authorizedModules = getAuthorizedDashboardModules();
        
        return authorizedModules.stream()
                .filter(module -> moduleType.equals(module.getModuleType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardModuleData> getAuthorizedDashboardModulesWithCounts(DashboardModuleType moduleType) {
        var authorizedModules = getAuthorizedDashboardModulesByType(moduleType);
        
        // Her modül için count hesapla
        authorizedModules.forEach(module -> {
            if (module.getSearchItemType() != null && module.getShowCount() != null && module.getShowCount()) {
                try {
                    // Model class'ını bul
                    Class<?> modelClass = Class.forName("com.btc_store.domain.model.custom." + module.getSearchItemType());
                    
                    // SearchFilters'ı parse et
                    Map<String, Object> filters = new HashMap<>();
                    if (module.getSearchFilters() != null && !module.getSearchFilters().isEmpty()) {
                        // JSON parse etmek yerine basit bir yaklaşım - gerekirse ObjectMapper kullanılabilir
                        // Şimdilik boş filtre ile devam edelim
                    }
                    
                    // SearchService ile count al
                    var results = searchService.search(modelClass, filters, SearchOperator.AND);
                    module.setCount((long) results.size());
                    
                } catch (ClassNotFoundException e) {
                    log.error("Model class not found: {}", module.getSearchItemType(), e);
                    module.setCount(0L);
                } catch (Exception e) {
                    log.error("Error calculating count for module: {}", module.getCode(), e);
                    module.setCount(0L);
                }
            } else {
                module.setCount(0L);
            }
        });
        
        return authorizedModules;
    }

    @Override
    public DashboardModuleData getDashboardModuleByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var dashboardModule = searchService.searchByCodeAndSite(DashboardModuleModel.class, code, siteModel);
        return modelMapper.map(dashboardModule, DashboardModuleData.class);
    }

    @Override
    public DashboardModuleData saveDashboardModule(DashboardModuleData dashboardModuleData) {
        var siteModel = siteService.getCurrentSite();
        DashboardModuleModel dashboardModuleModel;

        if (dashboardModuleData.isNew()) {
            dashboardModuleModel = modelMapper.map(dashboardModuleData, DashboardModuleModel.class);
            ServiceUtils.generateCodeIfMissing(dashboardModuleModel);
            dashboardModuleModel.setSite(siteModel);
        } else {
            dashboardModuleModel = searchService.searchByCodeAndSite(DashboardModuleModel.class, 
                    dashboardModuleData.getCode(), siteModel);
            modelMapper.map(dashboardModuleData, dashboardModuleModel);
        }

        // UserGroup'ları set et
        if (dashboardModuleData.getUserGroups() != null) {
            Set<com.btc_store.domain.model.custom.user.UserGroupModel> userGroups = new HashSet<>();
            dashboardModuleData.getUserGroups().forEach(ugData -> {
                var userGroup = searchService.searchByCodeAndSite(
                        com.btc_store.domain.model.custom.user.UserGroupModel.class, 
                        ugData.getCode(), 
                        siteModel);
                userGroups.add(userGroup);
            });
            dashboardModuleModel.setUserGroups(userGroups);
        }

        var savedModel = modelService.save(dashboardModuleModel);
        return modelMapper.map(savedModel, DashboardModuleData.class);
    }

    @Override
    public void deleteDashboardModule(String code) {
        var siteModel = siteService.getCurrentSite();
        var dashboardModule = searchService.searchByCodeAndSite(DashboardModuleModel.class, code, siteModel);
        modelService.remove(dashboardModule);
    }
}
