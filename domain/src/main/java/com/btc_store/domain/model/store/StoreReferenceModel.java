package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.model.custom.MediaModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;

@Entity
@Table(name = DomainConstant.REFERENCE_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.REFERENCE_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@FieldNameConstants
public class StoreReferenceModel extends StoreCodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String REFERENCE_RELATION = "reference_id";

    @Column(name = "name", length = 500, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private MediaModel media;

    @Column(name = "display_order")
    private Integer order;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "show_on_home", nullable = false)
    private Boolean showOnHome = false;
}
