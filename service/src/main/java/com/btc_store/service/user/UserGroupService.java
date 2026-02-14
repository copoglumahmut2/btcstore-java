package com.btc_store.service.user;

import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;

import java.util.Set;

public interface UserGroupService {
    UserGroupModel getUserGroupModel(String code, SiteModel siteModel);
    UserGroupModel getUserGroupModelForBack(String code, SiteModel siteModel);
    Set<UserGroupModel> getUserGroupModels(SiteModel siteModel);
    Set<UserGroupModel> getUserGroupModelsByCodeIn(Set<String> codes, SiteModel siteModel);
}
