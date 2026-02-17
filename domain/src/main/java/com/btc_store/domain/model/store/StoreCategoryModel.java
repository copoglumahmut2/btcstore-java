package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.localize.Localized;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;

@Entity
@Table(name = DomainConstant.CATEGORY_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.CATEGORY_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreCategoryModel extends StoreCodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String CATEGORY_RELATION = "category_id";

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "name_tr", length = 500)),
            @AttributeOverride(name = "en", column = @Column(name = "name_en", length = 500)),
            @AttributeOverride(name = "de", column = @Column(name = "name_de", length = 500)),
            @AttributeOverride(name = "fr", column = @Column(name = "name_fr", length = 500)),
            @AttributeOverride(name = "es", column = @Column(name = "name_es", length = 500)),
            @AttributeOverride(name = "it", column = @Column(name = "name_it", length = 500))
    })
    private Localized name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "description_tr", length = 2000)),
            @AttributeOverride(name = "en", column = @Column(name = "description_en", length = 2000)),
            @AttributeOverride(name = "de", column = @Column(name = "description_de", length = 2000)),
            @AttributeOverride(name = "fr", column = @Column(name = "description_fr", length = 2000)),
            @AttributeOverride(name = "es", column = @Column(name = "description_es", length = 2000)),
            @AttributeOverride(name = "it", column = @Column(name = "description_it", length = 2000))
    })
    private Localized description;

    @ManyToOne(fetch = FetchType.LAZY)
    private MediaModel media;

    // Renk Ayarları
    @Column(name = "background_color", length = 50)
    private String backgroundColor;

    @Column(name = "text_color", length = 50)
    private String textColor;

    // Buton Ayarları
    @Column(name = "show_button")
    private Boolean showButton = true;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "button_text_tr", length = 200)),
            @AttributeOverride(name = "en", column = @Column(name = "button_text_en", length = 200)),
            @AttributeOverride(name = "de", column = @Column(name = "button_text_de", length = 200)),
            @AttributeOverride(name = "fr", column = @Column(name = "button_text_fr", length = 200)),
            @AttributeOverride(name = "es", column = @Column(name = "button_text_es", length = 200)),
            @AttributeOverride(name = "it", column = @Column(name = "button_text_it", length = 200))
    })
    private Localized buttonText;

    @Column(name = "button_link", length = 500)
    private String buttonLink;

    @Column(name = "button_background_color", length = 50)
    private String buttonBackgroundColor;

    @Column(name = "button_border_color", length = 50)
    private String buttonBorderColor;

    @Column(name = "button_text_color", length = 50)
    private String buttonTextColor;

    // Görünürlük
    @Column(name = "show_on_homepage")
    private Boolean showOnHomepage = false;

    @Column(name = "display_order")
    private Integer order;

    @Column(nullable = false)
    private Boolean active = true;
}
