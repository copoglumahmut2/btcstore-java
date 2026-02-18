package com.btc_store.service;

import com.btc_store.domain.model.custom.EmailTemplateModel;
import com.btc_store.domain.model.custom.SiteModel;

import java.util.List;

public interface EmailTemplateService {
    
    EmailTemplateModel getEmailTemplateByCode(String code, SiteModel siteModel);
    
    List<EmailTemplateModel> getEmailTemplatesBySite(SiteModel siteModel);
    
    List<EmailTemplateModel> getActiveEmailTemplatesBySite(SiteModel siteModel);
    
    EmailTemplateModel saveEmailTemplate(EmailTemplateModel emailTemplateModel);
    
    void deleteEmailTemplate(Long id);
    
    String processTemplate(String templateBody, java.util.Map<String, String> variables);
}
