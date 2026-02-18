package com.btc_store.service.impl;

import com.btc_store.domain.model.custom.EmailTemplateModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.persistence.dao.EmailTemplateDao;
import com.btc_store.service.EmailTemplateService;
import com.btc_store.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailTemplateServiceImpl implements EmailTemplateService {
    
    private final EmailTemplateDao emailTemplateDao;
    
    @Override
    public EmailTemplateModel getEmailTemplateByCode(String code, SiteModel siteModel) {
        EmailTemplateModel template = emailTemplateDao.findByCodeAndSite(code, siteModel)
                .orElse(null);
        ServiceUtils.checkItemModelIsExist(template, EmailTemplateModel.class, siteModel, code);
        return template;
    }
    
    @Override
    public List<EmailTemplateModel> getEmailTemplatesBySite(SiteModel siteModel) {
        return emailTemplateDao.findBySite(siteModel);
    }
    
    @Override
    public List<EmailTemplateModel> getActiveEmailTemplatesBySite(SiteModel siteModel) {
        return emailTemplateDao.findBySiteAndActive(siteModel, true);
    }
    
    @Override
    @Transactional
    public EmailTemplateModel saveEmailTemplate(EmailTemplateModel emailTemplateModel) {
        return emailTemplateDao.save(emailTemplateModel);
    }
    
    @Override
    @Transactional
    public void deleteEmailTemplate(Long id) {
        emailTemplateDao.deleteById(id);
    }
    
    @Override
    public String processTemplate(String templateBody, Map<String, String> variables) {
        if (templateBody == null || variables == null) {
            return templateBody;
        }
        
        String result = templateBody;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}
