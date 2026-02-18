package com.btc_store.service.impl;

import com.btc_store.domain.enums.CallRequestActionType;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.CallRequestModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.persistence.dao.CallRequestDao;
import com.btc_store.service.*;
import com.btc_store.service.user.UserService;
import com.btc_store.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallRequestServiceImpl implements CallRequestService {
    
    private final CallRequestDao callRequestDao;
    private final CallRequestHistoryService callRequestHistoryService;
    private final ParameterService parameterService;
    private final ModelService modelService;
    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;
    private final GenericTemplateService genericTemplateService;
    private final com.btc_store.service.EmailTemplateService emailTemplateService;
    private final SiteService siteService;
    
    private static final String EMAIL_EXCHANGE = "email.exchange";
    private static final String EMAIL_ROUTING_KEY = "email.routing.key";
    
    @Override
    @Transactional
    public CallRequestModel createCallRequest(CallRequestModel callRequestModel) {
        var siteModel = siteService.getCurrentSite();
        // Save call request (Interceptor otomatik atama ve mail gönderimini yapacak)
        CallRequestModel saved = modelService.save(callRequestModel);
        
        // Create history
        callRequestHistoryService.createHistory(
                saved,
                CallRequestActionType.CREATED,
                "Call request oluşturuldu",
                null,
                "System",
                null,
                saved.getStatus(),
                null,
                siteModel
        );
        
        // Eğer otomatik atama yapıldıysa history ekle
        if (CallRequestStatus.ASSIGNED.equals(saved.getStatus()) && saved.getAssignedGroup() != null) {
            callRequestHistoryService.createHistory(
                    saved,
                    CallRequestActionType.ASSIGNED_TO_GROUP,
                    "Otomatik olarak gruba atandı: " + saved.getAssignedGroup(),
                    null,
                    "System",
                    CallRequestStatus.PENDING,
                    CallRequestStatus.ASSIGNED,
                    null,
                    siteModel
            );
        }
        
        log.info("Call request oluşturuldu: {}", saved.getId());
        return saved;
    }
    
    @Override
    @Transactional
    public CallRequestModel updateCallRequest(CallRequestModel callRequestModel) {
        return callRequestDao.save(callRequestModel);
    }
    
    @Override
    public CallRequestModel getCallRequestById(Long id) {
        CallRequestModel callRequest = callRequestDao.findById(id).orElse(null);
        ServiceUtils.checkItemModelIsExist(callRequest, CallRequestModel.class, null, String.valueOf(id));
        return callRequest;
    }
    
    @Override
    public List<CallRequestModel> getCallRequestsBySite(SiteModel siteModel) {
        return callRequestDao.findBySiteOrderByCreatedDateDesc(siteModel);
    }
    
    @Override
    public List<CallRequestModel> getCallRequestsBySiteAndStatus(SiteModel siteModel, CallRequestStatus status) {
        return callRequestDao.findBySiteAndStatus(siteModel, status);
    }
    
    @Override
    public List<CallRequestModel> getCallRequestsByAssignedGroup(String assignedGroup, CallRequestStatus status) {
        return callRequestDao.findByAssignedGroupAndStatus(assignedGroup, status);
    }
    
    @Override
    public List<CallRequestModel> getCallRequestsByAssignedUser(Long userId, CallRequestStatus status) {
        return callRequestDao.findByAssignedUserIdAndStatus(userId, status);
    }
    
    @Override
    @Transactional
    public void assignToGroup(Long callRequestId, String groupCode) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        CallRequestStatus oldStatus = callRequest.getStatus();
        
        callRequest.setAssignedGroup(groupCode);
        callRequest.setStatus(CallRequestStatus.ASSIGNED);
        modelService.save(callRequest);
        
        // Create history
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.ASSIGNED_TO_GROUP,
                "Gruba atandı: " + groupCode,
                null,
                "System",
                oldStatus,
                CallRequestStatus.ASSIGNED,
                null,
                siteModel
        );
        
        // Publish event
        publishCallRequestEvent(callRequest, "ASSIGNED_TO_GROUP");
        
        log.info("Call request {} gruba atandı: {}", callRequestId, groupCode);
    }
    
    @Override
    @Transactional
    public void assignToUser(Long callRequestId, Long userId) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        UserModel user = userService.getUserModelById(userId);
        CallRequestStatus oldStatus = callRequest.getStatus();
        
        callRequest.setAssignedUser(user);
        callRequest.setStatus(CallRequestStatus.IN_PROGRESS);
        modelService.save(callRequest);
        
        // Create history
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.ASSIGNED_TO_USER,
                "Kullanıcıya atandı: " + user.getUsername(),
                userId,
                user.getUsername(),
                oldStatus,
                CallRequestStatus.IN_PROGRESS,
                null,
                siteModel
        );
        
        log.info("Call request {} kullanıcıya atandı: {}", callRequestId, userId);
    }
    
    @Override
    @Transactional
    public void updateStatus(Long callRequestId, CallRequestStatus newStatus, String comment) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        CallRequestStatus oldStatus = callRequest.getStatus();
        
        callRequest.setStatus(newStatus);
        if (newStatus == CallRequestStatus.COMPLETED) {
            callRequest.setCompletedAt(new Date());
        }
        callRequestDao.save(callRequest);
        
        // Create history
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.STATUS_CHANGED,
                "Durum değişti: " + oldStatus + " -> " + newStatus,
                null,
                "System",
                oldStatus,
                newStatus,
                comment,
                siteModel
        );
        
        // Publish event if completed
        if (newStatus == CallRequestStatus.COMPLETED) {
            publishCallRequestEvent(callRequest, "COMPLETED");
        }
        
        log.info("Call request {} durumu güncellendi: {} -> {}", callRequestId, oldStatus, newStatus);
    }
    
    @Override
    public void publishCallRequestEvent(CallRequestModel callRequestModel, String eventType) {
        try {
            var siteModel = siteService.getCurrentSite();
            // Get user group emails from parameter
            String userGroupCodes = parameterService.getValueByCode("call.center.group", callRequestModel.getSite());
            
            List<String> userEmails = new ArrayList<>();
            if (userGroupCodes != null && !userGroupCodes.isEmpty()) {
                String[] groups = userGroupCodes.split(";");
                for (String groupCode : groups) {
                    List<UserModel> users = userService.getUsersByGroupCode(groupCode.trim());
                    users.forEach(user -> {
                        if (user.getEmail() != null) {
                            userEmails.add(user.getEmail());
                        }
                    });
                }
            }
            
            // Get email template from database
            String templateCode = "call_request_notification";
            var emailTemplate = emailTemplateService.getEmailTemplateByCode(templateCode, callRequestModel.getSite());
            
            // Extract variables using generic template service
            Map<String, Object> variables = genericTemplateService.extractVariables(callRequestModel, "CallRequestModel");
            
            // Process template with variables
            String processedSubject = genericTemplateService.processTemplate(emailTemplate.getSubject(), variables);
            String processedBody = genericTemplateService.processTemplate(emailTemplate.getBody(), variables);
            
            // Create event DTO
            Map<String, Object> event = new HashMap<>();
            event.put("subject", processedSubject);
            event.put("body", processedBody);
            event.put("recipients", userEmails); // Generic field name
            event.put("source", "CallRequest"); // Hangi modülden geldiğini belirt
            event.put("sourceId", callRequestModel.getId());
            
            // Send to RabbitMQ
            rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTING_KEY, event);
            
            // Create history for email sent
            callRequestHistoryService.createHistory(
                    callRequestModel,
                    CallRequestActionType.EMAIL_SENT,
                    "Email gönderildi: " + userEmails.size() + " alıcı",
                    null,
                    "System",
                    null,
                    null,
                    null,
                    siteModel
            );
            
            log.info("Call request event published: {} - {}", callRequestModel.getId(), eventType);
        } catch (Exception e) {
            log.error("Call request event gönderilemedi: {}", e.getMessage(), e);
        }
    }
}
