package com.btc_store.service.interceptor.impl.user;


import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.service.exception.interceptor.InterceptorException;
import com.btc_store.service.interceptor.BeforeSaveInterceptor;
import com.btc_store.service.interceptor.Interceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import util.StoreStringUtils;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@BeforeSaveInterceptor(itemType = UserModel.class)
public class UsernameLowercaseInterceptor implements Interceptor<UserModel> {

    @Override
    public void invoke(UserModel userModel) throws InterceptorException {

        var username = StoreStringUtils.clearTurkishCharacter(userModel.getUsername());
        userModel.setUsername(StringUtils.lowerCase(username, Locale.ENGLISH));
    }
}
