package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.LanguageFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.LANGUAGES)
public class LanguageController {

    private final LanguageFacade languageFacade;

    @GetMapping
    @Operation(summary = "Get all languages")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('LanguageModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllLanguages(@Parameter(description = "IsoCode for validation message internalization") 
                                              @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllLanguages of LanguageController.");
        var languages = languageFacade.getAllLanguages();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(languages);
        return responseData;
    }
}
