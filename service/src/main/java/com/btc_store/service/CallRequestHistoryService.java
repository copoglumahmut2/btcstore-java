package com.btc_store.service;

import com.btc_store.domain.enums.CallRequestActionType;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.CallRequestHistoryModel;
import com.btc_store.domain.model.custom.CallRequestModel;
import com.btc_store.domain.model.custom.SiteModel;

import java.util.List;

public interface CallRequestHistoryService {
    
    CallRequestHistoryModel createHistory(
            CallRequestModel callRequest,
            CallRequestActionType actionType,
            String description,
            Long performedByUserId,
            String performedByUsername,
            CallRequestStatus oldStatus,
            CallRequestStatus newStatus,
            String comment,
            SiteModel siteModel
    );
    
    List<CallRequestHistoryModel> getHistoryByCallRequest(CallRequestModel callRequest);
    
    List<CallRequestHistoryModel> getHistoryByCallRequestId(Long callRequestId);
}
