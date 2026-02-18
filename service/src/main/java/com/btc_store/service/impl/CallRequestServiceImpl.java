package com.btc_store.service.impl;

import com.btc_store.domain.enums.CallRequestActionType;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.CallRequestModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.persistence.dao.CallRequestDao;
import com.btc_store.service.CallRequestHistoryService;
import com.btc_store.service.CallRequestService;
import com.btc_store.service.EmailTemplateService;
import com.btc_store.service.GenericTemplateService;
import com.btc_store.service.ModelService;
import com.btc_store.service.ParameterService;
import com.btc_store.service.SiteService;
import com.btc_store.service.user.UserGroupService;
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
    private final UserGroupService userGroupService;
    private final RabbitTemplate rabbitTemplate;
    private final GenericTemplateService genericTemplateService;
    private final EmailTemplateService emailTemplateService;
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
        if (CallRequestStatus.ASSIGNED.equals(saved.getStatus()) && !saved.getAssignedGroups().isEmpty()) {
            // Get group names
            String groupNames = saved.getAssignedGroups().stream()
                    .map(group -> group.getCode())
                    .collect(java.util.stream.Collectors.joining(", "));
            
            callRequestHistoryService.createHistory(
                    saved,
                    CallRequestActionType.ASSIGNED_TO_GROUP,
                    "Otomatik olarak gruplara atandı: " + groupNames,
                    null,
                    "System",
                    CallRequestStatus.PENDING,
                    CallRequestStatus.ASSIGNED,
                    null,
                    siteModel
            );
            
            // Send email notification to all assigned groups
            for (var group : saved.getAssignedGroups()) {
                publishCallRequestEventToGroup(saved, group.getCode(), "AUTO_ASSIGNED_TO_GROUP");
            }
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
        
        // Check if request is closed
        if (CallRequestStatus.CLOSED.equals(callRequest.getStatus())) {
            throw new IllegalStateException("Kapalı çağrılarda işlem yapılamaz");
        }
        
        CallRequestStatus oldStatus = callRequest.getStatus();
        
        // Get current user who is making the assignment
        String assignedBy = "System";
        Long assignedByUserId = null;
        try {
            var currentUser = userService.getCurrentUser();
            assignedBy = currentUser.getUsername();
            assignedByUserId = currentUser.getId();
        } catch (Exception e) {
            log.warn("Could not get current user: {}", e.getMessage());
        }
        
        var userGroup = userGroupService.getUserGroupModel(groupCode, siteModel);
        callRequest.getAssignedGroups().clear();
        callRequest.getAssignedGroups().add(userGroup);
        callRequest.setStatus(CallRequestStatus.ASSIGNED);
        modelService.save(callRequest);
        
        // Create history
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.ASSIGNED_TO_GROUP,
                "Gruba atandı: " + groupCode + " (Atayan: " + assignedBy + ")",
                assignedByUserId,
                assignedBy,
                oldStatus,
                CallRequestStatus.ASSIGNED,
                null,
                siteModel
        );
        
        // Send email notification to group members
        publishCallRequestEventToGroup(callRequest, groupCode, "ASSIGNED_TO_GROUP");
        
        log.info("Call request {} gruba atandı: {} (Atayan: {})", callRequestId, groupCode, assignedBy);
    }
    
    @Override
    @Transactional
    public void assignToGroups(Long callRequestId, List<String> groupCodes) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        
        // Check if request is closed
        if (CallRequestStatus.CLOSED.equals(callRequest.getStatus())) {
            throw new IllegalStateException("Kapalı çağrılarda işlem yapılamaz");
        }
        
        CallRequestStatus oldStatus = callRequest.getStatus();
        
        // Get current user who is making the assignment
        String assignedBy = "System";
        Long assignedByUserId = null;
        try {
            var currentUser = userService.getCurrentUser();
            assignedBy = currentUser.getUsername();
            assignedByUserId = currentUser.getId();
        } catch (Exception e) {
            log.warn("Could not get current user: {}", e.getMessage());
        }
        
        // Clear existing groups and add new ones
        callRequest.getAssignedGroups().clear();
        
        for (String groupCode : groupCodes) {
            var userGroup = userGroupService.getUserGroupModel(groupCode, siteModel);
            callRequest.getAssignedGroups().add(userGroup);
        }
        
        callRequest.setStatus(CallRequestStatus.ASSIGNED);
        modelService.save(callRequest);
        
        // Create history
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.ASSIGNED_TO_GROUP,
                "Gruplara atandı: " + String.join(", ", groupCodes) + " (Atayan: " + assignedBy + ")",
                assignedByUserId,
                assignedBy,
                oldStatus,
                CallRequestStatus.ASSIGNED,
                null,
                siteModel
        );
        
        // Send email notification to all group members
        for (String groupCode : groupCodes) {
            publishCallRequestEventToGroup(callRequest, groupCode, "ASSIGNED_TO_GROUP");
        }
        
        log.info("Call request {} gruplara atandı: {} (Atayan: {})", callRequestId, String.join(", ", groupCodes), assignedBy);
    }
    
    @Override
    @Transactional
    public void assignToUser(Long callRequestId, Long userId) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        
        // Check if request is closed
        if (CallRequestStatus.CLOSED.equals(callRequest.getStatus())) {
            throw new IllegalStateException("Kapalı çağrılarda işlem yapılamaz");
        }
        
        UserModel user = userService.getUserModelById(userId);
        CallRequestStatus oldStatus = callRequest.getStatus();
        
        // Get current user who is making the assignment
        String assignedBy = "System";
        Long assignedByUserId = null;
        try {
            var currentUser = userService.getCurrentUser();
            assignedBy = currentUser.getUsername();
            assignedByUserId = currentUser.getId();
        } catch (Exception e) {
            log.warn("Could not get current user: {}", e.getMessage());
        }
        
        callRequest.getAssignedUsers().clear();
        callRequest.getAssignedUsers().add(user);
        callRequest.setStatus(CallRequestStatus.IN_PROGRESS);
        modelService.save(callRequest);
        
        // Create history
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.ASSIGNED_TO_USER,
                "Kullanıcıya atandı: " + user.getUsername() + " (Atayan: " + assignedBy + ")",
                assignedByUserId,
                assignedBy,
                oldStatus,
                CallRequestStatus.IN_PROGRESS,
                null,
                siteModel
        );
        
        // Send email notification to assigned user
        publishCallRequestEventToUser(callRequest, user, "ASSIGNED_TO_USER");
        
        log.info("Call request {} kullanıcıya atandı: {} (Atayan: {})", callRequestId, userId, assignedBy);
    }
    
    @Override
    @Transactional
    public void assignToUsers(Long callRequestId, List<Long> userIds) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        
        // Check if request is closed
        if (CallRequestStatus.CLOSED.equals(callRequest.getStatus())) {
            throw new IllegalStateException("Kapalı çağrılarda işlem yapılamaz");
        }
        
        CallRequestStatus oldStatus = callRequest.getStatus();
        
        // Get current user who is making the assignment
        String assignedBy = "System";
        Long assignedByUserId = null;
        try {
            var currentUser = userService.getCurrentUser();
            assignedBy = currentUser.getUsername();
            assignedByUserId = currentUser.getId();
        } catch (Exception e) {
            log.warn("Could not get current user: {}", e.getMessage());
        }
        
        callRequest.getAssignedUsers().clear();
        List<String> usernames = new ArrayList<>();
        List<UserModel> assignedUsers = new ArrayList<>();
        
        for (Long userId : userIds) {
            UserModel user = userService.getUserModelById(userId);
            callRequest.getAssignedUsers().add(user);
            assignedUsers.add(user);
            usernames.add(user.getUsername());
        }
        
        callRequest.setStatus(CallRequestStatus.IN_PROGRESS);
        modelService.save(callRequest);
        
        // Create history
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.ASSIGNED_TO_USER,
                "Kullanıcılara atandı: " + String.join(", ", usernames) + " (Atayan: " + assignedBy + ")",
                assignedByUserId,
                assignedBy,
                oldStatus,
                CallRequestStatus.IN_PROGRESS,
                null,
                siteModel
        );
        
        // Send email notification to all assigned users
        for (UserModel user : assignedUsers) {
            publishCallRequestEventToUser(callRequest, user, "ASSIGNED_TO_USER");
        }
        
        log.info("Call request {} kullanıcılara atandı: {} (Atayan: {})", callRequestId, String.join(", ", usernames), assignedBy);
    }
    
    @Override
    @Transactional
    public void closeCallRequest(Long callRequestId, String comment) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        CallRequestStatus oldStatus = callRequest.getStatus();
        
        callRequest.setStatus(CallRequestStatus.CLOSED);
        callRequest.setCompletedAt(new Date());
        callRequestDao.save(callRequest);
        
        // Get current user
        String closedBy = "System";
        Long closedByUserId = null;
        try {
            var currentUser = userService.getCurrentUser();
            closedBy = currentUser.getUsername();
            closedByUserId = currentUser.getId();
        } catch (Exception e) {
            log.warn("Could not get current user: {}", e.getMessage());
        }
        
        // Create history
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.STATUS_CHANGED,
                "Çağrı kapatıldı - " + closedBy + " tarafından",
                closedByUserId,
                closedBy,
                oldStatus,
                CallRequestStatus.CLOSED,
                comment,
                siteModel
        );
        
        log.info("Call request {} kapatıldı: {} tarafından", callRequestId, closedBy);
    }
    
    @Override
    @Transactional
    public void updateStatus(Long callRequestId, CallRequestStatus newStatus, String comment) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        CallRequestStatus oldStatus = callRequest.getStatus();
        
        // Check if request is closed (except when trying to reopen)
        if (CallRequestStatus.CLOSED.equals(oldStatus) && !CallRequestStatus.CLOSED.equals(newStatus)) {
            throw new IllegalStateException("Kapalı çağrılarda işlem yapılamaz");
        }
        
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
                    "Email gönderildi: " + String.join(", ", userEmails),
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
    
    /**
     * Send email notification to a specific user
     */
    private void publishCallRequestEventToUser(CallRequestModel callRequestModel, UserModel user, String eventType) {
        try {
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                log.warn("User {} has no email address", user.getUsername());
                return;
            }
            
            var siteModel = siteService.getCurrentSite();
            
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
            event.put("recipients", List.of(user.getEmail()));
            event.put("source", "CallRequest");
            event.put("sourceId", callRequestModel.getId());
            
            // Send to RabbitMQ
            rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTING_KEY, event);
            
            // Create history for email sent
            callRequestHistoryService.createHistory(
                    callRequestModel,
                    CallRequestActionType.EMAIL_SENT,
                    "Email gönderildi: " + user.getEmail(),
                    null,
                    "System",
                    null,
                    null,
                    null,
                    siteModel
            );
            
            log.info("Call request event sent to user: {} - {} - {}", user.getUsername(), callRequestModel.getId(), eventType);
        } catch (Exception e) {
            log.error("Call request event kullanıcıya gönderilemedi: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Send email notification to all members of a group
     */
    private void publishCallRequestEventToGroup(CallRequestModel callRequestModel, String groupCode, String eventType) {
        try {
            var siteModel = siteService.getCurrentSite();
            
            // Get users in the group
            List<UserModel> users = userService.getUsersByGroupCode(groupCode);
            List<String> userEmails = new ArrayList<>();
            
            users.forEach(user -> {
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    userEmails.add(user.getEmail());
                }
            });
            
            if (userEmails.isEmpty()) {
                log.warn("No users with email found in group: {}", groupCode);
                return;
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
            event.put("recipients", userEmails);
            event.put("source", "CallRequest");
            event.put("sourceId", callRequestModel.getId());
            
            // Send to RabbitMQ
            rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTING_KEY, event);
            
            // Create history for email sent
            callRequestHistoryService.createHistory(
                    callRequestModel,
                    CallRequestActionType.EMAIL_SENT,
                    "Email gönderildi (" + groupCode + " grubu): " + String.join(", ", userEmails),
                    null,
                    "System",
                    null,
                    null,
                    null,
                    siteModel
            );
            
            log.info("Call request event sent to group: {} - {} users - {} - {}", groupCode, userEmails.size(), callRequestModel.getId(), eventType);
        } catch (Exception e) {
            log.error("Call request event gruba gönderilemedi: {}", e.getMessage(), e);
        }
    }
}
