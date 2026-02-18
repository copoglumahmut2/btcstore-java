package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.EmailTemplateData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.EmailTemplateFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.EMAIL_TEMPLATES)
public class EmailTemplateController {
    
    private final EmailTemplateFacade emailTemplateFacade;
    
    @GetMapping
    @Operation(summary = "Get all email templates")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('EmailTemplateModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllEmailTemplates(@Parameter(description = "IsoCode for validation message internalization")
                                                    @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllEmailTemplates of EmailTemplateController.");
        var templates = emailTemplateFacade.getAllEmailTemplates();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(templates);
        return responseData;
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active email templates")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('EmailTemplateModel', @authorizationConstants.READ))")
    public ServiceResponseData getActiveEmailTemplates(@Parameter(description = "IsoCode for validation message internalization")
                                                       @RequestParam(required = false) String isoCode) {
        log.info("Inside getActiveEmailTemplates of EmailTemplateController.");
        var templates = emailTemplateFacade.getActiveEmailTemplates();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(templates);
        return responseData;
    }
    
    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get email template by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('EmailTemplateModel', @authorizationConstants.READ))")
    public ServiceResponseData getEmailTemplateByCode(@Parameter(description = "Template Code") @PathVariable String code,
                                                      @Parameter(description = "IsoCode for validation message internalization")
                                                      @RequestParam(required = false) String isoCode) {
        log.info("Inside getEmailTemplateByCode of EmailTemplateController with code: {}", code);
        var template = emailTemplateFacade.getEmailTemplateByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(template);
        return responseData;
    }
    
    @PostMapping
    @Operation(summary = "Create or update email template")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('EmailTemplateModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveEmailTemplate(@Parameter(description = "Email template data to save")
                                                 @Validated @RequestBody EmailTemplateData emailTemplateData,
                                                 @Parameter(description = "IsoCode for validation message internalization")
                                                 @RequestParam(required = false) String isoCode) {
        log.info("Inside saveEmailTemplate of EmailTemplateController.");
        var savedTemplate = emailTemplateFacade.saveEmailTemplate(emailTemplateData);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedTemplate);
        return responseData;
    }
    
    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete email template by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('EmailTemplateModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteEmailTemplate(@Parameter(description = "Template Code") @PathVariable String code,
                                                   @Parameter(description = "IsoCode for validation message internalization")
                                                   @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteEmailTemplate of EmailTemplateController with code: {}", code);
        emailTemplateFacade.deleteEmailTemplate(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
