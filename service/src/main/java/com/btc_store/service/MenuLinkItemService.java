package com.btc_store.service;

import com.btc_store.domain.model.custom.MenuLinkItemModel;

import java.util.List;

public interface MenuLinkItemService {

    List<MenuLinkItemModel> getAllMenus();

    List<MenuLinkItemModel> getRootMenus();

    List<MenuLinkItemModel> getMenusByType(String menuType);

    List<MenuLinkItemModel> getMenusWithUserGroups();

    MenuLinkItemModel getMenuByCode(String code);

    MenuLinkItemModel getMenuByCode(String code, com.btc_store.domain.model.custom.SiteModel siteModel);

    MenuLinkItemModel saveMenu(MenuLinkItemModel menuModel);

    void deleteMenu(String code);
}
