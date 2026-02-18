package com.btc_store.domain.data.store;

import com.btc_store.domain.data.custom.MenuLinkItemData;
import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.extend.back.BackBaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreSiteConfigurationData extends BackBaseData {

    private MediaData headerLogo;
    private MediaData footerLogo;
    private String contactPhone;
    private Boolean showContactPhone;
    private String footerEmail;
    private String footerPhone;
    private String footerAddress;
    private Set<MenuLinkItemData> footerMenus;

    // Top Banner Fields
    private Boolean topBannerEnabled;
    private LocalizeData topBannerText;
    private String topBannerBgColor;
    private String topBannerTextColor;
    private String topBannerLink;
}
