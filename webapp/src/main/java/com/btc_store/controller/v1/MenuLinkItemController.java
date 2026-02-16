package com.btc_store.controller.v1;

import com.btc_store.constants.ControllerMappings;
import com.btc_store.domain.data.custom.MenuLinkItemData;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.facade.MenuLinkItemFacade;
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
@RequestMapping(ControllerMappings.VERSION_V1 + ControllerMappings.MENU_LINK_ITEMS)
public class MenuLinkItemController {

    private final MenuLinkItemFacade menuFacade;

    @GetMapping
    @Operation(summary = "Get all menus ordered by display order")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('MenuModel', @authorizationConstants.READ))")
    public ServiceResponseData getAllMenus(@Parameter(description = "IsoCode for validation message internalization")
                                           @RequestParam(required = false) String isoCode) {
        log.info("Inside getAllMenus of MenuController.");
        var menus = menuFacade.getAllMenus();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(menus);
        return responseData;
    }

    @GetMapping("/root")
    @Operation(summary = "Get all root menus ordered by display order")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('MenuModel', @authorizationConstants.READ))")
    public ServiceResponseData getRootMenus(@Parameter(description = "IsoCode for validation message internalization")
                                            @RequestParam(required = false) String isoCode) {
        log.info("Inside getRootMenus of MenuController.");
        var menus = menuFacade.getRootMenus();
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(menus);
        return responseData;
    }

    @GetMapping(ControllerMappings.CODE)
    @Operation(summary = "Get menu by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('MenuModel', @authorizationConstants.READ))")
    public ServiceResponseData getMenuByCode(@Parameter(description = "Menu Code") @PathVariable String code,
                                             @Parameter(description = "IsoCode for validation message internalization")
                                             @RequestParam(required = false) String isoCode) {
        log.info("Inside getMenuByCode of MenuController with code: {}", code);
        var menu = menuFacade.getMenuByCode(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(menu);
        return responseData;
    }

    @PostMapping
    @Operation(summary = "Create or update menu")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('MenuModel', @authorizationConstants.SAVE))")
    public ServiceResponseData saveMenu(@Parameter(description = "Menu data to save")
                                        @Validated @RequestBody MenuLinkItemData menuData,
                                        @Parameter(description = "IsoCode for validation message internalization")
                                        @RequestParam(required = false) String isoCode) {
        log.info("Inside saveMenu of MenuController.");
        var savedMenu = menuFacade.saveMenu(menuData);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        responseData.setData(savedMenu);
        return responseData;
    }

    @DeleteMapping(ControllerMappings.CODE)
    @Operation(summary = "Delete menu by code")
    @PreAuthorize("hasAnyAuthority(@authorizationConstants.generateRoles('MenuModel', @authorizationConstants.DELETE))")
    public ServiceResponseData deleteMenu(@Parameter(description = "Menu Code") @PathVariable String code,
                                          @Parameter(description = "IsoCode for validation message internalization")
                                          @RequestParam(required = false) String isoCode) {
        log.info("Inside deleteMenu of MenuController with code: {}", code);
        menuFacade.deleteMenu(code);
        var responseData = new ServiceResponseData();
        responseData.setStatus(ProcessStatus.SUCCESS);
        return responseData;
    }
}
