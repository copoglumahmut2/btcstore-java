package com.btc_store.facade;

import com.btc_store.domain.data.custom.CallRequestData;
import com.btc_store.domain.data.custom.CallRequestHistoryData;
import com.btc_store.domain.data.custom.pageable.PageableData;
import com.btc_store.domain.enums.CallRequestPriority;
import com.btc_store.domain.enums.CallRequestStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CallRequestFacade {
    
    CallRequestData createCallRequest(CallRequestData callRequestData, String isoCode);
    
    CallRequestData createProductContactRequest(String productCode, CallRequestData callRequestData, String isoCode);
    
    CallRequestData updateCallRequest(CallRequestData callRequestData);
    
    CallRequestData getCallRequestById(Long id);
    
    List<CallRequestData> getAllCallRequests();
    
    List<CallRequestData> getCallRequestsByStatus(CallRequestStatus status);

    PageableData getMyCallRequestsPageable(Pageable pageable);
    
    void assignToGroup(Long callRequestId, String groupCode, String isoCode);
    
    void assignToGroups(Long callRequestId, List<String> groupCodes, String isoCode);
    
    void assignToUser(Long callRequestId, Long userId, String isoCode);
    
    void assignToUsers(Long callRequestId, List<Long> userIds, String isoCode);
    
    void updateStatus(Long callRequestId, CallRequestStatus newStatus, String comment, String isoCode);
    
    void updatePriority(Long callRequestId, CallRequestPriority newPriority, String isoCode);
    
    void closeCallRequest(Long callRequestId, String comment, String isoCode);
    
    List<CallRequestHistoryData> getCallRequestHistory(Long callRequestId, String isoCode);
}
