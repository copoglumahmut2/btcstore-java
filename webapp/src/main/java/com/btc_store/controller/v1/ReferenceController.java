package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.ReferenceData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.ReferenceFacade;
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
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.REFERENCES)
public class ReferenceController {

    private final ReferenceFacade referenceFacade;

    @GetMapping
    @Operation(summary = "Get all references ordered by display order")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('ReferenceModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllReferences(@Parameter(description = "IsoCode for validation message internalization") 
                                                @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllReferences of ReferenceController.");
        var references = referenceFacade.getAllReferences();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(references);
        return responseData;
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active references ordered by display order")
    public ServiceResponseData getActiveReferences(@Parameter(description = "IsoCode for validation message internalization") 
                                                   @RequestParam(required = false) String isoCode) {
        log.info("Inside getActiveReferences of ReferenceController.");
        var references = referenceFacade.getActiveReferences();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(references);
        return responseData;
    }

    @GetMapping("/home")
    @Operation(summary = "Get references to show on home page")
    public ServiceResponseData getHomePageReferences(@Parameter(description = "IsoCode for validation message internalization") 
                                                     @RequestParam(required = false) String isoCode) {
        log.info("Inside getHomePageReferences of ReferenceController.");
        var references = referenceFacade.getHomePageReferences();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(references);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get reference by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('ReferenceModel', @authorizationConstants.READ))")
    public ServiceResponseData getReferenceByCode(@Parameter(description = "Reference Code") @PathVariable String code,
                                                  @Parameter(description = "IsoCode for validation message internalization") 
                                                  @RequestParam(required = false) String isoCode) {
        log.info("Inside getReferenceByCode of ReferenceController with code: {}", code);
        var reference = referenceFacade.getReferenceByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(reference);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update reference")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('ReferenceModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveReference(@Parameter(description = "Reference data to save") 
                                             @Validated @RequestPart(value = "referenceData") ReferenceData referenceData,
                                             @Parameter(description = "Reference media file") 
                                             @RequestPart(value = "media", required = false) MultipartFile mediaFile,
                                             @Parameter(description = "Remove existing media") 
                                             @RequestPart(value = "removeMedia", required = false) String removeMedia,
                                             @Parameter(description = "IsoCode for validation message internalization") 
                                             @RequestParam(required = false) String isoCode) {
        log.info("Inside saveReference of ReferenceController.");
        boolean shouldRemoveMedia = "true".equals(removeMedia);
        var savedReference = referenceFacade.saveReference(referenceData, mediaFile, shouldRemoveMedia);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedReference);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete reference by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('ReferenceModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteReference(@Parameter(description = "Reference Code") @PathVariable String code,
                                               @Parameter(description = "IsoCode for validation message internalization") 
                                               @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteReference of ReferenceController with code: {}", code);
        referenceFacade.deleteReference(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
