package com.btc_store.domain.data.store;

import com.btc_store.domain.data.custom.CategoryData;
import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.custom.user.UserData;
import com.btc_store.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreProductData extends BaseData {

    private LocalizeData name;
    private LocalizeData description;
    private LocalizeData shortDescription;
    private List<CategoryData> categories = new ArrayList<>();
    private MediaData mainImage;
    private List<MediaData> images = new ArrayList<>();
    private UserData responsibleUser;
    private Boolean active;
}
