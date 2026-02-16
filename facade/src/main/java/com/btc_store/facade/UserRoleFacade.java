package com.btc_store.facade;

import com.btc_store.domain.data.custom.role.UserRoleData;
import com.btc_store.domain.data.custom.user.UserGroupData;

import java.util.List;

public interface UserRoleFacade {

    List<UserRoleData> getAllUserRoles();

    UserRoleData getUserRoleByCode(String code);

    UserRoleData saveUserRole(UserRoleData userRoleData);

    void deleteUserRole(String code);
}
