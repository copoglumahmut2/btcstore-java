package com.btc_store.domain.data.store;

import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.extend.BaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreCategoryData extends BaseData {

    private LocalizeData name;
    private LocalizeData description;
    private MediaData media;
    private String backgroundColor;
    private String textColor;
    private Boolean showButton;
    private LocalizeData buttonText;
    private String buttonLink;
    private String buttonBackgroundColor;
    private String buttonBorderColor;
    private String buttonTextColor;
    private Boolean showOnHomepage;
    private Integer order;
    private Boolean active;
}
