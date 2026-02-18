package com.btc_store.facade;

import com.btc_store.domain.data.custom.CallRequestData;
import com.btc_store.domain.data.custom.CallRequestHistoryData;
import com.btc_store.domain.enums.CallRequestStatus;

import java.util.List;

public interface CallRequestFacade {
    
    CallRequestData createCallRequest(CallRequestData callRequestData);
    
    CallRequestData updateCallRequest(CallRequestData callRequestData);
    
    CallRequestData getCallRequestById(Long id);
    
    List<CallRequestData> getAllCallRequests();
    
    List<CallRequestData> getCallRequestsByStatus(CallRequestStatus status);
    
    List<CallRequestData> getMyCallRequests();
    
    void assignToGroup(Long callRequestId, String groupCode);
    
    void assignToUser(Long callRequestId, Long userId);
    
    void updateStatus(Long callRequestId, CallRequestStatus newStatus, String comment);
    
    List<CallRequestHistoryData> getCallRequestHistory(Long callRequestId);
}
