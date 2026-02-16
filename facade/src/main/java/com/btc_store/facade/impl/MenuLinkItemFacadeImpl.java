package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.MenuLinkItemData;
import com.btc_store.domain.model.custom.BannerModel;
import com.btc_store.domain.model.custom.MenuLinkItemModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.facade.MenuLinkItemFacade;
import com.btc_store.service.MenuLinkItemService;
import com.btc_store.service.ModelService;
import com.btc_store.service.SearchService;
import com.btc_store.service.SiteService;
import com.btc_store.service.user.UserGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
    private final SearchService searchService;

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
    public List<MenuLinkItemData> getMenusByType(String menuType) {
        var menuModels = menuService.getMenusByType(menuType);
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
    public MenuLinkItemData saveMenu(MenuLinkItemData menuLinkItemData) {
        var siteModel = siteService.getCurrentSite();
        MenuLinkItemModel menuLinkItemModel;

        if (menuLinkItemData.isNew()) {
            menuLinkItemModel = modelMapper.map(menuLinkItemData, MenuLinkItemModel.class);
            menuLinkItemModel.setCode(UUID.randomUUID().toString());
            menuLinkItemModel.setSite(siteModel);
        } else {
            menuLinkItemModel =  searchService.searchByCodeAndSite(MenuLinkItemModel.class, menuLinkItemData.getCode(), siteModel);
            modelMapper.map(menuLinkItemData, menuLinkItemModel);
        }

        // Handle parent menu
        if (StringUtils.hasText(menuLinkItemData.getParentMenuCode())) {
            var parentMenu = searchService.searchByCodeAndSite(MenuLinkItemModel.class, menuLinkItemData.getParentMenuCode(), siteModel);
            menuLinkItemModel.setParentMenuLinkItem(parentMenu);
            menuLinkItemModel.setIsRoot(false);
        } else {
            menuLinkItemModel.setParentMenuLinkItem(null);
            menuLinkItemModel.setIsRoot(true);
        }

        // Handle user groups
        Set<UserGroupModel> userGroups = new HashSet<>();
        if (CollectionUtils.isNotEmpty(menuLinkItemData.getUserGroups())) {
            menuLinkItemData.getUserGroups().forEach(ug ->
                    userGroups.add(searchService.searchByCodeAndSite(UserGroupModel.class, ug.getCode(), siteModel)));
        }
        menuLinkItemModel.setUserGroups(userGroups);

        var savedModel = modelService.save(menuLinkItemModel);
        return modelMapper.map(savedModel, MenuLinkItemData.class);
    }

    @Override
    public void deleteMenu(String code) {
        menuService.deleteMenu(code);
    }
}
