package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.LegalDocumentData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.LegalDocumentFacade;
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
@RequestMapping(ControllerMappings.VERSION_V1 + "/legal-documents")
public class LegalDocumentController {

    private final LegalDocumentFacade legalDocumentFacade;

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get legal document by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('LegalDocumentModel', @authorizationConstants.READ))")
    public ServiceResponseData getLegalDocumentByCode(@Parameter(description = "Legal Document Code") @PathVariable String code,
                                                      @Parameter(description = "IsoCode for validation message internalization")
                                                      @RequestParam(required = false) String isoCode) {
        log.info("Inside getLegalDocumentByCode of LegalDocumentController with code: {}", code);
        var legalDocument = legalDocumentFacade.getLegalDocumentByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(legalDocument);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update legal document")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('LegalDocumentModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveLegalDocument(@Parameter(description = "Legal document data to save")
                                                 @Validated @RequestBody LegalDocumentData legalDocumentData,
                                                 @Parameter(description = "IsoCode for validation message internalization")
                                                 @RequestParam(required = false) String isoCode) {
        log.info("Inside saveLegalDocument of LegalDocumentController.");
        var savedLegalDocument = legalDocumentFacade.saveLegalDocument(legalDocumentData);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedLegalDocument);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete legal document by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('LegalDocumentModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteLegalDocument(@Parameter(description = "Legal Document Code") @PathVariable String code,
                                                   @Parameter(description = "IsoCode for validation message internalization")
                                                   @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteLegalDocument of LegalDocumentController with code: {}", code);
        legalDocumentFacade.deleteLegalDocument(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
