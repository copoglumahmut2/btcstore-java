package com.btc_store.service.interceptor.impl.user;

import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.service.SearchService;
import com.btc_store.service.constant.ServiceConstant;
import com.btc_store.service.exception.interceptor.InterceptorException;
import com.btc_store.service.interceptor.BeforeSaveInterceptor;
import com.btc_store.service.interceptor.Interceptor;
import com.btc_store.service.user.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@BeforeSaveInterceptor(itemType = UserModel.class)
public class UserEveryOneRoleInterceptor implements Interceptor<UserModel> {

    protected final UserGroupService userGroupService;
    protected final SearchService searchService;

    @Override
    public void invoke(UserModel userModel) throws InterceptorException {

        Set<UserGroupModel> userGroups = CollectionUtils.isEmpty(userModel.getUserGroups()) ? new HashSet<>()
                : userModel.getUserGroups();

        var everyOneUserGroupModel = userGroupService.getUserGroupModel(ServiceConstant.EVERYONE_USER_GROUP, userModel.getSite());

        userGroups.add(everyOneUserGroupModel);
        userModel.setUserGroups(userGroups);
    }
}
