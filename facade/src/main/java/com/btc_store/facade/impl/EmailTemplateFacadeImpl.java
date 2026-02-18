package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.EmailTemplateData;
import com.btc_store.domain.model.custom.EmailTemplateModel;
import com.btc_store.facade.EmailTemplateFacade;
import com.btc_store.service.EmailTemplateService;
import com.btc_store.service.ModelService;
import com.btc_store.service.SearchService;
import com.btc_store.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateFacadeImpl implements EmailTemplateFacade {
    
    private final EmailTemplateService emailTemplateService;
    private final SiteService siteService;
    private final ModelService modelService;
    private final SearchService searchService;
    private final ModelMapper modelMapper;
    
    @Override
    public EmailTemplateData getEmailTemplateByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var templateModel = emailTemplateService.getEmailTemplateByCode(code, siteModel);
        
        EmailTemplateData data = modelMapper.map(templateModel, EmailTemplateData.class);
        return data;
    }
    
    @Override
    public List<EmailTemplateData> getAllEmailTemplates() {
        var siteModel = siteService.getCurrentSite();
        var templateModels = emailTemplateService.getEmailTemplatesBySite(siteModel);
        return List.of(modelMapper.map(templateModels, EmailTemplateData[].class));
    }
    
    @Override
    public List<EmailTemplateData> getActiveEmailTemplates() {
        var siteModel = siteService.getCurrentSite();
        var templateModels = emailTemplateService.getActiveEmailTemplatesBySite(siteModel);
        return List.of(modelMapper.map(templateModels, EmailTemplateData[].class));
    }
    
    @Override
    public EmailTemplateData saveEmailTemplate(EmailTemplateData emailTemplateData) {
        var siteModel = siteService.getCurrentSite();
        EmailTemplateModel templateModel;
        boolean isNew = emailTemplateData.isNew();
        
        if (isNew) {
            templateModel = modelMapper.map(emailTemplateData, EmailTemplateModel.class);
            templateModel.setCode(UUID.randomUUID().toString());
            templateModel.setSite(siteModel);
        } else {
            templateModel = searchService.searchByCodeAndSite(
                    EmailTemplateModel.class, 
                    emailTemplateData.getCode(), 
                    siteModel
            );
            modelMapper.map(emailTemplateData, templateModel);
        }
        
        var savedModel = modelService.save(templateModel);
        return modelMapper.map(savedModel, EmailTemplateData.class);
    }
    
    @Override
    public void deleteEmailTemplate(String code) {
        var siteModel = siteService.getCurrentSite();
        var templateModel = searchService.searchByCodeAndSite(
                EmailTemplateModel.class, 
                code, 
                siteModel
        );
        modelService.remove(templateModel);
    }
}
