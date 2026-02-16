package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.data.custom.user.UserData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.UserFacade;
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
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.USERS)
public class UserController {

    private final UserFacade userFacade;

    @GetMapping
    @Operation(summary = "Get all users")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('UserModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllUsers(@Parameter(description = "IsoCode for validation message internalization")
                                           @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllUsers of UserController.");
        var users = userFacade.getAllUsers();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(users);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get user by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('UserModel', @authorizationConstants.READ))")
    public ServiceResponseData getUserByCode(@Parameter(description = "User Code") @PathVariable String code,
                                             @Parameter(description = "IsoCode for validation message internalization")
                                             @RequestParam(required = false) String isoCode) {
        log.info("Inside getUserByCode of UserController with code: {}", code);
        var user = userFacade.getUserByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(user);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update user")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('UserModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveUser(@Parameter(description = "User data to save")
                                        @Validated @RequestBody UserData userData,
                                        @Parameter(description = "IsoCode for validation message internalization")
                                        @RequestParam(required = false) String isoCode) {
        log.info("Inside saveUser of UserController.");
        var savedUser = userFacade.saveUser(userData);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedUser);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete user by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('UserModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteUser(@Parameter(description = "User Code") @PathVariable String code,
                                          @Parameter(description = "IsoCode for validation message internalization")
                                          @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteUser of UserController with code: {}", code);
        userFacade.deleteUser(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
