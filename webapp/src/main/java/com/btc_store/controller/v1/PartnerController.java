package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.PartnerData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.PartnerFacade;
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
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.PARTNERS)
public class PartnerController {

    private final PartnerFacade partnerFacade;

    @GetMapping
    @Operation(summary = "Get all partners ordered by display order")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('PartnerModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllPartners(@Parameter(description = "IsoCode for validation message internalization") 
                                              @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllPartners of PartnerController.");
        var partners = partnerFacade.getAllPartners();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(partners);
        return responseData;
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active partners ordered by display order")
    public ServiceResponseData getActivePartners(@Parameter(description = "IsoCode for validation message internalization") 
                                                 @RequestParam(required = false) String isoCode) {
        log.info("Inside getActivePartners of PartnerController.");
        var partners = partnerFacade.getActivePartners();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(partners);
        return responseData;
    }

    @GetMapping("/home")
    @Operation(summary = "Get partners to show on home page")
    public ServiceResponseData getHomePagePartners(@Parameter(description = "IsoCode for validation message internalization") 
                                                   @RequestParam(required = false) String isoCode) {
        log.info("Inside getHomePagePartners of PartnerController.");
        var partners = partnerFacade.getHomePagePartners();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(partners);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get partner by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('PartnerModel', @authorizationConstants.READ))")
    public ServiceResponseData getPartnerByCode(@Parameter(description = "Partner Code") @PathVariable String code,
                                                @Parameter(description = "IsoCode for validation message internalization") 
                                                @RequestParam(required = false) String isoCode) {
        log.info("Inside getPartnerByCode of PartnerController with code: {}", code);
        var partner = partnerFacade.getPartnerByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(partner);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update partner")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('PartnerModel', @authorizationConstants.SAVE))")
    public ServiceResponseData savePartner(@Parameter(description = "Partner data to save") 
                                           @Validated @RequestPart(value = "partnerData") PartnerData partnerData,
                                           @Parameter(description = "Partner media file") 
                                           @RequestPart(value = "media", required = false) MultipartFile mediaFile,
                                           @Parameter(description = "Remove existing media") 
                                           @RequestPart(value = "removeMedia", required = false) String removeMedia,
                                           @Parameter(description = "IsoCode for validation message internalization") 
                                           @RequestParam(required = false) String isoCode) {
        log.info("Inside savePartner of PartnerController.");
        boolean shouldRemoveMedia = "true".equals(removeMedia);
        var savedPartner = partnerFacade.savePartner(partnerData, mediaFile, shouldRemoveMedia);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedPartner);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete partner by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('PartnerModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deletePartner(@Parameter(description = "Partner Code") @PathVariable String code,
                                             @Parameter(description = "IsoCode for validation message internalization") 
                                             @RequestParam(required = false) String isoCode) {
        log.info("Inside deletePartner of PartnerController with code: {}", code);
        partnerFacade.deletePartner(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
