package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.enums.MenuType;
import com.btc_store.domain.model.custom.MenuLinkItemModel;
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
@Table(name = DomainConstant.MENU_LINK_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(name = DomainConstant.MENU_LINK_TABLE_NAME + DomainConstant.UNIQUE_KEYS,
                columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.MENU_LINK_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreMenuLinkItemModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String MENU_LINK_ITEM_RELATION = "menu_link_item_id";
    public static final String PARENT_MENU_LINK_ITEM_RELATION = "parent_menu_link_item_id";

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

    @Column(name = "icon", length = 100)
    private String icon;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_root")
    private Boolean isRoot = false;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "url", length = 500)
    private String url;

    @Enumerated(EnumType.STRING)
    private MenuType menuType = MenuType.PUBLIC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = PARENT_MENU_LINK_ITEM_RELATION)
    private MenuLinkItemModel parentMenuLinkItem;

    @OneToMany(mappedBy = "parentMenuLinkItem", cascade = CascadeType.ALL)
    private Set<MenuLinkItemModel> subMenuLinkItems;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "menu_user_groups",
            joinColumns = @JoinColumn(name = MENU_LINK_ITEM_RELATION),
            inverseJoinColumns = @JoinColumn(name = UserGroupModel.USER_GROUP_RELATION))
    private Set<UserGroupModel> userGroups;
}
