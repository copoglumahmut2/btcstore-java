package com.btc_store.domain.model.store;

import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.enums.CallRequestActionType;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import com.btc_store.domain.model.store.extend.StoreItemModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;

@Entity
@Table(name = DomainConstant.CALLREQUESTHISTORYMODEL_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.CALLREQUESTHISTORYMODEL_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@Getter
@Setter
@FieldNameConstants
public class StoreCallRequestHistoryModel extends StoreCodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    private StoreCallRequestModel callRequest;

    @Enumerated(EnumType.STRING)
    private CallRequestActionType actionType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "performed_by_username")
    private String performedByUsername;

    @Enumerated(EnumType.STRING)
    private CallRequestStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private CallRequestStatus newStatus;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
}
