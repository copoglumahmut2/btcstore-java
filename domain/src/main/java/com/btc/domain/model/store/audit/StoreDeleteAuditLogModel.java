package com.btc.domain.model.store.audit;

import com.btc.domain.constant.DomainConstant;
import com.btc.domain.model.custom.extend.CodeBasedItemModel;
import com.btc.domain.model.custom.extend.SiteBasedItemModel;
import com.btc.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serial;

@Entity
@Table(name = DomainConstant.DELETE_AUDIT_LOG_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.DELETE_AUDIT_LOG_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
public class StoreDeleteAuditLogModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String DATA = "data";
    public static final String ITEMTYPE = "itemType";

    @Column(columnDefinition = "TEXT")
    private String data;
    private String itemType;

}
