package com.btc_store.service.interceptor.impl.callrequest;

import com.btc_store.domain.model.custom.CallRequestModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.service.EmailTemplateService;
import com.btc_store.service.ParameterService;
import com.btc_store.service.exception.interceptor.InterceptorException;
import com.btc_store.service.GenericTemplateService;
import com.btc_store.service.interceptor.AfterSaveInterceptor;
import com.btc_store.service.interceptor.Interceptor;
import com.btc_store.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CallRequest kaydedildikten sonra mail gönderir
 * RabbitTemplate optional - yoksa mail gönderilmez
 */
@Slf4j
@Component
@AfterSaveInterceptor(itemType = CallRequestModel.class)
public class CallRequestEmailNotificationInterceptor implements Interceptor<CallRequestModel> {
    
    private final ParameterService parameterService;
    private final UserService userService;
    private final EmailTemplateService emailTemplateService;
    private final GenericTemplateService genericTemplateService;
    
    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;
    
    private static final String EMAIL_EXCHANGE = "email.exchange";
    private static final String EMAIL_ROUTING_KEY = "email.routing.key";
    
    public CallRequestEmailNotificationInterceptor(
            ParameterService parameterService,
            UserService userService,
            EmailTemplateService emailTemplateService,
            GenericTemplateService genericTemplateService) {
        this.parameterService = parameterService;
        this.userService = userService;
        this.emailTemplateService = emailTemplateService;
        this.genericTemplateService = genericTemplateService;
    }
    
    @Override
    public void invoke(CallRequestModel model) throws InterceptorException {
        // Sadece yeni kayıtlar için mail gönder
        if (!model.isNewTransaction()) {
            log.debug("CallRequest güncelleme - mail gönderilmedi: {}", model.getId());
            return;
        }
        
        // RabbitTemplate yoksa mail gönderme
        if (rabbitTemplate == null) {
            log.warn("RabbitTemplate bulunamadı, mail gönderilemedi. btcstorerabbit çalışıyor mu?");
            return;
        }
        
        try {
            // Get user group emails from parameter
            String userGroupCodes = parameterService.getValueByCode("call.center.group", model.getSite());
            
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
            
            if (userEmails.isEmpty()) {
                log.warn("Call request için mail gönderilecek kullanıcı bulunamadı: {}", model.getId());
                return;
            }
            
            // Get email template from database
            String templateCode = "call_request_notification";
            var emailTemplate = emailTemplateService.getEmailTemplateByCode(templateCode, model.getSite());
            
            // Extract variables using generic template service
            Map<String, Object> variables = genericTemplateService.extractVariables(model, "CallRequestModel");
            
            // Process template with variables
            String processedSubject = genericTemplateService.processTemplate(emailTemplate.getSubject(), variables);
            String processedBody = genericTemplateService.processTemplate(emailTemplate.getBody(), variables);
            
            // Create event DTO
            Map<String, Object> event = new HashMap<>();
            event.put("subject", processedSubject);
            event.put("body", processedBody);
            event.put("recipients", userEmails);
            event.put("source", "CallRequest");
            event.put("sourceId", model.getId());
            event.put("siteCode", model.getSite().getCode()); // Site code'u ekle
            
            // Send to RabbitMQ
            rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, EMAIL_ROUTING_KEY, event);
            
            log.info("Call request email event gönderildi: {} - {} alıcı", model.getId(), userEmails.size());
        } catch (Exception e) {
            log.error("Call request email gönderimi sırasında hata: {}", e.getMessage(), e);
            // Mail gönderilemese bile işlem devam etsin
        }
    }
}
