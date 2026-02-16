package com.btc_store.domain.data.extend;

import com.btc_store.domain.data.store.base.StoreBaseLocalizedDescriptionData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class BaseLocalizedDescriptionData extends StoreBaseLocalizedDescriptionData {
}

