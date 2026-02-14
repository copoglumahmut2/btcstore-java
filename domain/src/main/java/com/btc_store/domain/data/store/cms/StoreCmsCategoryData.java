package com.btc_store.domain.data.store.cms;

import com.btc_store.domain.data.custom.cms.CmsCategoryTypeData;
import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreCmsCategoryData extends BaseData {

    private LocalizeData name;
    private CmsCategoryTypeData cmsCategoryType;
    private boolean root;
    private String bgColor;
    private String description;
}
