package com.btc_store.service.interceptor.impl.user;

import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.service.exception.interceptor.InterceptorException;
import com.btc_store.service.interceptor.BeforeSaveInterceptor;
import com.btc_store.service.interceptor.Interceptor;
import com.btc_store.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@BeforeSaveInterceptor(itemType = UserModel.class)
public class UserCodeInterceptor implements Interceptor<UserModel> {

    @Override
    public void invoke(UserModel userModel) throws InterceptorException {
        ServiceUtils.generateCodeIfMissing(userModel);
    }
}
