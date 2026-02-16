package com.btc_store.facade;

import com.btc_store.domain.data.custom.SectorData;
import com.btc_store.domain.data.custom.user.UserGroupData;

import java.util.List;

public interface UserGroupFacade {

    List<UserGroupData> getAllUserGroups();

    UserGroupData getUserGroupByCode(String code);

    UserGroupData saveUserGroup(UserGroupData userGroupData);

    void deleteUserGroup(String code);
}
