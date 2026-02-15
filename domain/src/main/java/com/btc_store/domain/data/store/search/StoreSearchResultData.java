package com.btc_store.domain.data.store.search;

import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.extend.back.BackBaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreSearchResultData extends BackBaseData {

    private LocalizeData title;
    private LocalizeData subtitle;
    private LocalizeData buttonText;
    private MediaData media;
    private Integer order;
    private Boolean active;

}
