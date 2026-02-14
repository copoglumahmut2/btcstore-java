package com.btc_store.service.interceptor.impl.user;

import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.domain.model.store.user.StoreUserModel;
import com.btc_store.service.exception.interceptor.InterceptorException;
import com.btc_store.service.interceptor.BeforeSaveInterceptor;
import com.btc_store.service.interceptor.Interceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
@BeforeSaveInterceptor(itemType = UserModel.class)
public class UserPhoneNumberInterceptor implements Interceptor<UserModel> {

    @Override
    public void invoke(UserModel userModel) throws InterceptorException {


        /**
         * Kullanıcı numaraları 11 hane olacak şekilde ayarlandı.Sondan 10 karakter alınıp
         * başına 0 ekliyoruz.
         */

        if (isNew(userModel) || isModified(userModel, StoreUserModel.Fields.phoneNumber)) {
            var number = userModel.getPhoneNumber();

            number = RegExUtils.removeAll(number, "[^\\d]");
            number = StringUtils.right(number, 10);
            if (StringUtils.length(number) != 10) {
                userModel.setPhoneNumber(StringUtils.EMPTY);

            } else {
                number = StringUtils.leftPad(number, StringUtils.length(number) + 1, "0");
                userModel.setPhoneNumber(number);
            }
        }

    }
}
