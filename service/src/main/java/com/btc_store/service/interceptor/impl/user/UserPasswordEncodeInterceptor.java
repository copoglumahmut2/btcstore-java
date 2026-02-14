package com.btc_store.service.interceptor.impl.user;


import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.domain.model.store.user.StoreUserModel;
import com.btc_store.service.interceptor.BeforeSaveInterceptor;
import com.btc_store.service.interceptor.Interceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@RequiredArgsConstructor
@BeforeSaveInterceptor(itemType = UserModel.class)
public class UserPasswordEncodeInterceptor implements Interceptor<UserModel> {

    protected final PasswordEncoder passwordEncoder;

    @Override
    public void invoke(UserModel userModel) throws com.btc_store.service.exception.interceptor.InterceptorException {

        if (isModified(userModel, StoreUserModel.Fields.password) && BooleanUtils.isFalse(userModel.isPasswordEncoded())) {
            userModel.setDefinedPassword(userModel.getPassword());
            userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
            userModel.setPasswordEncoded(Boolean.TRUE);
        }

    }
}
