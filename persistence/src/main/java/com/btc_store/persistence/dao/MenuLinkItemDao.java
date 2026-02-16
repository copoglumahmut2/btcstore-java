package com.btc_store.persistence.dao;

import com.btc_store.domain.enums.MenuType;
import com.btc_store.domain.model.custom.MenuLinkItemModel;
import com.btc_store.domain.model.custom.SiteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuLinkItemDao extends JpaRepository<MenuLinkItemModel, Long> {

    Optional<MenuLinkItemModel> findByCodeAndSite(String code, SiteModel site);

    List<MenuLinkItemModel> findBySiteOrderByDisplayOrderAsc(SiteModel site);

    List<MenuLinkItemModel> findByIsRootTrueAndSiteOrderByDisplayOrderAsc(SiteModel site);

    @Query("SELECT m FROM MenuLinkItemModel m WHERE m.menuType = :menuType AND m.site = :site ORDER BY m.displayOrder ASC")
    List<MenuLinkItemModel> findByMenuTypeAndSiteOrderByDisplayOrderAsc(@Param("menuType") MenuType menuType, @Param("site") SiteModel site);

    @Query("SELECT m FROM MenuLinkItemModel m LEFT JOIN FETCH m.userGroups WHERE m.site = :site ORDER BY m.displayOrder ASC")
    List<MenuLinkItemModel> findAllWithUserGroupsBySite(@Param("site") SiteModel site);

    @Query("SELECT m FROM MenuLinkItemModel m LEFT JOIN FETCH m.subMenuLinkItems WHERE m.isRoot = true AND m.site = :site ORDER BY m.displayOrder ASC")
    List<MenuLinkItemModel> findRootMenuLinkItemsWithSubMenuLinkItemsBySite(@Param("site") SiteModel site);
}
