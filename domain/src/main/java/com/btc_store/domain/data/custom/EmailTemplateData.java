package com.btc_store.domain.data.custom;

import com.btc_store.domain.data.store.StoreEmailTemplateData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class EmailTemplateData extends StoreEmailTemplateData {
}
