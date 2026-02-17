package com.btc_store.domain.data.store.search;

import com.btc_store.domain.data.custom.CategoryData;
import com.btc_store.domain.data.custom.ProductData;
import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.custom.user.UserData;
import com.btc_store.domain.data.extend.BaseLocalizedDescriptionData;
import com.btc_store.domain.data.extend.back.BackBaseData;
import com.btc_store.domain.enums.DataType;
import com.btc_store.domain.enums.ParameterType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreSearchResultData extends BackBaseData {

    private LocalizeData title;
    private LocalizeData subtitle;
    private LocalizeData buttonText;
    private String buttonLink;
    private MediaData media;
    private Integer order;
    private Boolean active;
    private Boolean showTitle;
    private Boolean showSubtitle;
    private Boolean showButton;
    private Object name;
    private LocalizeData description;
    private String backgroundColor;
    private String textColor;
    private String buttonBackgroundColor;
    private String buttonBorderColor;
    private String buttonTextColor;
    private Boolean showOnHomepage;
    private String company;
    private String industry;
    private Boolean showOnHome;
    private String value;
    private DataType dataType;
    private ParameterType parameterType;
    private Boolean encrypt;
    private String username;
    private String firstName;
    private String lastName;
    private Boolean deleted;
    private String email;
    private String phoneNumber;
    private Set<BaseLocalizedDescriptionData> userGroups;
    private MediaData picture;
    private LocalizeData shortDescription;
    private Set<CategoryData> categories;
    private Set<UserData> responsibleUsers;
    private MediaData mainImage;
    private List<MediaData> medias;
    private List<ProductData> products;
}
