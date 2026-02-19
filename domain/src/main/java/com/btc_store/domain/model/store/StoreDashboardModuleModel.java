package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.enums.DashboardModuleType;
import com.btc_store.domain.model.custom.extend.CodeBasedItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.localize.Localized;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.util.Set;

@Entity
@Table(name = DomainConstant.DASHBOARD_MODULE_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.DASHBOARD_MODULE_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreDashboardModuleModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "name_tr", length = 255)),
            @AttributeOverride(name = "en", column = @Column(name = "name_en", length = 255)),
            @AttributeOverride(name = "de", column = @Column(name = "name_de", length = 255)),
            @AttributeOverride(name = "fr", column = @Column(name = "name_fr", length = 255)),
            @AttributeOverride(name = "es", column = @Column(name = "name_es", length = 255)),
            @AttributeOverride(name = "it", column = @Column(name = "name_it", length = 255))
    })
    private Localized name;

    @Column(name = "link", nullable = false, length = 255)
    private String link;

    @Column(name = "icon", nullable = false, length = 50)
    private String icon;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "show_count")
    private Boolean showCount = true;

    @Column(name = "search_item_type", length = 100)
    private String searchItemType;

    @Column(name = "search_filters", columnDefinition = "TEXT")
    private String searchFilters;

    @Enumerated(EnumType.STRING)
    @Column(name = "module_type", length = 50)
    private DashboardModuleType moduleType;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "description_tr", length = 500)),
            @AttributeOverride(name = "en", column = @Column(name = "description_en", length = 500)),
            @AttributeOverride(name = "de", column = @Column(name = "description_de", length = 500)),
            @AttributeOverride(name = "fr", column = @Column(name = "description_fr", length = 500)),
            @AttributeOverride(name = "es", column = @Column(name = "description_es", length = 500)),
            @AttributeOverride(name = "it", column = @Column(name = "description_it", length = 500))
    })
    private Localized description;

    @ManyToMany
    @JoinTable(name = "dashboard_modules_user_groups",
            joinColumns = @JoinColumn(name = "dashboard_module_id"),
            inverseJoinColumns = @JoinColumn(name = UserGroupModel.USER_GROUP_RELATION))
    private Set<UserGroupModel> userGroups;
}
