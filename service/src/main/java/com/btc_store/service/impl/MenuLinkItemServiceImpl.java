package com.btc_store.service.impl;

import com.btc_store.domain.model.custom.MenuLinkItemModel;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.persistence.dao.MenuLinkItemDao;
import com.btc_store.service.MenuLinkItemService;
import com.btc_store.service.SiteService;
import com.btc_store.service.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuLinkItemServiceImpl implements MenuLinkItemService {

    private final MenuLinkItemDao menuDao;
    private final SiteService siteService;

    @Override
    public List<MenuLinkItemModel> getAllMenus() {
        SiteModel siteModel = siteService.getCurrentSite();
        Assert.notNull(siteModel, "Site must not be null");
        return menuDao.findBySiteOrderByDisplayOrderAsc(siteModel);
    }

    @Override
    public List<MenuLinkItemModel> getRootMenus() {
        SiteModel siteModel = siteService.getCurrentSite();
        Assert.notNull(siteModel, "Site must not be null");
        return menuDao.findByIsRootTrueAndSiteOrderByDisplayOrderAsc(siteModel);
    }

    @Override
    public List<MenuLinkItemModel> getMenusWithUserGroups() {
        SiteModel siteModel = siteService.getCurrentSite();
        Assert.notNull(siteModel, "Site must not be null");
        return menuDao.findAllWithUserGroupsBySite(siteModel);
    }

    @Override
    public MenuLinkItemModel getMenuByCode(String code) {
        SiteModel siteModel = siteService.getCurrentSite();
        Assert.notNull(code, "Code must not be null");
        Assert.notNull(siteModel, "Site must not be null");
        var menuModel = menuDao.findByCodeAndSite(code, siteModel).orElse(null);
        ServiceUtils.checkItemModelIsExist(menuModel, MenuLinkItemModel.class, siteModel, code);
        return menuModel;
    }

    @Override
    public MenuLinkItemModel saveMenu(MenuLinkItemModel menuModel) {
        Assert.notNull(menuModel, "Menu model must not be null");
        return menuDao.save(menuModel);
    }

    @Override
    public void deleteMenu(String code) {
        MenuLinkItemModel menuModel = getMenuByCode(code);
        Assert.notNull(menuModel, "Menu model must not be null");
        menuDao.delete(menuModel);
    }
}
