package com.btc_store.domain.data.custom;

import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.custom.user.UserGroupData;
import com.btc_store.domain.data.extend.BaseData;
import com.btc_store.domain.enums.DashboardModuleType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class DashboardModuleData extends BaseData {

    private LocalizeData name;
    private LocalizeData description;
    private String link;
    private String icon;
    private Integer displayOrder;
    private Boolean active;
    private Boolean showCount;
    private String searchItemType;
    private String searchFilters;
    private DashboardModuleType moduleType;
    private Set<UserGroupData> userGroups;
    private Long count;
}
