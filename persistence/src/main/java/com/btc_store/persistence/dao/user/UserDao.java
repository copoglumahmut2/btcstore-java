package com.btc_store.persistence.dao.user;

import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.user.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;


public interface UserDao extends JpaRepository<UserModel, Long> {

    UserModel getByUsernameIgnoreCaseAndSite(String username, SiteModel site);

    UserModel getByCodeIgnoreCaseAndSite(String code, SiteModel site);

    Page<UserModel> getBySiteOrderByLastModifiedDateDesc(Pageable pageable, SiteModel site);;

    Set<UserModel> getByActiveAndDeletedAndSite(Boolean active, Boolean deleted, SiteModel siteModel);

    boolean existsByUsernameAndSite(String username, SiteModel site);

    Set<UserModel> getUserModelByUsernameInAndSite(Set<String> usernames, SiteModel site);


}
