package com.btc_store.domain.data.custom;

import com.btc_store.domain.data.store.StoreCallRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class CallRequestData extends StoreCallRequestData {
}
