package com.btc.domain.data.store.media;

import com.btc.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreBinaryMediaData extends BaseData {
    private String binary;
    private String mime;
    private String realFileName;
    private long size;
}
