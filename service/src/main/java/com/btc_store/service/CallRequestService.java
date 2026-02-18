package com.btc_store.service;

import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.CallRequestModel;
import com.btc_store.domain.model.custom.SiteModel;

import java.util.List;

public interface CallRequestService {
    
    CallRequestModel createCallRequest(CallRequestModel callRequestModel);
    
    CallRequestModel updateCallRequest(CallRequestModel callRequestModel);
    
    CallRequestModel getCallRequestById(Long id);
    
    List<CallRequestModel> getCallRequestsBySite(SiteModel siteModel);
    
    List<CallRequestModel> getCallRequestsBySiteAndStatus(SiteModel siteModel, CallRequestStatus status);
    
    List<CallRequestModel> getCallRequestsByAssignedGroup(String assignedGroup, CallRequestStatus status);
    
    List<CallRequestModel> getCallRequestsByAssignedUser(Long userId, CallRequestStatus status);
    
    void assignToGroup(Long callRequestId, String groupCode);
    
    void assignToGroups(Long callRequestId, List<String> groupCodes);
    
    void assignToUser(Long callRequestId, Long userId);
    
    void assignToUsers(Long callRequestId, List<Long> userIds);
    
    void updateStatus(Long callRequestId, CallRequestStatus newStatus, String comment);
    
    void closeCallRequest(Long callRequestId, String comment);
    
    void publishCallRequestEvent(CallRequestModel callRequestModel, String eventType);
}
