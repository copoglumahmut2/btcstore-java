package com.btc.domain.model.store;

import com.btc.domain.constant.DomainConstant;
import com.btc.domain.model.custom.extend.CodeBasedItemModel;
import com.btc.domain.model.custom.extend.SiteBasedItemModel;
import com.btc.domain.model.custom.localize.Localized;
import com.btc.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Entity
@Table(name = DomainConstant.CMSCATEGORYTYPEMODEL_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.CMSCATEGORYTYPEMODEL_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
public class StoreCmsCategoryTypeModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

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

}
