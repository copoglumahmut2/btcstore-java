package com.btc_store.facade;

import com.btc_store.domain.data.custom.EmailTemplateData;

import java.util.List;

public interface EmailTemplateFacade {
    
    EmailTemplateData getEmailTemplateByCode(String code);
    
    List<EmailTemplateData> getAllEmailTemplates();
    
    List<EmailTemplateData> getActiveEmailTemplates();
    
    EmailTemplateData saveEmailTemplate(EmailTemplateData emailTemplateData);
    
    void deleteEmailTemplate(String code);
}
