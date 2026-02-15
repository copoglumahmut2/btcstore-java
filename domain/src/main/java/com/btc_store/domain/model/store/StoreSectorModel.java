package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
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
@Table(name = DomainConstant.SECTOR_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.SECTOR_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreSectorModel extends StoreCodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String SECTOR_RELATION = "sector_id";

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

    @Column(nullable = false)
    private Boolean active = true;
}
