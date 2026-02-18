package com.btc_store.domain.data.store;

import com.btc_store.domain.data.extend.back.BackBaseData;
import com.btc_store.domain.enums.CallRequestActionType;
import com.btc_store.domain.enums.CallRequestStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreCallRequestHistoryData extends BackBaseData {
    
    private Long callRequestId;
    private CallRequestActionType actionType;
    private String description;
    private String performedByUsername;
    private CallRequestStatus oldStatus;
    private CallRequestStatus newStatus;
    private String comment;
}
