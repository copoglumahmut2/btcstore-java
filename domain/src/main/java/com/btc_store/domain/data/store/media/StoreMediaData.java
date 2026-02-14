package com.btc_store.domain.data.store.media;

import com.btc_store.domain.data.custom.cms.CmsCategoryData;
import com.btc_store.domain.data.extend.BaseData;
import com.btc_store.domain.enums.MediaProcessType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreMediaData extends BaseData {
    private String realFileName;

    private String mime;
    private String absolutePath;
    @JsonIgnore
    private MediaProcessType mediaProcessType;
    private CmsCategoryData cmsCategory;
    private long size;
    private Date createdDate;
    private boolean deleted;
}
