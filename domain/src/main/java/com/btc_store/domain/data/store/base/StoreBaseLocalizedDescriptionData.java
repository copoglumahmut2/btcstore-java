package com.btc_store.domain.data.store.base;

import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@RequiredArgsConstructor
public class StoreBaseLocalizedDescriptionData extends BaseData {
    private LocalizeData description;

}

