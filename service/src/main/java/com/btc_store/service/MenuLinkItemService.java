package com.btc_store.service;

import com.btc_store.domain.model.custom.MenuLinkItemModel;

import java.util.List;

public interface MenuLinkItemService {

    List<MenuLinkItemModel> getAllMenus();

    List<MenuLinkItemModel> getRootMenus();

    List<MenuLinkItemModel> getMenusWithUserGroups();

    MenuLinkItemModel getMenuByCode(String code);

    MenuLinkItemModel saveMenu(MenuLinkItemModel menuModel);

    void deleteMenu(String code);
}
