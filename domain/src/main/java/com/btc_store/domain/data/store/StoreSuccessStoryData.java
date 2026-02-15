package com.btc_store.domain.data.store;

import com.btc_store.domain.data.custom.SectorData;
import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreSuccessStoryData extends BaseData {

    private String company;
    private SectorData sector;
    private LocalizeData title;
    private LocalizeData htmlContent;
    private MediaData media;
    private String videoUrl;
    private List<String> results = new ArrayList<>();
    private Integer order;
    private Boolean active;
}
