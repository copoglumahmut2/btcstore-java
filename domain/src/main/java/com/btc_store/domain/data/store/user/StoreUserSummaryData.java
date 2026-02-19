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
public class StoreUserSummaryData extends BaseData {

    private String firstName;
    private String lastName;
    @EqualsAndHashCode.Include
    private String username;
    private String email;
    private MediaData picture;
}
