package com.btc_store.facade;

import com.btc_store.domain.data.custom.user.UserGroupData;

import java.util.List;

public interface UserGroupFacade {

    List<UserGroupData> getAllUserGroups();
}
