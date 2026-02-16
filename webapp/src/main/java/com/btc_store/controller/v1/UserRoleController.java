package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.data.custom.role.UserRoleData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.UserRoleFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMappings.VERSION_V1 + "/userroles")
public class UserRoleController {

    private final UserRoleFacade userRoleFacade;

    @GetMapping
    @Operation(summary = "Get all user roles")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('UserRoleModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllUserRoles(@Parameter(description = "IsoCode for validation message internalization")
                                                @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllUserRoles of UserRoleController.");
        var userRoles = userRoleFacade.getAllUserRoles();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(userRoles);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get user role by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('UserRoleModel', @authorizationConstants.READ))")
    public ServiceResponseData getUserRoleByCode(@Parameter(description = "User Role Code") @PathVariable String code,
                                                  @Parameter(description = "IsoCode for validation message internalization")
                                                  @RequestParam(required = false) String isoCode) {
        log.info("Inside getUserRoleByCode of UserRoleController with code: {}", code);
        var userRole = userRoleFacade.getUserRoleByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(userRole);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update user role")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('UserRoleModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveUserRole(@Parameter(description = "User Role data to save")
                                             @Validated @RequestBody UserRoleData userRoleData,
                                             @Parameter(description = "IsoCode for validation message internalization")
                                             @RequestParam(required = false) String isoCode) {
        log.info("Inside saveUserRole of UserRoleController.");
        var savedUserRole = userRoleFacade.saveUserRole(userRoleData);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedUserRole);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete user role by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('UserRoleModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteUserRole(@Parameter(description = "User Role Code") @PathVariable String code,
                                               @Parameter(description = "IsoCode for validation message internalization")
                                               @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteUserRole of UserRoleController with code: {}", code);
        userRoleFacade.deleteUserRole(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
