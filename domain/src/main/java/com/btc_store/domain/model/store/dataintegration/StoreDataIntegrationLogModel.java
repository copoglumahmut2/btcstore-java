package com.btc_store.domain.model.store.dataintegration;
import com.btc_store.domain.constant.DomainConstant;
import com.btc_store.domain.enums.DataIntegrationStatus;
import com.btc_store.domain.enums.ImportProcessType;
import com.btc_store.domain.model.custom.extend.CodeBasedItemModel;
import com.btc_store.domain.model.custom.extend.SiteBasedItemModel;
import com.btc_store.domain.model.store.extend.StoreCodeBasedItemModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.util.Date;

@Entity
@Table(name = DomainConstant.DATAINTEGRATIONLOG_TABLE_NAME,
        uniqueConstraints = {@UniqueConstraint(columnNames = {StoreCodeBasedItemModel.Fields.code, SiteBasedItemModel.SITE_RELATION})},
        indexes = {@Index(name = DomainConstant.DATAINTEGRATIONLOG_TABLE_NAME + DomainConstant.CODE_IDX, columnList = "code,site_id")})
@FieldNameConstants
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Getter
@Setter
public class StoreDataIntegrationLogModel extends CodeBasedItemModel {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String ITEMTYPE = "itemType";
    public static final String IMPORTPROCESSTYPE = "importProcessType";
    public static final String STARTDATE = "startDate";
    public static final String ENDDATE = "endDate";
    public static final String COUNT = "count";
    public static final String LOGFILE = "logFile";
    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";

    private String itemType;
    @Enumerated(EnumType.STRING)
    private ImportProcessType importProcessType;
    private Date startDate;
    private Date endDate;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String requestJson;
    private int count;
    private String logFile;
    @Enumerated(EnumType.STRING)
    private DataIntegrationStatus status;

}
