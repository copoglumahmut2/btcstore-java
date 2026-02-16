package com.btc_store.domain.data.store;

import com.btc_store.domain.data.custom.ProductData;
import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.extend.BaseData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class StoreDocumentData extends BaseData {

    @Serial
    private static final long serialVersionUID = 1L;

    private LocalizeData title;
    private LocalizeData description;
    private List<ProductData> products;
    private List<MediaData> medias;
    private Boolean active;
    private Boolean deleted;
}
