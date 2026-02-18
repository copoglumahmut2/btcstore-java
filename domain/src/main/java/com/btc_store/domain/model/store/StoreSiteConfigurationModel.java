package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.MenuLinkItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.localize.Localized;
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

    // Top Banner Fields
    @Column(name = "top_banner_enabled")
    private Boolean topBannerEnabled = false;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "top_banner_text_tr", length = 500)),
            @AttributeOverride(name = "en", column = @Column(name = "top_banner_text_en", length = 500)),
            @AttributeOverride(name = "de", column = @Column(name = "top_banner_text_de", length = 500)),
            @AttributeOverride(name = "fr", column = @Column(name = "top_banner_text_fr", length = 500)),
            @AttributeOverride(name = "es", column = @Column(name = "top_banner_text_es", length = 500)),
            @AttributeOverride(name = "it", column = @Column(name = "top_banner_text_it", length = 500))
    })
    private Localized topBannerText;

    @Column(name = "top_banner_bg_color", length = 20)
    private String topBannerBgColor;

    @Column(name = "top_banner_text_color", length = 20)
    private String topBannerTextColor;

    @Column(name = "top_banner_link", length = 500)
    private String topBannerLink;
}
