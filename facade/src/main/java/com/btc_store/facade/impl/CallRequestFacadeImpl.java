package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.BannerData;
import com.btc_store.domain.data.custom.CallRequestData;
import com.btc_store.domain.data.custom.CallRequestHistoryData;
import com.btc_store.domain.data.custom.pageable.PageableData;
import com.btc_store.domain.enums.CallRequestPriority;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.CallRequestModel;
import com.btc_store.domain.model.custom.LegalDocumentModel;
import com.btc_store.domain.model.custom.ProductModel;
import com.btc_store.facade.CallRequestFacade;
import com.btc_store.facade.pageable.PageableProvider;
import com.btc_store.service.CallRequestHistoryService;
import com.btc_store.service.CallRequestService;
import com.btc_store.service.SearchService;
import com.btc_store.service.SiteService;
import com.btc_store.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
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
    private final SearchService searchService;
    protected final PageableProvider pageableProvider;
    
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
        
        // If acceptedLegalDocument is provided, find and set it by code
        if (callRequestData.getAcceptedLegalDocument() != null && 
            callRequestData.getAcceptedLegalDocument().getCode() != null) {
            try {
                var legalDocument = searchService.searchByCodeAndSite(
                    LegalDocumentModel.class,
                    callRequestData.getAcceptedLegalDocument().getCode(),
                    siteModel
                );
                callRequestModel.setAcceptedLegalDocument(legalDocument);
            } catch (Exception e) {
                log.warn("Could not find legal document with code: {}", 
                    callRequestData.getAcceptedLegalDocument().getCode(), e);
            }
        }
        
        var savedModel = callRequestService.createCallRequest(callRequestModel);
        return modelMapper.map(savedModel, CallRequestData.class);
    }
    
    @Override
    public CallRequestData createProductContactRequest(String productCode, CallRequestData callRequestData) {
        var siteModel = siteService.getCurrentSite();
        
        CallRequestModel callRequestModel = modelMapper.map(callRequestData, CallRequestModel.class);
        callRequestModel.setSite(siteModel);
        callRequestModel.setCode(UUID.randomUUID().toString());
        
        // Find and set the product
        ProductModel product = null;
        try {
            product = searchService.searchByCodeAndSite(
                    ProductModel.class,
                productCode,
                siteModel
            );
            callRequestModel.setProduct(product);

            
            log.info("Creating call request for product: {} ({})", product.getCode(), product.getName());
        } catch (Exception e) {
            log.error("Could not find product with code: {}", productCode, e);
            throw new RuntimeException("Product not found: " + productCode);
        }
        
        // If acceptedLegalDocument is provided, find and set it by code
        if (callRequestData.getAcceptedLegalDocument() != null && 
            callRequestData.getAcceptedLegalDocument().getCode() != null) {
            try {
                var legalDocument = searchService.searchByCodeAndSite(
                    LegalDocumentModel.class,
                    callRequestData.getAcceptedLegalDocument().getCode(),
                    siteModel
                );
                callRequestModel.setAcceptedLegalDocument(legalDocument);
            } catch (Exception e) {
                log.warn("Could not find legal document with code: {}", 
                    callRequestData.getAcceptedLegalDocument().getCode(), e);
            }
        }
        
        // Assign to product responsible users if available
        if (product != null && product.getResponsibleUsers() != null && !product.getResponsibleUsers().isEmpty()) {
            callRequestModel.setAssignedUsers(new java.util.HashSet<>(product.getResponsibleUsers()));
            callRequestModel.setStatus(CallRequestStatus.ASSIGNED);
            log.info("Product contact request assigned to {} responsible users", product.getResponsibleUsers().size());
        }
        
        // createCallRequest içinde otomatik olarak email gönderilecek
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
        return modelMapper.map(callRequestModel, CallRequestData.class);
    }
    
    @Override
    public List<CallRequestData> getAllCallRequests() {
        var siteModel = siteService.getCurrentSite();
        var callRequestModels = callRequestService.getCallRequestsBySite(siteModel);
        return List.of(modelMapper.map(callRequestModels, CallRequestData[].class));
    }
    
    @Override
    public List<CallRequestData> getCallRequestsByStatus(CallRequestStatus status) {
        var siteModel = siteService.getCurrentSite();
        var callRequestModels = callRequestService.getCallRequestsBySiteAndStatus(siteModel, status);
        return List.of(modelMapper.map(callRequestModels, CallRequestData[].class));
    }
    
    @Override
    public PageableData getMyCallRequestsPageable(Pageable pageable) {
        var callRequestModelsPage = callRequestService.getMyCallRequestsPageable(pageable);
        return pageableProvider.map(callRequestModelsPage, CallRequestData.class);
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
