package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.CallRequestData;
import com.btc_store.domain.data.custom.CallRequestHistoryData;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.CallRequestHistoryModel;
import com.btc_store.domain.model.custom.CallRequestModel;
import com.btc_store.facade.CallRequestFacade;
import com.btc_store.service.CallRequestHistoryService;
import com.btc_store.service.CallRequestService;
import com.btc_store.service.SiteService;
import com.btc_store.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CallRequestFacadeImpl implements CallRequestFacade {
    
    private final CallRequestService callRequestService;
    private final CallRequestHistoryService callRequestHistoryService;
    private final SiteService siteService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    
    @Override
    public CallRequestData createCallRequest(CallRequestData callRequestData) {
        var siteModel = siteService.getCurrentSite();
        
        CallRequestModel callRequestModel = modelMapper.map(callRequestData, CallRequestModel.class);
        callRequestModel.setSite(siteModel);
        callRequestModel.setCode(UUID.randomUUID().toString());
        
        var savedModel = callRequestService.createCallRequest(callRequestModel);
        return modelMapper.map(savedModel, CallRequestData.class);
    }
    
    @Override
    public CallRequestData updateCallRequest(CallRequestData callRequestData) {
        var siteModel = siteService.getCurrentSite();
        
        CallRequestModel existingModel = callRequestService.getCallRequestById((Long) callRequestData.getId());
        modelMapper.map(callRequestData, existingModel);
        
        var updatedModel = callRequestService.updateCallRequest(existingModel);
        return modelMapper.map(updatedModel, CallRequestData.class);
    }
    
    @Override
    public CallRequestData getCallRequestById(Long id) {
        var callRequestModel = callRequestService.getCallRequestById(id);
        return convertToData(callRequestModel);
    }
    
    @Override
    public List<CallRequestData> getAllCallRequests() {
        var siteModel = siteService.getCurrentSite();
        var callRequestModels = callRequestService.getCallRequestsBySite(siteModel);
        return callRequestModels.stream()
                .map(this::convertToData)
                .toList();
    }
    
    @Override
    public List<CallRequestData> getCallRequestsByStatus(CallRequestStatus status) {
        var siteModel = siteService.getCurrentSite();
        var callRequestModels = callRequestService.getCallRequestsBySiteAndStatus(siteModel, status);
        return callRequestModels.stream()
                .map(this::convertToData)
                .toList();
    }
    
    @Override
    public List<CallRequestData> getMyCallRequests() {
        var currentUser = userService.getCurrentUser();
        
        // Get user's groups
        var userGroups = currentUser.getUserGroups();
        
        // Get requests assigned directly to user (IN_PROGRESS or ASSIGNED status)
        var userRequests = callRequestService.getCallRequestsByAssignedUser(
                currentUser.getId(), 
                CallRequestStatus.IN_PROGRESS
        );
        
        // Get requests assigned to user's groups (ASSIGNED status)
        var groupRequests = userGroups.stream()
                .flatMap(group -> callRequestService.getCallRequestsByAssignedGroup(
                        group.getCode(), 
                        CallRequestStatus.ASSIGNED
                ).stream())
                .toList();
        
        // Combine and remove duplicates
        var allRequests = new java.util.ArrayList<>(userRequests);
        groupRequests.forEach(req -> {
            if (allRequests.stream().noneMatch(r -> r.getId().equals(req.getId()))) {
                allRequests.add(req);
            }
        });
        
        // Sort by created date descending
        allRequests.sort((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()));
        
        return allRequests.stream()
                .map(this::convertToData)
                .toList();
    }
    
    private CallRequestData convertToData(CallRequestModel model) {
        CallRequestData data = modelMapper.map(model, CallRequestData.class);
        
        // Manually set audit fields to ensure they are mapped
        data.setCreatedDate(model.getCreatedDate());
        data.setLastModifiedDate(model.getLastModifiedDate());
        data.setCreatedBy(model.getCreatedBy());
        data.setLastModifiedBy(model.getLastModifiedBy());
        
        if (model.getAssignedUser() != null) {
            data.setAssignedUserId(model.getAssignedUser().getId());
            data.setAssignedUserName(model.getAssignedUser().getUsername());
        }
        
        log.debug("Converted CallRequestModel to Data - ID: {}, CreatedDate: {}", data.getId(), data.getCreatedDate());
        return data;
    }
    
    @Override
    public void assignToGroup(Long callRequestId, String groupCode) {
        callRequestService.assignToGroup(callRequestId, groupCode);
    }
    
    @Override
    public void assignToGroups(Long callRequestId, List<String> groupCodes) {
        callRequestService.assignToGroups(callRequestId, groupCodes);
    }
    
    @Override
    public void assignToUser(Long callRequestId, Long userId) {
        callRequestService.assignToUser(callRequestId, userId);
    }
    
    @Override
    public void assignToUsers(Long callRequestId, List<Long> userIds) {
        callRequestService.assignToUsers(callRequestId, userIds);
    }
    
    @Override
    public void updateStatus(Long callRequestId, CallRequestStatus newStatus, String comment) {
        callRequestService.updateStatus(callRequestId, newStatus, comment);
    }
    
    @Override
    public void closeCallRequest(Long callRequestId, String comment) {
        callRequestService.closeCallRequest(callRequestId, comment);
    }
    
    @Override
    public List<CallRequestHistoryData> getCallRequestHistory(Long callRequestId) {
        var historyModels = callRequestHistoryService.getHistoryByCallRequestId(callRequestId);
        return List.of(modelMapper.map(historyModels, CallRequestHistoryData[].class));
    }
}
