package com.btc_store.domain.data.store.role;

import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreUserRoleData extends BaseData {
    private LocalizeData description;
    private Boolean active;
}
