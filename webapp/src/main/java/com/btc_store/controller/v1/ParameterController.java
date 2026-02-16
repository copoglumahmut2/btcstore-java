package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.ParameterData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.ParameterFacade;
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
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.PARAMETERS)
public class ParameterController {

    private final ParameterFacade parameterFacade;

    @GetMapping
    @Operation(summary = "Get all parameters")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('ParameterModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllParameters(@Parameter(description = "IsoCode for validation message internalization")
                                                @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllParameters of ParameterController.");
        var parameters = parameterFacade.getAllParameters();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(parameters);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get parameter by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('ParameterModel', @authorizationConstants.READ))")
    public ServiceResponseData getParameterByCode(@Parameter(description = "Parameter Code") @PathVariable String code,
                                                  @Parameter(description = "IsoCode for validation message internalization")
                                                  @RequestParam(required = false) String isoCode) {
        log.info("Inside getParameterByCode of ParameterController with code: {}", code);
        var parameter = parameterFacade.getParameterByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(parameter);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update parameter")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('ParameterModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveParameter(@Parameter(description = "Parameter data to save")
                                             @Validated @RequestBody ParameterData parameterData,
                                             @Parameter(description = "IsoCode for validation message internalization")
                                             @RequestParam(required = false) String isoCode) {
        log.info("Inside saveParameter of ParameterController.");
        var savedParameter = parameterFacade.saveParameter(parameterData);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedParameter);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete parameter by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('ParameterModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteParameter(@Parameter(description = "Parameter Code") @PathVariable String code,
                                               @Parameter(description = "IsoCode for validation message internalization")
                                               @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteParameter of ParameterController with code: {}", code);
        parameterFacade.deleteParameter(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
