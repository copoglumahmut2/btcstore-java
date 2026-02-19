package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.DashboardModuleData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.DashboardModuleType;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.DashboardModuleFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMappings.VERSION_V1 + "/dashboard")
@Tag(name = "Dashboard", description = "Dashboard API")
public class DashboardController {

    private final DashboardModuleFacade dashboardModuleFacade;

    @GetMapping("/modules")
    @Operation(summary = "Get authorized dashboard modules for current user")
    public ResponseEntity<ServiceResponseData> getAuthorizedModules(
            @Parameter(description = "IsoCode for validation message internalization")
            @RequestParam(required = false) String isoCode) {
        log.info("Inside getAuthorizedModules of DashboardController.");
        var modules = dashboardModuleFacade.getAuthorizedDashboardModules();

        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(modules);
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/modules/by-type/{moduleType}")
    @Operation(summary = "Get authorized dashboard modules by type for current user")
    public ResponseEntity<ServiceResponseData> getAuthorizedModulesByType(
            @PathVariable DashboardModuleType moduleType,
            @Parameter(description = "IsoCode for validation message internalization")
            @RequestParam(required = false) String isoCode) {
        log.info("Inside getAuthorizedModulesByType of DashboardController with type: {}", moduleType);
        var modules = dashboardModuleFacade.getAuthorizedDashboardModulesByType(moduleType);

        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(modules);
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/modules/by-type/{moduleType}/with-counts")
    @Operation(summary = "Get authorized dashboard modules by type with counts for current user")
    public ResponseEntity<ServiceResponseData> getAuthorizedModulesByTypeWithCounts(
            @PathVariable DashboardModuleType moduleType,
            @Parameter(description = "IsoCode for validation message internalization")
            @RequestParam(required = false) String isoCode) {
        log.info("Inside getAuthorizedModulesByTypeWithCounts of DashboardController with type: {}", moduleType);
        var modules = dashboardModuleFacade.getAuthorizedDashboardModulesWithCounts(moduleType);

        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(modules);
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/modules/all")
    @Operation(summary = "Get all dashboard modules (Admin only)")
    public ResponseEntity<ServiceResponseData> getAllModules(
            @Parameter(description = "IsoCode for validation message internalization")
            @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllModules of DashboardController.");
        var modules = dashboardModuleFacade.getAllDashboardModules();

        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(modules);
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/modules/active")
    @Operation(summary = "Get active dashboard modules")
    public ResponseEntity<ServiceResponseData> getActiveModules(
            @Parameter(description = "IsoCode for validation message internalization")
            @RequestParam(required = false) String isoCode) {
        log.info("Inside getActiveModules of DashboardController.");
        var modules = dashboardModuleFacade.getActiveDashboardModules();

        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(modules);
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/modules" + ControllerMappings.CODE)
    @Operation(summary = "Get dashboard module by code")
    public ResponseEntity<ServiceResponseData> getModuleByCode(
            @PathVariable String code,
            @Parameter(description = "IsoCode for validation message internalization")
            @RequestParam(required = false) String isoCode) {
        log.info("Inside getModuleByCode of DashboardController with code: {}", code);
        var module = dashboardModuleFacade.getDashboardModuleByCode(code);

        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(module);
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/modules")
    @Operation(summary = "Save dashboard module")
    public ResponseEntity<ServiceResponseData> saveModule(
            @RequestBody DashboardModuleData dashboardModuleData,
            @Parameter(description = "IsoCode for validation message internalization")
            @RequestParam(required = false) String isoCode) {
        log.info("Inside saveModule of DashboardController.");
        var savedModule = dashboardModuleFacade.saveDashboardModule(dashboardModuleData);

        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedModule);
        return ResponseEntity.ok(responseData);
    }

    @DeleteMapping("/modules" + ControllerMappings.CODE)
    @Operation(summary = "Delete dashboard module")
    public ResponseEntity<ServiceResponseData> deleteModule(
            @PathVariable String code,
            @Parameter(description = "IsoCode for validation message internalization")
            @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteModule of DashboardController with code: {}", code);
        dashboardModuleFacade.deleteDashboardModule(code);

        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return ResponseEntity.ok(responseData);
    }
}
