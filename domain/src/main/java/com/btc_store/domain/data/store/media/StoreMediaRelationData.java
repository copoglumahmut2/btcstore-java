package com.btc_store.domain.data.store.media;

import com.btc_store.domain.data.extend.BaseData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.custom.media.MediaRelationData;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class StoreMediaRelationData extends BaseData {
    private MediaRelationData parent;
    private String field;
    private String relationField;
    @JsonIncludeProperties({"code"})
    private List<MediaData> medias;
}
