package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.ProductModel;
import com.btc_store.domain.model.custom.extend.CodeBasedItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.localize.Localized;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = DomainConstant.DOCUMENT_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.DOCUMENT_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreDocumentModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String DOCUMENT_RELATION = "document_id";

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "title_tr", length = 500, nullable = false)),
            @AttributeOverride(name = "en", column = @Column(name = "title_en", length = 500)),
            @AttributeOverride(name = "de", column = @Column(name = "title_de", length = 500)),
            @AttributeOverride(name = "fr", column = @Column(name = "title_fr", length = 500)),
            @AttributeOverride(name = "es", column = @Column(name = "title_es", length = 500)),
            @AttributeOverride(name = "it", column = @Column(name = "title_it", length = 500))
    })
    private Localized title;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "description_tr", columnDefinition = "TEXT")),
            @AttributeOverride(name = "en", column = @Column(name = "description_en", columnDefinition = "TEXT")),
            @AttributeOverride(name = "de", column = @Column(name = "description_de", columnDefinition = "TEXT")),
            @AttributeOverride(name = "fr", column = @Column(name = "description_fr", columnDefinition = "TEXT")),
            @AttributeOverride(name = "es", column = @Column(name = "description_es", columnDefinition = "TEXT")),
            @AttributeOverride(name = "it", column = @Column(name = "description_it", columnDefinition = "TEXT"))
    })
    private Localized description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "document_media",
            joinColumns = @JoinColumn(name = DOCUMENT_RELATION),
            inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private List<MediaModel> medias = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "document_products",
            joinColumns = @JoinColumn(name = DOCUMENT_RELATION),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<ProductModel> products = new ArrayList<>();

    private Boolean active = true;

    private Boolean deleted = false;
}
