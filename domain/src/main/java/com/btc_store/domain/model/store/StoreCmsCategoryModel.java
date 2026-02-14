package com.btc_store.domain.model.store;


import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.model.custom.CmsCategoryModel;
import com.btc_store.domain.model.custom.CmsCategoryTypeModel;
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

@Entity
@Table(name = DomainConstant.CMSCATEGORYMODEL_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.CMSCATEGORYMODEL_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreCmsCategoryModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String CMS_CATEGORY_RELATION = "cmscategory_id";

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tr", column = @Column(name = "name_tr")),
            @AttributeOverride(name = "en", column = @Column(name = "name_en")),
            @AttributeOverride(name = "de", column = @Column(name = "name_de")),
            @AttributeOverride(name = "fr", column = @Column(name = "name_fr")),
            @AttributeOverride(name = "es", column = @Column(name = "name_es")),
            @AttributeOverride(name = "it", column = @Column(name = "name_it"))
    })
    private Localized name;

    private Boolean root;

    @OneToOne(fetch = FetchType.LAZY)
    private CmsCategoryModel cmsCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private CmsCategoryTypeModel cmsCategoryType;

    private String description;

}
