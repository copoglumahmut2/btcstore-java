package com.btc_store.domain.data.store.search;

import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.extend.BaseLocalizedDescriptionData;
import com.btc_store.domain.data.extend.back.BackBaseData;
import com.btc_store.domain.enums.DataType;
import com.btc_store.domain.enums.ParameterType;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreSearchResultData extends BackBaseData {

    private LocalizeData title;
    private LocalizeData subtitle;
    private LocalizeData buttonText;
    private MediaData media;
    private Integer order;
    private Boolean active;
    private Object name;
    private LocalizeData description;
    private String backgroundColor;
    private String textColor;
    private Boolean showButton;
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
}
