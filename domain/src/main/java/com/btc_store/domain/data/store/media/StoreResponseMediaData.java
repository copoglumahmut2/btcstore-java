package com.btc_store.domain.data.store.media;

import lombok.Data;

/**
 * @author hkaynar on 13.10.2021
 */
@Data
public class StoreResponseMediaData {
    private String fileName;
    private String url;
    private Integer uploaded;
}
