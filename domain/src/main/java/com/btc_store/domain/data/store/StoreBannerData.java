package com.btc_store.domain.data.store;

import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreBannerData extends BaseData {

    private LocalizeData title;
    private LocalizeData subtitle;
    private LocalizeData buttonText;
    private String buttonLink;
    private MediaData media;
    private Integer order;
    private Boolean active;
}
