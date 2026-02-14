package com.btc.domain.data.store.media;

import com.btc.domain.data.extend.BaseData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreUploadMediaData extends BaseData {
    private String realFileName;
    private long size;
    private boolean deleted;
}
