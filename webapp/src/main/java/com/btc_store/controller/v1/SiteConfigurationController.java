package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.SiteConfigurationData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.SiteConfigurationFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.SITE_CONFIGURATION)
public class SiteConfigurationController {

    private final SiteConfigurationFacade siteConfigurationFacade;

    @GetMapping
    @Operation(summary = "Get site configuration")
    public ServiceResponseData getSiteConfiguration(@Parameter(description = "IsoCode for validation message internalization") 
                                                    @RequestParam(required = false) String isoCode) {
        log.info("Inside getSiteConfiguration of SiteConfigurationController.");
        var config = siteConfigurationFacade.getSiteConfiguration();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(config);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Save site configuration")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('SiteConfigurationModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveSiteConfiguration(
            @Parameter(description = "Site configuration data to save") 
            @Validated @RequestPart(value = "configData") SiteConfigurationData configData,
            @Parameter(description = "Header logo file") 
            @RequestPart(value = "headerLogo", required = false) MultipartFile headerLogoFile,
            @Parameter(description = "Footer logo file") 
            @RequestPart(value = "footerLogo", required = false) MultipartFile footerLogoFile,
            @Parameter(description = "Remove existing header logo") 
            @RequestPart(value = "removeHeaderLogo", required = false) String removeHeaderLogo,
            @Parameter(description = "Remove existing footer logo") 
            @RequestPart(value = "removeFooterLogo", required = false) String removeFooterLogo,
            @Parameter(description = "IsoCode for validation message internalization") 
            @RequestParam(required = false) String isoCode) {
        log.info("Inside saveSiteConfiguration of SiteConfigurationController.");
        
        boolean shouldRemoveHeaderLogo = "true".equals(removeHeaderLogo);
        boolean shouldRemoveFooterLogo = "true".equals(removeFooterLogo);
        
        var savedConfig = siteConfigurationFacade.saveSiteConfiguration(
                configData, 
                headerLogoFile, 
                footerLogoFile,
                shouldRemoveHeaderLogo,
                shouldRemoveFooterLogo
        );
        
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedConfig);
        return responseData;
    }
}
