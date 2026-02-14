package com.btc.domain.data.extend;

import com.btc.domain.data.store.base.StoreBaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class BaseData extends StoreBaseData {
}

