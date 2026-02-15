package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.SectorData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.SectorFacade;
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
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.SECTORS)
public class SectorController {

    private final SectorFacade sectorFacade;

    @GetMapping
    @Operation(summary = "Get all sectors")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('SectorModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllSectors(@Parameter(description = "IsoCode for validation message internalization") 
                                            @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllSectors of SectorController.");
        var sectors = sectorFacade.getAllSectors();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(sectors);
        return responseData;
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active sectors")
    public ServiceResponseData getActiveSectors(@Parameter(description = "IsoCode for validation message internalization") 
                                               @RequestParam(required = false) String isoCode) {
        log.info("Inside getActiveSectors of SectorController.");
        var sectors = sectorFacade.getActiveSectors();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(sectors);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get sector by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('SectorModel', @authorizationConstants.READ))")
    public ServiceResponseData getSectorByCode(@Parameter(description = "Sector Code") @PathVariable String code,
                                              @Parameter(description = "IsoCode for validation message internalization") 
                                              @RequestParam(required = false) String isoCode) {
        log.info("Inside getSectorByCode of SectorController with code: {}", code);
        var sector = sectorFacade.getSectorByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(sector);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update sector")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('SectorModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveSector(@Parameter(description = "Sector data to save") 
                                         @Validated @RequestPart(value = "sectorData") SectorData sectorData,
                                         @Parameter(description = "IsoCode for validation message internalization") 
                                         @RequestParam(required = false) String isoCode) {
        log.info("Inside saveSector of SectorController.");
        var savedSector = sectorFacade.saveSector(sectorData);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedSector);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete sector by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('SectorModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteSector(@Parameter(description = "Sector Code") @PathVariable String code,
                                           @Parameter(description = "IsoCode for validation message internalization") 
                                           @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteSector of SectorController with code: {}", code);
        sectorFacade.deleteSector(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
