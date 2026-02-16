package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.MenuLinkItemData;
import com.btc_store.domain.model.custom.MenuLinkItemModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.facade.MenuLinkItemFacade;
import com.btc_store.service.MenuLinkItemService;
import com.btc_store.service.ModelService;
import com.btc_store.service.SiteService;
import com.btc_store.service.user.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuLinkItemFacadeImpl implements MenuLinkItemFacade {

    private final MenuLinkItemService menuService;
    private final SiteService siteService;
    private final UserGroupService userGroupService;
    private final ModelService modelService;
    private final ModelMapper modelMapper;

    @Override
    public List<MenuLinkItemData> getAllMenus() {
        var menuModels = menuService.getAllMenus();
        return menuModels.stream()
                .map(model -> modelMapper.map(model, MenuLinkItemData.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuLinkItemData> getRootMenus() {
        var menuModels = menuService.getRootMenus();
        return menuModels.stream()
                .map(model -> modelMapper.map(model, MenuLinkItemData.class))
                .collect(Collectors.toList());
    }

    @Override
    public MenuLinkItemData getMenuByCode(String code) {
        var menuModel = menuService.getMenuByCode(code);
        MenuLinkItemData menuData = modelMapper.map(menuModel, MenuLinkItemData.class);
        
        if (Objects.nonNull(menuModel.getParentMenuLinkItem())) {
            menuData.setParentMenuCode(menuModel.getParentMenuLinkItem().getCode());
        }
        
        return menuData;
    }

    @Override
    public MenuLinkItemData saveMenu(MenuLinkItemData menuData) {
        var siteModel = siteService.getCurrentSite();
        MenuLinkItemModel menuModel;

        if (menuData.isNew()) {
            menuModel = new MenuLinkItemModel();
            menuModel.setCode(UUID.randomUUID().toString());
            menuModel.setSite(siteModel);
        } else {
            menuModel = menuService.getMenuByCode(menuData.getCode());
        }

        // Handle parent menu
        if (StringUtils.hasText(menuData.getParentMenuCode())) {
            var parentMenu = menuService.getMenuByCode(menuData.getParentMenuCode());
            menuModel.setParentMenuLinkItem(parentMenu);
            menuModel.setIsRoot(false);
        } else {
            menuModel.setParentMenuLinkItem(null);
            menuModel.setIsRoot(true);
        }

        // Handle user groups
        if (Objects.nonNull(menuData.getUserGroups()) && !menuData.getUserGroups().isEmpty()) {
            Set<String> userGroupCodes = menuData.getUserGroups().stream()
                    .map(ug -> ug.getCode())
                    .collect(Collectors.toSet());
            Set<UserGroupModel> userGroups = userGroupService.getUserGroupModelsByCodeIn(userGroupCodes, siteModel);
            menuModel.setUserGroups(userGroups);
        } else {
            menuModel.setUserGroups(new HashSet<>());
        }

        var savedModel = modelService.save(menuModel);
        return modelMapper.map(savedModel, MenuLinkItemData.class);
    }

    @Override
    public void deleteMenu(String code) {
        menuService.deleteMenu(code);
    }
}
