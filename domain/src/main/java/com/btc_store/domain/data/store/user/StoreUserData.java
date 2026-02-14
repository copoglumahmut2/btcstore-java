package com.btc_store.domain.data.store.user;

import com.btc_store.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreUserData extends BaseData {

    private String firstName;
    private String lastName;
    @EqualsAndHashCode.Include
    private String username;
    private String definedPassword;
    private String newPassword;
    private String newPasswordConfirm;
}
