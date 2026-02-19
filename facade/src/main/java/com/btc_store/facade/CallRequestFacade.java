package com.btc_store.facade;

import com.btc_store.domain.data.custom.CallRequestData;
import com.btc_store.domain.data.custom.CallRequestHistoryData;
import com.btc_store.domain.enums.CallRequestPriority;
import com.btc_store.domain.enums.CallRequestStatus;

import java.util.List;

public interface CallRequestFacade {
    
    CallRequestData createCallRequest(CallRequestData callRequestData);
    
    CallRequestData createProductContactRequest(String productCode, CallRequestData callRequestData);
    
    CallRequestData updateCallRequest(CallRequestData callRequestData);
    
    CallRequestData getCallRequestById(Long id);
    
    List<CallRequestData> getAllCallRequests();
    
    List<CallRequestData> getCallRequestsByStatus(CallRequestStatus status);
    
    List<CallRequestData> getMyCallRequests();
    
    void assignToGroup(Long callRequestId, String groupCode);
    
    void assignToGroups(Long callRequestId, List<String> groupCodes);
    
    void assignToUser(Long callRequestId, Long userId);
    
    void assignToUsers(Long callRequestId, List<Long> userIds);
    
    void updateStatus(Long callRequestId, CallRequestStatus newStatus, String comment);
    
    void updatePriority(Long callRequestId, CallRequestPriority newPriority);
    
    void closeCallRequest(Long callRequestId, String comment);
    
    List<CallRequestHistoryData> getCallRequestHistory(Long callRequestId);
}
