package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.CallRequestData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.CallRequestFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.CALL_REQUESTS)
public class CallRequestController {
    
    private final CallRequestFacade callRequestFacade;
    
    @GetMapping
    @Operation(summary = "Get all call requests")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('CallRequestModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllCallRequests(@Parameter(description = "IsoCode for validation message internalization")
                                                  @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllCallRequests of CallRequestController.");
        var callRequests = callRequestFacade.getAllCallRequests();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(callRequests);
        return responseData;
    }
    
    @GetMapping(ControllerMappings.ID)
    @Operation(summary = "Get call request by id")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('CallRequestModel', @authorizationConstants.READ))")
    public ServiceResponseData getCallRequestById(@Parameter(description = "Call Request ID") @PathVariable Long id,
                                                  @Parameter(description = "IsoCode for validation message internalization")
                                                  @RequestParam(required = false) String isoCode) {
        log.info("Inside getCallRequestById of CallRequestController with id: {}", id);
        var callRequest = callRequestFacade.getCallRequestById(id);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(callRequest);
        return responseData;
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get call requests by status")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('CallRequestModel', @authorizationConstants.READ))")
    public ServiceResponseData getCallRequestsByStatus(@Parameter(description = "Call Request Status") @PathVariable CallRequestStatus status,
                                                       @Parameter(description = "IsoCode for validation message internalization")
                                                       @RequestParam(required = false) String isoCode) {
        log.info("Inside getCallRequestsByStatus of CallRequestController with status: {}", status);
        var callRequests = callRequestFacade.getCallRequestsByStatus(status);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(callRequests);
        return responseData;
    }
    
    @GetMapping(ControllerMappings.MY_REQUESTS)
    @Operation(summary = "Get my assigned call requests")
    @PreAuthorize("isAuthenticated()")
    public ServiceResponseData getMyCallRequests(@Parameter(description = "IsoCode for validation message internalization")
                                                 @RequestParam(required = false) String isoCode) {
        log.info("Inside getMyCallRequests of CallRequestController.");
        var callRequests = callRequestFacade.getMyCallRequests();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(callRequests);
        return responseData;
    }
    
    @PostMapping
    @Operation(summary = "Create call request")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('CallRequestModel', @authorizationConstants.SAVE))")
    public ServiceResponseData createCallRequest(@Parameter(description = "Call request data to create")
                                                 @Validated @RequestBody CallRequestData callRequestData,
                                                 @Parameter(description = "IsoCode for validation message internalization")
                                                 @RequestParam(required = false) String isoCode) {
        log.info("Inside createCallRequest of CallRequestController.");
        var savedCallRequest = callRequestFacade.createCallRequest(callRequestData);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedCallRequest);
        return responseData;
    }
    
    @PutMapping(ControllerMappings.ID)
    @Operation(summary = "Update call request")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('CallRequestModel', @authorizationConstants.SAVE))")
    public ServiceResponseData updateCallRequest(@Parameter(description = "Call Request ID") @PathVariable Long id,
                                                 @Parameter(description = "Call request data to update")
                                                 @Validated @RequestBody CallRequestData callRequestData,
                                                 @Parameter(description = "IsoCode for validation message internalization")
                                                 @RequestParam(required = false) String isoCode) {
        log.info("Inside updateCallRequest of CallRequestController with id: {}", id);
        callRequestData.setId(id);
        var updatedCallRequest = callRequestFacade.updateCallRequest(callRequestData);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(updatedCallRequest);
        return responseData;
    }
    
    @PostMapping(ControllerMappings.ID + ControllerMappings.ASSIGN_GROUP)
    @Operation(summary = "Assign call request to group")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('CallRequestModel', @authorizationConstants.SAVE))")
    public ServiceResponseData assignToGroup(@Parameter(description = "Call Request ID") @PathVariable Long id,
                                            @Parameter(description = "Group Code") @RequestParam String groupCode,
                                            @Parameter(description = "IsoCode for validation message internalization")
                                            @RequestParam(required = false) String isoCode) {
        log.info("Inside assignToGroup of CallRequestController with id: {} and groupCode: {}", id, groupCode);
        callRequestFacade.assignToGroup(id, groupCode);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
    
    @PostMapping(ControllerMappings.ID + ControllerMappings.ASSIGN_USER)
    @Operation(summary = "Assign call request to user")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('CallRequestModel', @authorizationConstants.SAVE))")
    public ServiceResponseData assignToUser(@Parameter(description = "Call Request ID") @PathVariable Long id,
                                           @Parameter(description = "User ID") @RequestParam Long userId,
                                           @Parameter(description = "IsoCode for validation message internalization")
                                           @RequestParam(required = false) String isoCode) {
        log.info("Inside assignToUser of CallRequestController with id: {} and userId: {}", id, userId);
        callRequestFacade.assignToUser(id, userId);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
    
    @PostMapping(ControllerMappings.ID + ControllerMappings.UPDATE_STATUS)
    @Operation(summary = "Update call request status")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('CallRequestModel', @authorizationConstants.SAVE))")
    public ServiceResponseData updateStatus(@Parameter(description = "Call Request ID") @PathVariable Long id,
                                           @Parameter(description = "New Status") @RequestParam CallRequestStatus status,
                                           @Parameter(description = "Comment") @RequestParam(required = false) String comment,
                                           @Parameter(description = "IsoCode for validation message internalization")
                                           @RequestParam(required = false) String isoCode) {
        log.info("Inside updateStatus of CallRequestController with id: {} and status: {}", id, status);
        callRequestFacade.updateStatus(id, status, comment);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
    
    @GetMapping(ControllerMappings.ID + ControllerMappings.HISTORY)
    @Operation(summary = "Get call request history")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('CallRequestModel', @authorizationConstants.READ))")
    public ServiceResponseData getCallRequestHistory(@Parameter(description = "Call Request ID") @PathVariable Long id,
                                                     @Parameter(description = "IsoCode for validation message internalization")
                                                     @RequestParam(required = false) String isoCode) {
        log.info("Inside getCallRequestHistory of CallRequestController with id: {}", id);
        var history = callRequestFacade.getCallRequestHistory(id);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(history);
        return responseData;
    }
}
