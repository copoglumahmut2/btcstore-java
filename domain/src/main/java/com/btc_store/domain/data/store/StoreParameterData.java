package com.btc_store.domain.data.store;

import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.extend.back.BackBaseData;
import com.btc_store.domain.enums.DataType;
import com.btc_store.domain.enums.ParameterType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreParameterData extends BackBaseData {

    private String value;
    private DataType dataType;
    private LocalizeData description;
    private ParameterType parameterType;
    private Boolean encrypt;
    private String encryptedValue;
}
