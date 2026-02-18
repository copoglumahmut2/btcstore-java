package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.CallRequestData;
import com.btc_store.domain.data.custom.CallRequestHistoryData;
import com.btc_store.domain.enums.CallRequestPriority;
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
    
    // Configure ModelMapper for CallRequest conversion
    private void configureModelMapper() {
        // This will be called once to configure custom mappings if needed
        // For now, we rely on default mapping + manual collection handling
    }
    
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
        
        // Sort by created date descending (null-safe)
        allRequests.sort((a, b) -> {
            if (a.getCreatedDate() == null && b.getCreatedDate() == null) return 0;
            if (a.getCreatedDate() == null) return 1;
            if (b.getCreatedDate() == null) return -1;
            return b.getCreatedDate().compareTo(a.getCreatedDate());
        });
        
        return allRequests.stream()
                .map(this::convertToData)
                .toList();
    }
    
    private CallRequestData convertToData(CallRequestModel model) {
        // ModelMapper automatically maps basic fields
        CallRequestData data = modelMapper.map(model, CallRequestData.class);
        
        // Only handle collections and computed fields manually
        mapAssignedUsers(model, data);
        mapAssignedGroups(model, data);
        
        log.debug("Converted CallRequestModel to Data - ID: {}", data.getId());
        return data;
    }
    
    private void mapAssignedUsers(CallRequestModel model, CallRequestData data) {
        if (model.getAssignedUsers() == null || model.getAssignedUsers().isEmpty()) {
            return;
        }
        
        // Detailed list - ModelMapper handles the mapping
        List<CallRequestData.AssignedUserInfo> userInfoList = model.getAssignedUsers().stream()
                .map(user -> modelMapper.map(user, CallRequestData.AssignedUserInfo.class))
                .toList();
        data.setAssignedUsersList(userInfoList);
        
        // Simple list for backward compatibility
        List<String> userNames = model.getAssignedUsers().stream()
                .map(user -> user.getUsername())
                .toList();
        data.setAssignedUserNames(userNames);
        
        // Set first user for backward compatibility
        var firstUser = model.getAssignedUsers().iterator().next();
        data.setAssignedUserId(firstUser.getId());
        data.setAssignedUserName(firstUser.getUsername());
    }
    
    private void mapAssignedGroups(CallRequestModel model, CallRequestData data) {
        if (model.getAssignedGroups() == null || model.getAssignedGroups().isEmpty()) {
            return;
        }
        
        // Detailed list with computed name field
        List<CallRequestData.AssignedGroupInfo> groupInfoList = model.getAssignedGroups().stream()
                .map(group -> {
                    var info = new CallRequestData.AssignedGroupInfo();
                    info.setCode(group.getCode());
                    
                    if (group.getDescription() != null) {
                        // ModelMapper handles Localized -> LocalizedDescription
                        info.setDescription(modelMapper.map(group.getDescription(), CallRequestData.LocalizedDescription.class));
                        // Computed field: default name from Turkish description
                        info.setName(group.getDescription().getTr() != null ? group.getDescription().getTr() : group.getCode());
                    } else {
                        info.setName(group.getCode());
                    }
                    
                    return info;
                })
                .toList();
        data.setAssignedGroupsList(groupInfoList);
        
        // Simple string for backward compatibility
        String groupCodes = model.getAssignedGroups().stream()
                .map(group -> group.getCode())
                .collect(java.util.stream.Collectors.joining(";"));
        data.setAssignedGroups(groupCodes);
        
        // Set first group for backward compatibility
        data.setAssignedGroup(model.getAssignedGroups().iterator().next().getCode());
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
    public void updatePriority(Long callRequestId, CallRequestPriority newPriority) {
        callRequestService.updatePriority(callRequestId, newPriority);
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
