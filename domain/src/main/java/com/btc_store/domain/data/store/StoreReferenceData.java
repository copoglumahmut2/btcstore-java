package com.btc_store.domain.data.store;

import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreReferenceData extends BaseData {

    private String name;
    private MediaData media;
    private Integer order;
    private Boolean active;
    private Boolean showOnHome;
}
