package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.user.UserData;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.UserFacade;
import com.btc_store.service.ModelService;
import com.btc_store.service.SearchService;
import com.btc_store.service.SiteService;
import com.btc_store.service.user.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserFacadeImpl implements UserFacade {

    private final SiteService siteService;
    private final ModelService modelService;
    private final ModelMapper modelMapper;
    private final SearchService searchService;
    private final PasswordEncoder passwordEncoder;
    private final UserGroupService userGroupService;

    @Override
    public List<UserData> getAllUsers() {
        var siteModel = siteService.getCurrentSite();
        var userModels = searchService.search(UserModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(userModels, UserData[].class));
    }

    @Override
    public UserData getUserByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var userModel = searchService.searchByCodeAndSite(UserModel.class, code, siteModel);
        return modelMapper.map(userModel, UserData.class);
    }

    @Override
    public UserData saveUser(UserData userData) {
        UserModel userModel;
        var siteModel = siteService.getCurrentSite();


        if (userData.isNew()) {
            userModel = modelService.create(UserModel.class);
            userModel.setSite(siteModel);
        } else {
            userModel = searchService.searchByCodeAndSite(UserModel.class, userData.getCode(), siteModel);
        }

        modelMapper.map(userData, userModel);

        Set<UserGroupModel> userGroups = new HashSet<>();
        if (CollectionUtils.isNotEmpty(userData.getUserGroups())) {
            for (var userGroup : userData.getUserGroups()) {
                userGroups.add(userGroupService.getUserGroupModel(userGroup.getCode(), siteModel));
            }
        }
        userModel.setUserGroups(userGroups);

        if (StringUtils.isNotEmpty(userData.getDefinedPassword())) {
            userModel.setPassword(passwordEncoder.encode(userData.getDefinedPassword()));
            userModel.setPasswordEncoded(Boolean.TRUE);
        } else {
            userModel.setPasswordEncoded(Boolean.TRUE);
        }

        var savedModel = modelService.save(userModel);
        return modelMapper.map(savedModel, UserData.class);
    }

    @Override
    public void deleteUser(String code) {
        var siteModel = siteService.getCurrentSite();
        var userModel = searchService.searchByCodeAndSite(UserModel.class, code, siteModel);
        userModel.setDeleted(true);
        userModel.setActive(false);
        modelService.save(userModel);
    }
}
