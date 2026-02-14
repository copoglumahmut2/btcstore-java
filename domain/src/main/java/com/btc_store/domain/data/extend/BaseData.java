package com.btc_store.domain.data.extend;

import com.btc_store.domain.data.store.base.StoreBaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class BaseData extends StoreBaseData {
}

