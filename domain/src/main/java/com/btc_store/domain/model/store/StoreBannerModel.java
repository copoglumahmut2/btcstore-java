package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.extend.CodeBasedItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import com.btc_store.domain.model.store.localize.StoreLocalized;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;

@Entity
@Table(name = DomainConstant.BANNER_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.BANNER_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreBannerModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String BANNER_RELATION = "banner_id";

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "title_tr", length = 500)),
            @AttributeOverride(name = "en", column = @Column(name = "title_en", length = 500)),
            @AttributeOverride(name = "de", column = @Column(name = "title_de", length = 500)),
            @AttributeOverride(name = "fr", column = @Column(name = "title_fr", length = 500)),
            @AttributeOverride(name = "es", column = @Column(name = "title_es", length = 500)),
            @AttributeOverride(name = "it", column = @Column(name = "title_it", length = 500))
    })
    private StoreLocalized title;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "subtitle_tr", length = 1000)),
            @AttributeOverride(name = "en", column = @Column(name = "subtitle_en", length = 1000)),
            @AttributeOverride(name = "de", column = @Column(name = "subtitle_de", length = 1000)),
            @AttributeOverride(name = "fr", column = @Column(name = "subtitle_fr", length = 1000)),
            @AttributeOverride(name = "es", column = @Column(name = "subtitle_es", length = 1000)),
            @AttributeOverride(name = "it", column = @Column(name = "subtitle_it", length = 1000))
    })
    private StoreLocalized subtitle;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "button_text_tr", length = 200)),
            @AttributeOverride(name = "en", column = @Column(name = "button_text_en", length = 200)),
            @AttributeOverride(name = "de", column = @Column(name = "button_text_de", length = 200)),
            @AttributeOverride(name = "fr", column = @Column(name = "button_text_fr", length = 200)),
            @AttributeOverride(name = "es", column = @Column(name = "button_text_es", length = 200)),
            @AttributeOverride(name = "it", column = @Column(name = "button_text_it", length = 200))
    })
    private StoreLocalized buttonText;

    @Column(length = 500)
    private String buttonLink;

    @ManyToOne(fetch = FetchType.LAZY)
    private MediaModel media;

    @Column(name = "display_order")
    private Integer order;

    @Column(nullable = false)
    private Boolean active = true;
}
