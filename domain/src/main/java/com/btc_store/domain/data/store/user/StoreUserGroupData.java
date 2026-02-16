package com.btc_store.domain.data.store.user;

import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreUserGroupData extends BaseData {
    private LocalizeData description;
}
