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
        
        List<UserGroupData> userGroupDataList = userGroupFacade.getAllUserGroups();
        
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(userGroupDataList);
        return responseData;
    }
}
