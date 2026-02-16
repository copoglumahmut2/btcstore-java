package com.btc_store.facade;

import com.btc_store.domain.data.custom.user.UserData;

import java.util.List;

public interface UserFacade {
    List<UserData> getAllUsers();
    UserData getUserByCode(String code);
    UserData saveUser(UserData userData);
    void deleteUser(String code);
}
