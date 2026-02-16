package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.role.UserRoleData;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.role.UserRoleModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.UserRoleFacade;
import com.btc_store.service.ModelService;
import com.btc_store.service.SearchService;
import com.btc_store.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRoleFacadeImpl implements UserRoleFacade {

    private final SearchService searchService;
    private final ModelService modelService;
    private final SiteService siteService;
    private final ModelMapper modelMapper;

    @Override
    public List<UserRoleData> getAllUserRoles() {
        var siteModel = siteService.getCurrentSite();
        var userRoleModels = searchService.search(UserRoleModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(userRoleModels, UserRoleData[].class));
    }

    @Override
    public UserRoleData getUserRoleByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var userRoleModel = searchService.searchByCodeAndSite(UserRoleModel.class, code, siteModel);
        return modelMapper.map(userRoleModel, UserRoleData.class);
    }

    @Override
    public UserRoleData saveUserRole(UserRoleData userRoleData) {
        var siteModel = siteService.getCurrentSite();
        UserRoleModel userRoleModel;

        if (userRoleData.isNew()) {
            userRoleModel = modelMapper.map(userRoleData, UserRoleModel.class);
            userRoleModel.setCode(UUID.randomUUID().toString());
            userRoleModel.setSite(siteModel);
        } else {
            userRoleModel = searchService.searchByCodeAndSite(UserRoleModel.class, userRoleData.getCode(), siteModel);
            modelMapper.map(userRoleData, userRoleModel);
        }

        var savedModel = modelService.save(userRoleModel);
        return modelMapper.map(savedModel, UserRoleData.class);
    }

    @Override
    public void deleteUserRole(String code) {
        var siteModel = siteService.getCurrentSite();
        var userRoleModel = searchService.searchByCodeAndSite(UserRoleModel.class, code, siteModel);
        modelService.remove(userRoleModel);
    }
}
