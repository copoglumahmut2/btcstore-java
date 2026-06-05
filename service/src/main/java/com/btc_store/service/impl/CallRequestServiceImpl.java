package com.btc_store.service.impl;

import com.btc_store.domain.enums.CallRequestActionType;
import com.btc_store.domain.enums.CallRequestPriority;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.CallRequestModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.persistence.dao.CallRequestDao;
import com.btc_store.service.*;
import com.btc_store.service.user.UserGroupService;
import com.btc_store.service.user.UserService;
import com.btc_store.service.util.ServiceUtils;
import constant.MessageConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.Messages;

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
    private final SearchService searchService;
    
    private static final String EMAIL_EXCHANGE = "email.exchange";
    private static final String EMAIL_ROUTING_KEY = "email.routing.key";
    
    /**
     * Get priority label and class for email templates
     * Supports multiple languages: tr, en, de, fr, es
     */
    private String[] getPriorityInfo(CallRequestPriority priority, SiteModel site) {
        String locale = "tr"; // Default to Turkish
        
        // Get locale from site's language
        if (site != null && site.getLanguage() != null && site.getLanguage().getCode() != null) {
            locale = site.getLanguage().getCode();
        }
        
        String label = switch (priority) {
            case LOW -> switch (locale) {
                case "en" -> "Low";
                case "de" -> "Niedrig";
                case "fr" -> "Faible";
                case "es" -> "Bajo";
                default -> "Düşük"; // Turkish
            };
            case MEDIUM -> switch (locale) {
                case "en" -> "Medium";
                case "de" -> "Mittel";
                case "fr" -> "Moyen";
                case "es" -> "Medio";
                default -> "Orta"; // Turkish
            };
            case HIGH -> switch (locale) {
                case "en" -> "High";
                case "de" -> "Hoch";
                case "fr" -> "Élevé";
                case "es" -> "Alto";
                default -> "Yüksek"; // Turkish
            };
            case URGENT -> switch (locale) {
                case "en" -> "Urgent";
                case "de" -> "Dringend";
                case "fr" -> "Urgent";
                case "es" -> "Urgente";
                default -> "Acil"; // Turkish
            };
        };
        
        String cssClass = priority.name().toLowerCase();
        return new String[]{label, cssClass};
    }
    
    @Override
    @Transactional
    public CallRequestModel createCallRequest(CallRequestModel callRequestModel, String isoCode) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel saved = modelService.save(callRequestModel);

        callRequestHistoryService.createHistory(
                saved,
                CallRequestActionType.CREATED,
                MessageConstant.CALL_REQUEST_HISTORY_CREATED,
                null,
                null,
                "System",
                null,
                saved.getStatus(),
                null,
                siteModel
        );

        // Eğer product varsa ve sorumlu kullanıcılar varsa, onlara email gönder
        if (saved.getProduct() != null && 
            saved.getProduct().getResponsibleUsers() != null && 
            !saved.getProduct().getResponsibleUsers().isEmpty()) {
            
            // Product contact için history kaydı
            String userNames = saved.getProduct().getResponsibleUsers().stream()
                    .map(user -> user.getUsername())
                    .collect(java.util.stream.Collectors.joining(", "));
            
            callRequestHistoryService.createHistory(
                    saved,
                    CallRequestActionType.ASSIGNED_TO_USER,
                    MessageConstant.CALL_REQUEST_HISTORY_ASSIGNED_TO_USER_AUTO,
                    userNames,
                    null,
                    "System",
                    CallRequestStatus.PENDING,
                    saved.getStatus(),
                    null,
                    siteModel
            );
            
            publishProductContactEmail(saved, saved.getProduct(), isoCode);
            
            log.info("Call request {} ürün sorumlu kullanıcılarına atandı: {}", saved.getId(), userNames);
        }
        // Eğer gruplara atanmışsa, gruplara email gönder
        else if (CallRequestStatus.ASSIGNED.equals(saved.getStatus()) && !saved.getAssignedGroups().isEmpty()) {
            String groupNames = saved.getAssignedGroups().stream()
                    .map(group -> group.getCode())
                    .collect(java.util.stream.Collectors.joining(", "));
            
            callRequestHistoryService.createHistory(
                    saved,
                    CallRequestActionType.ASSIGNED_TO_GROUP,
                    MessageConstant.CALL_REQUEST_HISTORY_ASSIGNED_TO_GROUP_AUTO,
                    groupNames,
                    null,
                    "System",
                    CallRequestStatus.PENDING,
                    CallRequestStatus.ASSIGNED,
                    null,
                    siteModel
            );

            publishCallRequestEvent(saved, "CREATED", isoCode);
            
            log.info("Call request {} otomatik olarak gruplara atandı: {}", saved.getId(), groupNames);
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
    public List<CallRequestModel> getMyCallRequests() {
        var currentUser = userService.getCurrentUser();
        var siteModel = siteService.getCurrentSite();
        
        log.info("=== DEBUG: Getting call requests for user: {} (id: {})", 
                currentUser.getUsername(), currentUser.getId());
        
        // Get user's groups
        var userGroups = currentUser.getUserGroups();
        log.info("=== DEBUG: User has {} groups", userGroups != null ? userGroups.size() : 0);
        if (userGroups != null && !userGroups.isEmpty()) {
            userGroups.forEach(g -> log.info("=== DEBUG: Group: {} (id: {})", g.getCode(), g.getId()));
        }
        
        // Build query like WorkListHeaderModel
        var queryBuilder = new StringBuilder();
        var params = new HashMap<String, Object>();
        
        queryBuilder.append("SELECT DISTINCT cr FROM CallRequestModel cr ")
                .append("LEFT JOIN cr.assignedUsers u ")
                .append("LEFT JOIN cr.assignedGroups g ")
                .append("WHERE cr.site = :site ")
                .append("AND cr.status IN :statuses ")
                .append("AND (u IN :users OR g IN :groups) ")
                .append("ORDER BY cr.createdDate DESC");
        
        // Prepare user set
        var allUsers = new HashSet<UserModel>();
        allUsers.add(currentUser);
        
        // Prepare group set
        var allGroups = new HashSet<com.btc_store.domain.model.custom.user.UserGroupModel>();
        if (userGroups != null && !userGroups.isEmpty()) {
            allGroups.addAll(userGroups);
        }
        
        params.put("site", siteModel);
        params.put("statuses", List.of(CallRequestStatus.ASSIGNED, CallRequestStatus.IN_PROGRESS));
        params.put("users", allUsers);
        params.put("groups", allGroups);
        
        log.info("=== DEBUG: Query: {}", queryBuilder.toString());
        log.info("=== DEBUG: Site: {}, Statuses: {}, Users count: {}, Groups count: {}", 
                siteModel.getCode(), params.get("statuses"), allUsers.size(), allGroups.size());
        
        var result = searchService.search(CallRequestModel.class, queryBuilder.toString(), params);
        
        log.info("=== DEBUG: Found {} call requests", result.size());
        
        return result;
    }
    
    @Override
    public Page<CallRequestModel> getMyCallRequestsPageable(Pageable pageable) {
        var currentUser = userService.getCurrentUser();
        var siteModel = siteService.getCurrentSite();
        
        // Get user's groups
        var userGroups = currentUser.getUserGroups();
        
        // Build query exactly like WorkListHeaderModel
        var queryBuilder = new StringBuilder();
        var params = new HashMap<String, Object>();
        
        queryBuilder.append("select cr from CallRequestModel cr ")
                .append("left join cr.assignedUsers u ")
                .append("left join cr.assignedGroups g ")
                .append("where cr.site = :site ")
                .append("and cr.status in :statuses ")
                .append("and (u in :users or g in :groups)");
        
        // Prepare user set
        var allUsers = new HashSet<UserModel>();
        allUsers.add(currentUser);
        
        // Prepare group set
        var allGroups = new HashSet<UserGroupModel>();
        if (userGroups != null && !userGroups.isEmpty()) {
            allGroups.addAll(userGroups);
        }
        
        params.put("site", siteModel);
        params.put("statuses", List.of(CallRequestStatus.ASSIGNED, CallRequestStatus.IN_PROGRESS));
        params.put("users", allUsers);
        params.put("groups", allGroups);
        
        var result = searchService.search(CallRequestModel.class, pageable, queryBuilder.toString(), params);
        
        log.info("Found {} call requests (page) for user: {} (userId: {}, groups: {})", 
                result.getTotalElements(), currentUser.getUsername(), currentUser.getId(), allGroups.size());
        
        return result;
    }
    
    @Override
    @Transactional
    public void assignToGroup(Long callRequestId, String groupCode, String isoCode) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        
        if (CallRequestStatus.CLOSED.equals(callRequest.getStatus())) {
            throw new IllegalStateException(Messages.getMessageForIsoCode(MessageConstant.CALL_REQUEST_CLOSED_OPERATION_NOT_ALLOWED, isoCode));
        }
        
        CallRequestStatus oldStatus = callRequest.getStatus();
        
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
        
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.ASSIGNED_TO_GROUP,
                MessageConstant.CALL_REQUEST_HISTORY_ASSIGNED_TO_GROUP,
                groupCode + "|" + assignedBy,
                assignedByUserId,
                assignedBy,
                oldStatus,
                CallRequestStatus.ASSIGNED,
                null,
                siteModel
        );
        
        publishCallRequestEventToGroup(callRequest, groupCode, "ASSIGNED_TO_GROUP", isoCode);
        
        log.info("Call request {} gruba atandı: {} (Atayan: {})", callRequestId, groupCode, assignedBy);
    }
    
    @Override
    @Transactional
    public void assignToGroups(Long callRequestId, List<String> groupCodes, String isoCode) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        
        if (CallRequestStatus.CLOSED.equals(callRequest.getStatus())) {
            throw new IllegalStateException(Messages.getMessageForIsoCode(MessageConstant.CALL_REQUEST_CLOSED_OPERATION_NOT_ALLOWED, isoCode));
        }
        
        CallRequestStatus oldStatus = callRequest.getStatus();
        
        String assignedBy = "System";
        Long assignedByUserId = null;
        try {
            var currentUser = userService.getCurrentUser();
            assignedBy = currentUser.getUsername();
            assignedByUserId = currentUser.getId();
        } catch (Exception e) {
            log.warn("Could not get current user: {}", e.getMessage());
        }
        
        callRequest.getAssignedGroups().clear();
        for (String groupCode : groupCodes) {
            var userGroup = userGroupService.getUserGroupModel(groupCode, siteModel);
            callRequest.getAssignedGroups().add(userGroup);
        }
        
        callRequest.setStatus(CallRequestStatus.ASSIGNED);
        modelService.save(callRequest);
        
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.ASSIGNED_TO_GROUP,
                MessageConstant.CALL_REQUEST_HISTORY_ASSIGNED_TO_GROUPS,
                String.join(", ", groupCodes) + "|" + assignedBy,
                assignedByUserId,
                assignedBy,
                oldStatus,
                CallRequestStatus.ASSIGNED,
                null,
                siteModel
        );
        
        for (String groupCode : groupCodes) {
            publishCallRequestEventToGroup(callRequest, groupCode, "ASSIGNED_TO_GROUP", isoCode);
        }
        
        log.info("Call request {} gruplara atandı: {} (Atayan: {})", callRequestId, String.join(", ", groupCodes), assignedBy);
    }
    
    @Override
    @Transactional
    public void assignToUser(Long callRequestId, Long userId, String isoCode) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        
        if (CallRequestStatus.CLOSED.equals(callRequest.getStatus())) {
            throw new IllegalStateException(Messages.getMessageForIsoCode(MessageConstant.CALL_REQUEST_CLOSED_OPERATION_NOT_ALLOWED, isoCode));
        }
        
        UserModel user = userService.getUserModelById(userId);
        CallRequestStatus oldStatus = callRequest.getStatus();
        
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
        
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.ASSIGNED_TO_USER,
                MessageConstant.CALL_REQUEST_HISTORY_ASSIGNED_TO_USER,
                user.getUsername() + "|" + assignedBy,
                assignedByUserId,
                assignedBy,
                oldStatus,
                CallRequestStatus.IN_PROGRESS,
                null,
                siteModel
        );
        
        publishCallRequestEventToUser(callRequest, user, "ASSIGNED_TO_USER", isoCode);
        
        log.info("Call request {} kullanıcıya atandı: {} (Atayan: {})", callRequestId, userId, assignedBy);
    }
    
    @Override
    @Transactional
    public void assignToUsers(Long callRequestId, List<Long> userIds, String isoCode) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        
        if (CallRequestStatus.CLOSED.equals(callRequest.getStatus())) {
            throw new IllegalStateException(Messages.getMessageForIsoCode(MessageConstant.CALL_REQUEST_CLOSED_OPERATION_NOT_ALLOWED, isoCode));
        }
        
        CallRequestStatus oldStatus = callRequest.getStatus();
        
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
        
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.ASSIGNED_TO_USER,
                MessageConstant.CALL_REQUEST_HISTORY_ASSIGNED_TO_USERS,
                String.join(", ", usernames) + "|" + assignedBy,
                assignedByUserId,
                assignedBy,
                oldStatus,
                CallRequestStatus.IN_PROGRESS,
                null,
                siteModel
        );
        
        for (UserModel user : assignedUsers) {
            publishCallRequestEventToUser(callRequest, user, "ASSIGNED_TO_USER", isoCode);
        }
        
        log.info("Call request {} kullanıcılara atandı: {} (Atayan: {})", callRequestId, String.join(", ", usernames), assignedBy);
    }
    
    @Override
    @Transactional
    public void closeCallRequest(Long callRequestId, String comment, String isoCode) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        CallRequestStatus oldStatus = callRequest.getStatus();
        
        callRequest.setStatus(CallRequestStatus.CLOSED);
        callRequest.setCompletedAt(new Date());
        callRequestDao.save(callRequest);
        
        String closedBy = "System";
        Long closedByUserId = null;
        try {
            var currentUser = userService.getCurrentUser();
            closedBy = currentUser.getUsername();
            closedByUserId = currentUser.getId();
        } catch (Exception e) {
            log.warn("Could not get current user: {}", e.getMessage());
        }
        
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.STATUS_CHANGED,
                MessageConstant.CALL_REQUEST_HISTORY_CLOSED,
                closedBy,
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
    public void updateStatus(Long callRequestId, CallRequestStatus newStatus, String comment, String isoCode) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        CallRequestStatus oldStatus = callRequest.getStatus();
        
        if (CallRequestStatus.CLOSED.equals(oldStatus) && !CallRequestStatus.CLOSED.equals(newStatus)) {
            throw new IllegalStateException(Messages.getMessageForIsoCode(MessageConstant.CALL_REQUEST_CLOSED_OPERATION_NOT_ALLOWED, isoCode));
        }
        
        callRequest.setStatus(newStatus);
        if (newStatus == CallRequestStatus.COMPLETED) {
            callRequest.setCompletedAt(new Date());
        }
        callRequestDao.save(callRequest);
        
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.STATUS_CHANGED,
                MessageConstant.CALL_REQUEST_HISTORY_STATUS_CHANGED,
                oldStatus + "|" + newStatus,
                null,
                "System",
                oldStatus,
                newStatus,
                comment,
                siteModel
        );
        
        // Publish event if completed
        if (newStatus == CallRequestStatus.COMPLETED) {
            publishCallRequestEvent(callRequest, "COMPLETED", isoCode);
        }
        
        log.info("Call request {} durumu güncellendi: {} -> {}", callRequestId, oldStatus, newStatus);
    }
    
    @Override
    @Transactional
    public void updatePriority(Long callRequestId, CallRequestPriority newPriority, String isoCode) {
        var siteModel = siteService.getCurrentSite();
        CallRequestModel callRequest = getCallRequestById(callRequestId);
        
        if (CallRequestStatus.CLOSED.equals(callRequest.getStatus())) {
            throw new IllegalStateException(Messages.getMessageForIsoCode(MessageConstant.CALL_REQUEST_CLOSED_OPERATION_NOT_ALLOWED, isoCode));
        }
        
        CallRequestPriority oldPriority = callRequest.getPriority();
        
        String updatedBy = "System";
        Long updatedByUserId = null;
        try {
            var currentUser = userService.getCurrentUser();
            updatedBy = currentUser.getUsername();
            updatedByUserId = currentUser.getId();
        } catch (Exception e) {
            log.warn("Could not get current user: {}", e.getMessage());
        }
        
        callRequest.setPriority(newPriority);
        modelService.save(callRequest);
        
        callRequestHistoryService.createHistory(
                callRequest,
                CallRequestActionType.PRIORITY_CHANGED,
                MessageConstant.CALL_REQUEST_HISTORY_PRIORITY_CHANGED,
                oldPriority + "|" + newPriority + "|" + updatedBy,
                updatedByUserId,
                updatedBy,
                null,
                null,
                null,
                siteModel
        );
        
        log.info("Call request {} önceliği güncellendi: {} -> {} (Güncelleyen: {})", callRequestId, oldPriority, newPriority, updatedBy);
    }
    
    @Override
    public void publishCallRequestEvent(CallRequestModel callRequestModel, String eventType, String isoCode) {
        try {
            var siteModel = siteService.getCurrentSite();
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
            
            String templateCode = "call_request_notification";
            var emailTemplate = emailTemplateService.getEmailTemplateByCode(templateCode, callRequestModel.getSite());
            
            Map<String, Object> variables = genericTemplateService.extractVariables(callRequestModel, "CallRequestModel");
            
            String[] priorityInfo = getPriorityInfo(callRequestModel.getPriority(), callRequestModel.getSite());
            variables.put("priority", priorityInfo[0]);
            variables.put("priorityClass", priorityInfo[1]);
            
            if (callRequestModel.getCreatedDate() != null) {
                variables.put("createdDate", new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(callRequestModel.getCreatedDate()));
            } else {
                variables.put("createdDate", "");
            }
            
            String processedSubject = genericTemplateService.processTemplate(emailTemplate.getSubject(), variables);
            String processedBody = genericTemplateService.processTemplate(emailTemplate.getBody(), variables);
            
            Map<String, Object> event = new HashMap<>();
            event.put("subject", processedSubject);
            event.put("body", processedBody);
            event.put("recipients", userEmails);
            event.put("siteCode", callRequestModel.getSite().getCode());
            event.put("source", "CallRequest");
            event.put("sourceId", callRequestModel.getId());
            
            rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTING_KEY, event);
            
            String emailList = String.join(", ", userEmails);
            callRequestHistoryService.createHistory(
                    callRequestModel,
                    CallRequestActionType.EMAIL_SENT,
                    MessageConstant.CALL_REQUEST_HISTORY_EMAIL_SENT,
                    emailList,
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
    
    private void publishCallRequestEventToUser(CallRequestModel callRequestModel, UserModel user, String eventType, String isoCode) {
        try {
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                log.warn("User {} has no email address", user.getUsername());
                return;
            }
            
            var siteModel = siteService.getCurrentSite();
            
            // Get email template from database - kullanıcıya atama için özel template
            String templateCode = "call_request_assigned_to_user";
            var emailTemplate = emailTemplateService.getEmailTemplateByCode(templateCode, callRequestModel.getSite());
            
            // Extract variables using generic template service
            Map<String, Object> variables = genericTemplateService.extractVariables(callRequestModel, "CallRequestModel");
            
            // Add user-specific variables
            variables.put("assignedUserName", user.getUsername());
            
            // Get current user who made the assignment
            try {
                var currentUser = userService.getCurrentUser();
                variables.put("assignedBy", currentUser.getUsername());
            } catch (Exception e) {
                variables.put("assignedBy", "System");
            }
            
            // Add additional variables not in model - with locale support
            String[] priorityInfo = getPriorityInfo(callRequestModel.getPriority(), callRequestModel.getSite());
            variables.put("priority", priorityInfo[0]);
            variables.put("priorityClass", priorityInfo[1]);
            
            // Format created date if exists
            if (callRequestModel.getCreatedDate() != null) {
                variables.put("createdDate", new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(callRequestModel.getCreatedDate()));
            } else {
                variables.put("createdDate", "");
            }
            
            // Add call request URL (adjust base URL as needed)
            String baseUrl = parameterService.getValueByCode("frontend.base.url", callRequestModel.getSite());
            if (baseUrl == null || baseUrl.isEmpty()) {
                baseUrl = "http://localhost:3000"; // Fallback
            }
            variables.put("callRequestUrl", baseUrl + "/call-requests/" + callRequestModel.getId());
            
            // Process template with variables
            String processedSubject = genericTemplateService.processTemplate(emailTemplate.getSubject(), variables);
            String processedBody = genericTemplateService.processTemplate(emailTemplate.getBody(), variables);
            
            // Create event DTO
            Map<String, Object> event = new HashMap<>();
            event.put("subject", processedSubject);
            event.put("body", processedBody);
            event.put("recipients", List.of(user.getEmail()));
            event.put("siteCode", callRequestModel.getSite().getCode());
            event.put("source", "CallRequest");
            event.put("sourceId", callRequestModel.getId());
            
            // Send to RabbitMQ
            rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTING_KEY, event);
            
            // Create history for email sent
            callRequestHistoryService.createHistory(
                    callRequestModel,
                    CallRequestActionType.EMAIL_SENT,
                    MessageConstant.CALL_REQUEST_HISTORY_EMAIL_SENT_USER_ASSIGNED,
                    user.getEmail(),
                    null,
                    "System",
                    null,
                    null,
                    null,
                    siteModel
            );
            
            log.info("Call request assignment email sent to user: {} - {} - {}", user.getUsername(), callRequestModel.getId(), eventType);
        } catch (Exception e) {
            log.error("Call request event kullanıcıya gönderilemedi: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Send email notification to all members of a group
     */
    private void publishCallRequestEventToGroup(CallRequestModel callRequestModel, String groupCode, String eventType, String isoCode) {
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
            
            // Get email template from database - gruba atama için özel template
            String templateCode = "call_request_assigned_to_group";
            var emailTemplate = emailTemplateService.getEmailTemplateByCode(templateCode, callRequestModel.getSite());
            
            // Extract variables using generic template service
            Map<String, Object> variables = genericTemplateService.extractVariables(callRequestModel, "CallRequestModel");
            
            // Add group-specific variables
            variables.put("groupName", groupCode);
            
            // Get current user who made the assignment
            try {
                var currentUser = userService.getCurrentUser();
                variables.put("assignedBy", currentUser.getUsername());
            } catch (Exception e) {
                variables.put("assignedBy", "System");
            }
            
            // Add additional variables not in model - with locale support
            String[] priorityInfo = getPriorityInfo(callRequestModel.getPriority(), callRequestModel.getSite());
            variables.put("priority", priorityInfo[0]);
            variables.put("priorityClass", priorityInfo[1]);
            
            // Format created date if exists
            if (callRequestModel.getCreatedDate() != null) {
                variables.put("createdDate", new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(callRequestModel.getCreatedDate()));
            } else {
                variables.put("createdDate", "");
            }
            
            // Add call request URL (adjust base URL as needed)
            String baseUrl = parameterService.getValueByCode("site.backoffice.url", callRequestModel.getSite());
            if (baseUrl == null || baseUrl.isEmpty()) {
                baseUrl = "http://localhost:3000"; // Fallback
            }
            variables.put("callRequestUrl", baseUrl + "/call-requests/" + callRequestModel.getId());
            
            // Process template with variables
            String processedSubject = genericTemplateService.processTemplate(emailTemplate.getSubject(), variables);
            String processedBody = genericTemplateService.processTemplate(emailTemplate.getBody(), variables);
            
            // Create event DTO
            Map<String, Object> event = new HashMap<>();
            event.put("subject", processedSubject);
            event.put("body", processedBody);
            event.put("recipients", userEmails);
            event.put("siteCode", callRequestModel.getSite().getCode());
            event.put("source", "CallRequest");
            event.put("sourceId", callRequestModel.getId());
            
            // Send to RabbitMQ
            rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTING_KEY, event);
            
            // Create history for email sent
            callRequestHistoryService.createHistory(
                    callRequestModel,
                    CallRequestActionType.EMAIL_SENT,
                    MessageConstant.CALL_REQUEST_HISTORY_EMAIL_SENT_GROUP_ASSIGNED,
                    groupCode + "|" + String.join(", ", userEmails),
                    null,
                    "System",
                    null,
                    null,
                    null,
                    siteModel
            );
            
            log.info("Call request assignment email sent to group: {} - {} users - {} - {}", groupCode, userEmails.size(), callRequestModel.getId(), eventType);
        } catch (Exception e) {
            log.error("Call request event gruba gönderilemedi: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Send email notification to product responsible users
     */
    private void publishProductContactEmail(CallRequestModel callRequestModel, com.btc_store.domain.model.custom.ProductModel product, String isoCode) {
        try {
            // Get responsible users' emails
            List<String> userEmails = product.getResponsibleUsers().stream()
                    .filter(user -> user.getEmail() != null && !user.getEmail().isEmpty())
                    .map(UserModel::getEmail)
                    .distinct()
                    .toList();
            
            if (userEmails.isEmpty()) {
                log.warn("No email addresses found for product responsible users");
                return;
            }
            
            var siteModel = siteService.getCurrentSite();
            
            // Get email template from database - ürün iletişimi için özel template
            String templateCode = "product_contact_request";
            var emailTemplate = emailTemplateService.getEmailTemplateByCode(templateCode, callRequestModel.getSite());
            
            // Extract variables using generic template service
            Map<String, Object> variables = genericTemplateService.extractVariables(callRequestModel, "CallRequestModel");
            
            // Product information
            variables.put("productCode", product.getCode());
            variables.put("productName", product.getName() != null && product.getName().getTr() != null 
                ? product.getName().getTr() : product.getCode());
            variables.put("productDescription", product.getShortDescription() != null && product.getShortDescription().getTr() != null 
                ? product.getShortDescription().getTr() : "");
            
            // Add priority translation based on site locale
            String[] priorityInfo = getPriorityInfo(callRequestModel.getPriority(), callRequestModel.getSite());
            variables.put("priority", priorityInfo[0]);
            variables.put("priorityClass", priorityInfo[1]);
            
            // Format created date if exists
            if (callRequestModel.getCreatedDate() != null) {
                variables.put("createdDate", new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(callRequestModel.getCreatedDate()));
            } else {
                variables.put("createdDate", "");
            }
            
            // Add call request URL
            String baseUrl = parameterService.getValueByCode("site.backoffice.url", callRequestModel.getSite());
            if (baseUrl == null || baseUrl.isEmpty()) {
                baseUrl = "http://localhost:3000";
            }
            variables.put("callRequestUrl", baseUrl + "/call-requests/" + callRequestModel.getId());
            
            // Process template with variables
            String processedSubject = genericTemplateService.processTemplate(emailTemplate.getSubject(), variables);
            String processedBody = genericTemplateService.processTemplate(emailTemplate.getBody(), variables);
            
            // Create event DTO for RabbitMQ
            Map<String, Object> event = new HashMap<>();
            event.put("subject", processedSubject);
            event.put("body", processedBody);
            event.put("recipients", userEmails);
            event.put("siteCode", callRequestModel.getSite().getCode());
            event.put("source", "ProductContact");
            event.put("sourceId", callRequestModel.getId());
            
            // Send to RabbitMQ
            rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTING_KEY, event);
            
            // Create history for email sent
            callRequestHistoryService.createHistory(
                    callRequestModel,
                    CallRequestActionType.EMAIL_SENT,
                    MessageConstant.CALL_REQUEST_HISTORY_EMAIL_SENT_PRODUCT_CONTACT,
                    String.join(", ", userEmails),
                    null,
                    "System",
                    null,
                    null,
                    null,
                    siteModel
            );
            
            log.info("Product contact email notification sent for call request: {} to {} users", callRequestModel.getId(), userEmails.size());
        } catch (Exception e) {
            log.error("Failed to send product contact email: {}", e.getMessage(), e);
        }
    }
}

