package com.btc_store.service;

import com.btc_store.domain.enums.CallRequestPriority;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.CallRequestModel;
import com.btc_store.domain.model.custom.SiteModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CallRequestService {
    
    CallRequestModel createCallRequest(CallRequestModel callRequestModel, String isoCode);
    
    CallRequestModel updateCallRequest(CallRequestModel callRequestModel);
    
    CallRequestModel getCallRequestById(Long id);
    
    List<CallRequestModel> getCallRequestsBySite(SiteModel siteModel);
    
    List<CallRequestModel> getCallRequestsBySiteAndStatus(SiteModel siteModel, CallRequestStatus status);
    
    List<CallRequestModel> getCallRequestsByAssignedGroup(String assignedGroup, CallRequestStatus status);
    
    List<CallRequestModel> getCallRequestsByAssignedUser(Long userId, CallRequestStatus status);
    
    List<CallRequestModel> getMyCallRequests();
    
    Page<CallRequestModel> getMyCallRequestsPageable(org.springframework.data.domain.Pageable pageable);
    
    void assignToGroup(Long callRequestId, String groupCode, String isoCode);
    
    void assignToGroups(Long callRequestId, List<String> groupCodes, String isoCode);
    
    void assignToUser(Long callRequestId, Long userId, String isoCode);
    
    void assignToUsers(Long callRequestId, List<Long> userIds, String isoCode);
    
    void updateStatus(Long callRequestId, CallRequestStatus newStatus, String comment, String isoCode);
    
    void updatePriority(Long callRequestId, CallRequestPriority newPriority, String isoCode);
    
    void closeCallRequest(Long callRequestId, String comment, String isoCode);
    
    void publishCallRequestEvent(CallRequestModel callRequestModel, String eventType);
}
