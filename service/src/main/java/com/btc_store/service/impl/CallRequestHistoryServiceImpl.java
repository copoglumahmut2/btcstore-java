package com.btc_store.service.impl;

import com.btc_store.domain.enums.CallRequestActionType;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.CallRequestHistoryModel;
import com.btc_store.domain.model.custom.CallRequestModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.persistence.dao.CallRequestHistoryDao;
import com.btc_store.service.CallRequestHistoryService;
import com.btc_store.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallRequestHistoryServiceImpl implements CallRequestHistoryService {
    
    private final CallRequestHistoryDao callRequestHistoryDao;
    private final ModelService modelService;
    
    @Override
    @Transactional
    public CallRequestHistoryModel createHistory(
            CallRequestModel callRequest,
            CallRequestActionType actionType,
            String description,
            Long performedByUserId,
            String performedByUsername,
            CallRequestStatus oldStatus,
            CallRequestStatus newStatus,
            String comment,
            SiteModel siteModel) {
        
        CallRequestHistoryModel history = new CallRequestHistoryModel();
        history.setCode(UUID.randomUUID().toString());
        history.setSite(siteModel);
        history.setCallRequest(callRequest);
        history.setActionType(actionType);
        history.setDescription(description);
        history.setPerformedByUsername(performedByUsername);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setComment(comment);
        
        return modelService.save(history);
    }
    
    @Override
    public List<CallRequestHistoryModel> getHistoryByCallRequest(CallRequestModel callRequest) {
        return callRequestHistoryDao.findByCallRequestOrderByCreatedDateDesc(callRequest);
    }
    
    @Override
    public List<CallRequestHistoryModel> getHistoryByCallRequestId(Long callRequestId) {
        return callRequestHistoryDao.findByCallRequestIdOrderByCreatedDateDesc(callRequestId);
    }
}
