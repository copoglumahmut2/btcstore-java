package com.btc_store.domain.data.store;

import com.btc_store.domain.data.custom.MenuLinkItemData;
import com.btc_store.domain.data.custom.user.UserGroupData;
import com.btc_store.domain.data.extend.back.BackBaseData;
import com.btc_store.domain.data.store.localize.StoreLocalizeData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreMenuLinkItemData extends BackBaseData {

    private String code;
    private StoreLocalizeData name;
    private String icon;
    private Integer displayOrder;
    private Boolean isRoot;
    private Boolean active;
    private String url;
    private String parentMenuCode;
    private Set<UserGroupData> userGroups;
    private Set<MenuLinkItemData> subMenuLinkItems;
}
