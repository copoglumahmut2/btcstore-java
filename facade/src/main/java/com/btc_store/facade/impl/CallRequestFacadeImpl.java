package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.CallRequestData;
import com.btc_store.domain.data.custom.CallRequestHistoryData;
import com.btc_store.domain.data.custom.pageable.PageableData;
import com.btc_store.domain.enums.CallRequestPriority;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.CallRequestModel;
import com.btc_store.domain.model.custom.CallRequestHistoryModel;
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
    
    @Override
    public CallRequestData createCallRequest(CallRequestData callRequestData, String isoCode) {
        var siteModel = siteService.getCurrentSite();
        
        CallRequestModel callRequestModel = modelMapper.map(callRequestData, CallRequestModel.class);
        callRequestModel.setSite(siteModel);
        callRequestModel.setCode(UUID.randomUUID().toString());

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
        
        var savedModel = callRequestService.createCallRequest(callRequestModel, isoCode);
        return modelMapper.map(savedModel, CallRequestData.class);
    }
    
    @Override
    public CallRequestData createProductContactRequest(String productCode, CallRequestData callRequestData, String isoCode) {
        var siteModel = siteService.getCurrentSite();
        
        CallRequestModel callRequestModel = modelMapper.map(callRequestData, CallRequestModel.class);
        callRequestModel.setSite(siteModel);
        callRequestModel.setCode(UUID.randomUUID().toString());

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

        if (product != null && product.getResponsibleUsers() != null && !product.getResponsibleUsers().isEmpty()) {
            callRequestModel.setAssignedUsers(new java.util.HashSet<>(product.getResponsibleUsers()));
            callRequestModel.setStatus(CallRequestStatus.ASSIGNED);
            log.info("Product contact request assigned to {} responsible users", product.getResponsibleUsers().size());
        }

        var savedModel = callRequestService.createCallRequest(callRequestModel, isoCode);
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
    public void assignToGroup(Long callRequestId, String groupCode, String isoCode) {
        callRequestService.assignToGroup(callRequestId, groupCode, isoCode);
    }
    
    @Override
    public void assignToGroups(Long callRequestId, List<String> groupCodes, String isoCode) {
        callRequestService.assignToGroups(callRequestId, groupCodes, isoCode);
    }
    
    @Override
    public void assignToUser(Long callRequestId, Long userId, String isoCode) {
        callRequestService.assignToUser(callRequestId, userId, isoCode);
    }
    
    @Override
    public void assignToUsers(Long callRequestId, List<Long> userIds, String isoCode) {
        callRequestService.assignToUsers(callRequestId, userIds, isoCode);
    }
    
    @Override
    public void updateStatus(Long callRequestId, CallRequestStatus newStatus, String comment, String isoCode) {
        callRequestService.updateStatus(callRequestId, newStatus, comment, isoCode);
    }
    
    @Override
    public void updatePriority(Long callRequestId, CallRequestPriority newPriority, String isoCode) {
        callRequestService.updatePriority(callRequestId, newPriority, isoCode);
    }
    
    @Override
    public void closeCallRequest(Long callRequestId, String comment, String isoCode) {
        callRequestService.closeCallRequest(callRequestId, comment, isoCode);
    }
    
    @Override
    public List<CallRequestHistoryData> getCallRequestHistory(Long callRequestId, String isoCode) {
        return callRequestHistoryService.getHistoryByCallRequestId(callRequestId)
                .stream()
                .map(model -> toHistoryData(model, isoCode))
                .collect(java.util.stream.Collectors.toList());
    }

    private CallRequestHistoryData toHistoryData(CallRequestHistoryModel model, String isoCode) {
        var data = modelMapper.map(model, CallRequestHistoryData.class);
        if (model.getMessageKey() != null && !model.getMessageKey().isBlank()) {
            var params = hasText(model.getMessageParams())
                    ? model.getMessageParams().split("\\|")
                    : new String[0];
            data.setDescription(util.Messages.getMessageForIsoCode(model.getMessageKey(), isoCode, (Object[]) params));
        }
        return data;
    }

    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

}
