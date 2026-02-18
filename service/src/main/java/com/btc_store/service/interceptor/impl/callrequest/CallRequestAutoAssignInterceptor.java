package com.btc_store.service.interceptor.impl.callrequest;

import com.btc_store.domain.enums.CallRequestActionType;
import com.btc_store.domain.enums.CallRequestStatus;
import com.btc_store.domain.model.custom.CallRequestModel;
import com.btc_store.service.CallRequestHistoryService;
import com.btc_store.service.ParameterService;
import com.btc_store.service.exception.interceptor.InterceptorException;
import com.btc_store.service.interceptor.BeforeSaveInterceptor;
import com.btc_store.service.interceptor.Interceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * CallRequest oluşturulduğunda otomatik olarak gruba atar
 */
@Slf4j
@RequiredArgsConstructor
@BeforeSaveInterceptor(itemType = CallRequestModel.class)
public class CallRequestAutoAssignInterceptor implements Interceptor<CallRequestModel> {
    
    private final ParameterService parameterService;
    private final CallRequestHistoryService callRequestHistoryService;
    private final RabbitTemplate rabbitTemplate;
    
    private static final String EMAIL_EXCHANGE = "email.exchange";
    private static final String EMAIL_ROUTING_KEY = "email.routing.key";
    
    @Override
    public void invoke(CallRequestModel model) throws InterceptorException {
        // Sadece yeni kayıtlar için çalış
        if (!isNew(model)) {
            return;
        }
        
        try {
            // Otomatik olarak gruba ata
            String callCenterGroup = parameterService.getValueByCode("call.center.group", model.getSite());
            if (callCenterGroup != null && !callCenterGroup.isEmpty()) {
                model.setAssignedGroup(callCenterGroup);
                model.setStatus(CallRequestStatus.ASSIGNED);
                
                log.info("Call request otomatik olarak gruba atandı: {}", callCenterGroup);
            }
        } catch (Exception e) {
            log.error("CallRequest otomatik atama sırasında hata: {}", e.getMessage(), e);
            throw new InterceptorException("CallRequest otomatik atama başarısız", e);
        }
    }
}
