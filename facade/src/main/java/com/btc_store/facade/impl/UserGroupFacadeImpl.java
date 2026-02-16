package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.user.UserGroupData;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.SectorModel;
import com.btc_store.domain.model.custom.role.UserRoleModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.UserGroupFacade;
import com.btc_store.service.ModelService;
import com.btc_store.service.SearchService;
import com.btc_store.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserGroupFacadeImpl implements UserGroupFacade {

    private final SearchService searchService;
    private final ModelService modelService;
    private final SiteService siteService;
    private final ModelMapper modelMapper;

    @Override
    public List<UserGroupData> getAllUserGroups() {
        var siteModel = siteService.getCurrentSite();
        var userGroupModels = searchService.search(UserGroupModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(userGroupModels, UserGroupData[].class));
    }

    @Override
    public UserGroupData getUserGroupByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var userGroupModel = searchService.searchByCodeAndSite(UserGroupModel.class, code, siteModel);
        return modelMapper.map(userGroupModel, UserGroupData.class);
    }

    @Override
    public UserGroupData saveUserGroup(UserGroupData userGroupData) {
        var siteModel = siteService.getCurrentSite();
        UserGroupModel userGroupModel;

        if (userGroupData.isNew()) {
            userGroupModel = modelMapper.map(userGroupData, UserGroupModel.class);
            userGroupModel.setCode(UUID.randomUUID().toString());
            userGroupModel.setSite(siteModel);
        } else {
            userGroupModel = searchService.searchByCodeAndSite(UserGroupModel.class, userGroupData.getCode(), siteModel);
            modelMapper.map(userGroupData, userGroupModel);
        }

        Set<UserRoleModel> userRoles = new HashSet<>();
        if (CollectionUtils.isNotEmpty(userGroupData.getUserRoles())) {
            userGroupData.getUserRoles().forEach(uR ->
                    userRoles.add(searchService.searchByCodeAndSite(UserRoleModel.class, uR.getCode(), siteModel)));
        }
        userGroupModel.setUserRoles(userRoles);

        var savedModel = modelService.save(userGroupModel);
        return modelMapper.map(savedModel, UserGroupData.class);
    }

    @Override
    public void deleteUserGroup(String code) {
        var siteModel = siteService.getCurrentSite();
        var userGroupModel = searchService.searchByCodeAndSite(UserGroupModel.class, code, siteModel);
        modelService.remove(userGroupModel);
    }
}
