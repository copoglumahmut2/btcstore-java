package com.btc_store.facade;

import com.btc_store.domain.data.custom.MenuLinkItemData;

import java.util.List;

public interface MenuLinkItemFacade {

    List<MenuLinkItemData> getAllMenus();

    List<MenuLinkItemData> getRootMenus();

    List<MenuLinkItemData> getMenusByType(String menuType);

    List<MenuLinkItemData> getAllMenusByTypeFlat(String menuType);

    MenuLinkItemData getMenuByCode(String code);

    MenuLinkItemData saveMenu(MenuLinkItemData menuData);

    void deleteMenu(String code);
}
