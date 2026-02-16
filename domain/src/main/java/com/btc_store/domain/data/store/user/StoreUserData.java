package com.btc_store.domain.data.store.user;

import com.btc_store.domain.data.custom.localize.LanguageData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.custom.user.UserGroupData;
import com.btc_store.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreUserData extends BaseData {

    private String firstName;
    private String lastName;
    @EqualsAndHashCode.Include
    private String username;
    private String email;
    private String phoneNumber;
    private String description;
    private Boolean active;
    private Boolean deleted;
    private String definedPassword;
    private String newPassword;
    private String newPasswordConfirm;
    private MediaData picture;
    private LanguageData language;
    private Set<UserGroupData> userGroups;
}
