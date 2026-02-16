package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.DocumentData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.DocumentFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.DOCUMENTS)
public class DocumentController {

    private final DocumentFacade documentFacade;

    @GetMapping
    @Operation(summary = "Get all documents")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('DocumentModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllDocuments(@Parameter(description = "IsoCode for validation message internalization")
                                               @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllDocuments of DocumentController.");
        var documents = documentFacade.getAllDocuments();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(documents);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get document by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('DocumentModel', @authorizationConstants.READ))")
    public ServiceResponseData getDocumentByCode(@Parameter(description = "Document Code") @PathVariable String code,
                                                 @Parameter(description = "IsoCode for validation message internalization")
                                                 @RequestParam(required = false) String isoCode) {
        log.info("Inside getDocumentByCode of DocumentController with code: {}", code);
        var document = documentFacade.getDocumentByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(document);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update document")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('DocumentModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveDocument(@Parameter(description = "Document data to save")
                                            @Validated @RequestPart(value = "documentData") DocumentData documentData,
                                            @Parameter(description = "Media files")
                                            @RequestPart(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles,
                                            @Parameter(description = "IsoCode for validation message internalization")
                                            @RequestParam(required = false) String isoCode) {
        log.info("Inside saveDocument of DocumentController.");
        var savedDocument = documentFacade.saveDocument(documentData, mediaFiles);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedDocument);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete document by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('DocumentModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteDocument(@Parameter(description = "Document Code") @PathVariable String code,
                                              @Parameter(description = "IsoCode for validation message internalization")
                                              @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteDocument of DocumentController with code: {}", code);
        documentFacade.deleteDocument(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
