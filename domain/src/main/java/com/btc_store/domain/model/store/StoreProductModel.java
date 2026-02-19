package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.model.custom.CategoryModel;
import com.btc_store.domain.model.custom.DocumentModel;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.extend.CodeBasedItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.localize.Localized;
import com.btc_store.domain.model.custom.user.UserModel;
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
@Table(name = DomainConstant.PRODUCT_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.PRODUCT_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreProductModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String PRODUCT_RELATION = "product_id";

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "name_tr", length = 500, nullable = false)),
            @AttributeOverride(name = "en", column = @Column(name = "name_en", length = 500)),
            @AttributeOverride(name = "de", column = @Column(name = "name_de", length = 500)),
            @AttributeOverride(name = "fr", column = @Column(name = "name_fr", length = 500)),
            @AttributeOverride(name = "es", column = @Column(name = "name_es", length = 500)),
            @AttributeOverride(name = "it", column = @Column(name = "name_it", length = 500))
    })
    private Localized name;

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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "short_description_tr", length = 1000)),
            @AttributeOverride(name = "en", column = @Column(name = "short_description_en", length = 1000)),
            @AttributeOverride(name = "de", column = @Column(name = "short_description_de", length = 1000)),
            @AttributeOverride(name = "fr", column = @Column(name = "short_description_fr", length = 1000)),
            @AttributeOverride(name = "es", column = @Column(name = "short_description_es", length = 1000)),
            @AttributeOverride(name = "it", column = @Column(name = "short_description_it", length = 1000))
    })
    private Localized shortDescription;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = PRODUCT_RELATION),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryModel> categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_image_id")
    private MediaModel mainImage;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_images",
            joinColumns = @JoinColumn(name = PRODUCT_RELATION),
            inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private List<MediaModel> images = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_responsible_users",
            joinColumns = @JoinColumn(name = PRODUCT_RELATION),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserModel> responsibleUsers = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_features", joinColumns = @JoinColumn(name = PRODUCT_RELATION))
    @Column(name = "feature", length = 500)
    private List<String> features = new ArrayList<>();

    @Column(name = "video_link", length = 1000)
    private String videoLink;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_documents",
            joinColumns = @JoinColumn(name = PRODUCT_RELATION),
            inverseJoinColumns = @JoinColumn(name = "document_id")
    )
    private List<DocumentModel> documents = new ArrayList<>();

    private Boolean active = true;

    private Boolean deleted = false;
}
