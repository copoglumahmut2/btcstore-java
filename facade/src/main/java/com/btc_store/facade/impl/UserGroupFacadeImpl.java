package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.user.UserGroupData;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.domain.model.store.extend.StoreSiteBasedItemModel;
import com.btc_store.facade.UserGroupFacade;
import com.btc_store.service.SearchService;
import com.btc_store.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserGroupFacadeImpl implements UserGroupFacade {

    private final SearchService searchService;
    private final SiteService siteService;
    private final ModelMapper modelMapper;

    @Override
    public List<UserGroupData> getAllUserGroups() {
        SiteModel siteModel = siteService.getCurrentSite();
        var userGroupModels = searchService.search(UserGroupModel.class,
                Map.of(StoreSiteBasedItemModel.Fields.site, siteModel),
                SearchOperator.AND);
        return List.of(modelMapper.map(userGroupModels, UserGroupData[].class));
    }
}
