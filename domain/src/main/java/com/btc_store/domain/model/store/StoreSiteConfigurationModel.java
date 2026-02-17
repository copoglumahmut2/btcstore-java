package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.MenuLinkItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.util.Set;

@Entity
@Table(name = DomainConstant.SITE_CONFIGURATION_TABLE_NAME,
        indexes = {@Index(name = DomainConstant.SITE_CONFIGURATION_TABLE_NAME + DomainConstant.SITE_IDX, columnList = "site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreSiteConfigurationModel extends SiteBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String SITE_CONFIGURATION_RELATION = "site_configuration_id";

    @ManyToOne(fetch = FetchType.LAZY)
    private MediaModel headerLogo;

    @ManyToOne(fetch = FetchType.LAZY)
    private MediaModel footerLogo;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "show_contact_phone")
    private Boolean showContactPhone = true;

    @Column(name = "footer_email", length = 255)
    private String footerEmail;

    @Column(name = "footer_phone", length = 50)
    private String footerPhone;

    @Column(name = "footer_address", length = 1000)
    private String footerAddress;

    @ManyToMany
    @JoinTable(name = "site_configuration_footer_menus",
            joinColumns = @JoinColumn(name = SITE_CONFIGURATION_RELATION),
            inverseJoinColumns = @JoinColumn(name = StoreMenuLinkItemModel.MENU_LINK_ITEM_RELATION))
    private Set<MenuLinkItemModel> footerMenus;
}
