package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.data.custom.user.UserGroupData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.service.SiteService;
import com.btc_store.service.user.UserGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(ControllerMappings.VERSION_V1 + "/usergroups")
public class UserGroupController {

    private final UserGroupService userGroupService;
    private final SiteService siteService;
    private final ModelMapper modelMapper;

    @GetMapping
    @Operation(summary = "Get all user groups")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('UserGroupModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllUserGroups(@Parameter(description = "IsoCode for validation message internalization")
                                                @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllUserGroups of UserGroupController.");
        SiteModel siteModel = siteService.getCurrentSite();
        Set<UserGroupModel> userGroups = userGroupService.getUserGroupModels(siteModel);
        
        List<UserGroupData> userGroupDataList = userGroups.stream()
                .map(model -> modelMapper.map(model, UserGroupData.class))
                .collect(Collectors.toList());
        
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(userGroupDataList);
        return responseData;
    }
}
