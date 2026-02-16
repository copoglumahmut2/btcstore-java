package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.data.custom.user.UserGroupData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.UserGroupFacade;
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
@RequestMapping(ControllerMappings.VERSION_V1 + "/usergroups")
public class UserGroupController {

    private final UserGroupFacade userGroupFacade;

    @GetMapping
    @Operation(summary = "Get all user groups")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('UserGroupModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllUserGroups(@Parameter(description = "IsoCode for validation message internalization")
                                             @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllUserGroups of UserGroupController.");
        var userGroups = userGroupFacade.getAllUserGroups();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(userGroups);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get user group by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('UserGroupModel', @authorizationConstants.READ))")
    public ServiceResponseData getUserGroupByCode(@Parameter(description = "User Group Code") @PathVariable String code,
                                               @Parameter(description = "IsoCode for validation message internalization")
                                               @RequestParam(required = false) String isoCode) {
        log.info("Inside getUserGroupByCode of UserGroupController with code: {}", code);
        var userGroup = userGroupFacade.getUserGroupByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(userGroup);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update user group")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('UserGroupModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveUserGroup(@Parameter(description = "User Group data to save")
                                          @Validated @RequestPart(value = "userGroupData") UserGroupData userGroupData,
                                          @Parameter(description = "IsoCode for validation message internalization")
                                          @RequestParam(required = false) String isoCode) {
        log.info("Inside saveUserGroup of UserGroupController.");
        var savedUserGroup = userGroupFacade.saveUserGroup(userGroupData);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedUserGroup);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete user group by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('UserGroupModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteUserGroup(@Parameter(description = "User Group Code") @PathVariable String code,
                                            @Parameter(description = "IsoCode for validation message internalization")
                                            @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteUserGroup of UserGroupController with code: {}", code);
        userGroupFacade.deleteUserGroup(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
